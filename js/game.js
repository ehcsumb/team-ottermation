/* 
 * Typing Runner (Title pending)
 *
 * Top-down endless runner on an HTML5 canvas. The animal runs up a
 * scrolling path. Each turn (blue word) must be typed before the curve
 * or you lose a heart. Each obstacle (orange word) must be
 * typed or you lose a heart. Once you miss 3, game over.
 *
 * If api.js is missing or the API fails, a local word list is used.
 */
(function () {
  "use strict";

  const canvas = document.getElementById("gameCanvas");
  if (!canvas) return;
  const ctx = canvas.getContext("2d");

  // Currently not implemented.  Needs a menu to choose which animal, biome and difficulty.  Can be saved in a database of player profiles so it rememebers last used.
  // Settings from the player's profile (data attributes)
  const settings = {
    animal: canvas.dataset.animal || "jackrabbit",
    biome: canvas.dataset.biome || "desert",
    difficulty: canvas.dataset.difficulty || "normal",
    sound: canvas.dataset.sound === "1" || canvas.dataset.sound === "true"
  };

  const DIFFICULTY = {
    easy:   { startSpeed: 110, accel: 2.0, maxSpeed: 260 },
    normal: { startSpeed: 140, accel: 3.0, maxSpeed: 340 },
    hard:   { startSpeed: 180, accel: 4.5, maxSpeed: 420 }
  };
  const diff = DIFFICULTY[settings.difficulty] || DIFFICULTY.normal;

  const BIOMES = {
    desert: { ground: "#e9c98b", groundDot: "#d8b170", path: "#c8955a", edge: "#a8743f", obstacles: ["cactus", "rock"] },
    forest: { ground: "#5e9e4f", groundDot: "#4c8740", path: "#9c7b52", edge: "#6f5434", obstacles: ["log", "rock"] },
    snow:   { ground: "#eef4f8", groundDot: "#d5e2ea", path: "#b9cbd8", edge: "#8ea5b5", obstacles: ["rock", "log"] }
  };
  const biome = BIOMES[settings.biome] || BIOMES.desert;

  const ANIMALS = {
    jackrabbit: { body: "#b08a62", belly: "#e6d3b8", ear: "#d9a6a0", earLen: 16 },
    fox:        { body: "#d9682b", belly: "#f6e7d6", ear: "#3b2a20", earLen: 9 },
    tortoise:   { body: "#5f7f3c", belly: "#c9b37a", ear: "#5f7f3c", earLen: 0 }
  };
  const animalLook = ANIMALS[settings.animal] || ANIMALS.jackrabbit;

  const COLORS = { turn: "#2f7de1", jump: "#f07c1c", typed: "#ffffff", text: "#1d2330" };

// local word list if fetch fails.
  const FALLBACK_WORDS = [
    "sand", "dune", "mesa", "sun", "heat", "trail", "dust", "hop", "dash", "sprint",
    "canyon", "sage", "yucca", "coyote", "lizard", "hawk", "quail", "boulder", "ridge",
    "arroyo", "mirage", "oasis", "breeze", "shadow", "pebble", "thorn", "burrow", "dawn",
    "dusk", "wind", "storm", "river", "stone", "cliff", "valley", "meadow", "pine",
    "maple", "frost", "glacier", "comet", "rocket", "jungle", "puzzle", "garden",
    "silver", "orange", "violet", "rapid", "swift", "brave", "quick", "clever", "nimble"
  ];

  // Layout constants
  const W = canvas.width;
  const H = canvas.height;
  const ANIMAL_Y = H - 130;        // animal's fixed screen y
  const PATH_W = 120;              // path width in px
  const TURN_SHIFT = 110;          // how far one turn moves the path sideways
  const CURVE_LEN = 220;           // world px a curve takes
  const MAX_OFFSET = W / 2 - PATH_W / 2 - 20;
  const JUMP_LEN = 130;            // world px a jump lasts
  const PX_PER_METER = 10;

  // Sharp rendering on high-DPI screens.
  const dpr = window.devicePixelRatio || 1;
  canvas.style.width = W + "px";
  canvas.style.height = H + "px";
  canvas.width = W * dpr;
  canvas.height = H * dpr;
  ctx.scale(dpr, dpr);

  // Game state
  let state = "loading";           // loading | ready | running | paused | over
  let wordPool = FALLBACK_WORDS.slice();
  let g;                           // per-run state, created by resetRun()
  let lastTime = 0;
  let saveStatus = "";

  function resetRun() {
    g = {
      dist: 0,                     // world distance travelled (px)
      speed: diff.startSpeed,      // px per second
      baseOffset: 0,               // path center offset from finished turns
      events: [],                  // upcoming turns & obstacles
      nextEventY: 420,
      target: null,                // event currently being typed
      typed: "",
      lives: 3,
      invuln: 0,                   // seconds of crash protection
      flash: 0,
      jump: null,                  // { start } world distance when jump started
      correctKeys: 0,
      wrongKeys: 0,
      wordsCompleted: 0,
      obstaclesCleared: 0,
      turnsMade: 0,
      cleanStreak: 0,
      bonus: 0,
      elapsed: 0,
      runFrame: 0,
      decor: makeDecor()
    };
    saveStatus = "";
  }

  function makeDecor() {
    const dots = [];
    for (let i = 0; i < 60; i++) {
      dots.push({ x: Math.random() * W, y: Math.random() * 2000, r: 1 + Math.random() * 3 });
    }
    return dots;
  }

  // Words 
  function pickWord() {
    // Shorter words early on, longer as the run goes.
    const maxLen = Math.min(4 + Math.floor(g.dist / 1500), 10);
    const usedLetters = g.events.filter(e => !e.done).map(e => e.word[0]);
    let candidates = wordPool.filter(w => w.length <= maxLen && !usedLetters.includes(w[0]));
    if (candidates.length === 0) candidates = wordPool;
    return candidates[Math.floor(Math.random() * candidates.length)];
  }

  function cleanWords(list) {
    return list
      .map(w => String(w).toLowerCase().trim())
      .filter(w => /^[a-z]{2,10}$/.test(w));
  }

  // add fetchWords() to the api.js file then a word table in app.js and that can be a database
  async function loadWords() {
    try {
      if (window.GameApi && typeof window.GameApi.fetchWords === "function") {
        const words = cleanWords(await window.GameApi.fetchWords());
        if (words.length >= 20) wordPool = words;
      }
    } catch (err) {
      console.warn("Words API failed, using local word list.", err);
    }
    state = "ready";
  }

  // Path geometry 
  function smoothstep(t) {
    t = Math.max(0, Math.min(1, t));
    return t * t * (3 - 2 * t);
  }

  // Path center offset (from canvas center) at a world y.
  function pathOffsetAt(wy, onlyCommitted) {
    let off = g.baseOffset;
    for (const e of g.events) {
      if (e.type !== "turn") continue;
      if (onlyCommitted && !e.committed) continue;
      off += e.dir * TURN_SHIFT * smoothstep((wy - e.y) / CURVE_LEN);
    }
    return off;
  }

  function worldToScreenY(wy) {
    return ANIMAL_Y - (wy - g.dist);
  }

  function projectedOffset() {
    // Where the path will end up after every queued turn finishes.
    let off = g.baseOffset;
    for (const e of g.events) if (e.type === "turn") off += e.dir * TURN_SHIFT;
    return off;
  }

  // Event spawning 
  function spawnEvents() {
    while (g.nextEventY < g.dist + H + 200) {
      const y = g.nextEventY;
      const word = pickWord();
      let gap;
      if (Math.random() < 0.55) {
        // Turn: pick a direction that keeps the path on screen.
        const proj = projectedOffset();
        let dir = Math.random() < 0.5 ? -1 : 1;
        if (proj + dir * TURN_SHIFT > MAX_OFFSET || proj + dir * TURN_SHIFT < -MAX_OFFSET) dir = -dir;
        g.events.push({ type: "turn", y, dir, word, committed: false, done: false });
        gap = CURVE_LEN + 140 + Math.random() * 140;
      } else {
        const kind = biome.obstacles[Math.floor(Math.random() * biome.obstacles.length)];
        g.events.push({ type: "jump", y, kind, word, cleared: false, hit: false, done: false });
        gap = 240 + Math.random() * 160;
      }
      g.nextEventY = y + gap;
    }
  }

  // Typing 
  function currentTarget() {
    if (g.target && !g.target.done) return g.target;
    // Nearest unfinished event ahead of the animal.
    let best = null;
    for (const e of g.events) {
      if (e.done || e.y < g.dist - 40) continue;
      if (worldToScreenY(e.y) < -20) continue; // not on screen yet
      if (!best || e.y < best.y) best = e;
    }
    return best;
  }

  function handleKey(ch) {
    if (!g.target || g.target.done) {
      // Pick target by first letter (ZType style), nearest first.
      const options = g.events
        .filter(e => !e.done && e.y >= g.dist - 40 && worldToScreenY(e.y) > -20 && e.word[0] === ch)
        .sort((a, b) => a.y - b.y);
      if (options.length === 0) {
        registerMiss();
        return;
      }
      g.target = options[0];
      g.typed = "";
    }

    const word = g.target.word;
    if (word[g.typed.length] === ch) {
      g.typed += ch;
      g.correctKeys++;
      beep(660, 0.03);
      if (g.typed === word) completeTarget();
    } else {
      registerMiss();
    }
  }

  function registerMiss() {
    g.wrongKeys++;
    g.cleanStreak = 0;
    if (g.target) g.target.missed = true;
    beep(160, 0.06);
  }

  function completeTarget() {
    const e = g.target;
    e.done = true;
    g.wordsCompleted++;
    if (!e.missed) {
      g.cleanStreak++;
      g.bonus += 5 * Math.min(g.cleanStreak, 10); // clean-typing bonus
    }
    if (e.type === "turn") {
      e.committed = true;
      g.turnsMade++;
    } else {
      e.cleared = true;
    }
    g.target = null;
    g.typed = "";
    beep(880, 0.08);
  }

  // Crashes
  function crash(e) {
    e.done = true;
    if (e.type === "turn") e.committed = true; // snap back onto the path
    else e.hit = true;
    if (g.target === e) { g.target = null; g.typed = ""; }
    g.cleanStreak = 0;
    if (g.invuln > 0) return;
    g.lives--;
    g.invuln = 1.2;
    g.flash = 0.35;
    beep(110, 0.25);
    if (g.lives <= 0) endRun();
  }

  // Update
  function update(dt) {
    g.elapsed += dt;
    g.speed = Math.min(diff.maxSpeed, g.speed + diff.accel * dt);
    g.dist += g.speed * dt;
    g.runFrame += g.speed * dt / 18;
    g.invuln = Math.max(0, g.invuln - dt);
    g.flash = Math.max(0, g.flash - dt);

    spawnEvents();

    // Turns: if the animal hasn't committed, it drifts off the path.
    const drift = pathOffsetAt(g.dist, false) - pathOffsetAt(g.dist, true);
    if (Math.abs(drift) > PATH_W / 2 - 14) {
      const missed = g.events.find(e => e.type === "turn" && !e.committed && g.dist >= e.y);
      if (missed) crash(missed);
    }

    // Obstacles: jump if cleared, crash if not.
    for (const e of g.events) {
      if (e.type !== "jump" || e.hit || e.jumped) continue;
      if (e.cleared && g.dist >= e.y - JUMP_LEN / 2) {
        e.jumped = true;
        g.jump = { start: g.dist };
        g.obstaclesCleared++;
        beep(520, 0.1);
      } else if (!e.cleared && g.dist >= e.y - 12) {
        crash(e);
      }
    }
    if (g.jump && g.dist - g.jump.start > JUMP_LEN) g.jump = null;

    // Fold finished turns into baseOffset and drop old events.
    g.events = g.events.filter(e => {
      if (e.type === "turn" && g.dist > e.y + CURVE_LEN) {
        g.baseOffset += e.dir * TURN_SHIFT;
        return false;
      }
      if (e.type === "jump" && g.dist > e.y + 120) return false;
      return true;
    });
  }

  // Stats 
  function getStats() {
    const minutes = Math.max(g.elapsed / 60, 1 / 60);
    const totalKeys = g.correctKeys + g.wrongKeys;
    const distance = Math.floor(g.dist / PX_PER_METER);
    return {
      distance,
      wpm: Math.round((g.correctKeys / 5) / minutes),
      accuracy: totalKeys ? Math.round((g.correctKeys / totalKeys) * 1000) / 10 : 100,
      obstacles_cleared: g.obstaclesCleared,
      turns_made: g.turnsMade,
      score: distance + g.bonus
    };
  }

  async function endRun() {
    state = "over";
    const stats = getStats();
    window.dispatchEvent(new CustomEvent("game:over", { detail: stats }));
    if (window.GameApi && typeof window.GameApi.saveRun === "function") {
      saveStatus = "Saving run...";
      try {
        await window.GameApi.saveRun(stats);
        saveStatus = "Run saved!";
      } catch (err) {
        console.error(err);
        saveStatus = "Could not save run.";
      }
    }
  }

  // Drawing 
  function draw() {
    ctx.fillStyle = biome.ground;
    ctx.fillRect(0, 0, W, H);
    if (!g) return;

    drawGroundDecor();
    drawPath();
    drawObstacles();
    drawAnimal();
    drawWordLabels();
    drawHud();

    if (g.flash > 0) {
      ctx.fillStyle = `rgba(220, 40, 40, ${g.flash})`;
      ctx.fillRect(0, 0, W, H);
    }
    if (state !== "running") drawOverlay();
  }

  function drawGroundDecor() {
    ctx.fillStyle = biome.groundDot;
    for (const d of g.decor) {
      const sy = ((d.y + g.dist) % 2000) - 2000 + H + 400;
      if (sy < -10 || sy > H + 10) continue;
      ctx.beginPath();
      ctx.arc(d.x, sy, d.r, 0, Math.PI * 2);
      ctx.fill();
    }
  }

  function drawPath() {
    const left = [], right = [];
    for (let sy = -20; sy <= H + 20; sy += 8) {
      const wy = g.dist + (ANIMAL_Y - sy);
      const cx = W / 2 + pathOffsetAt(wy, false);
      left.push([cx - PATH_W / 2, sy]);
      right.push([cx + PATH_W / 2, sy]);
    }
    ctx.beginPath();
    ctx.moveTo(left[0][0], left[0][1]);
    for (const p of left) ctx.lineTo(p[0], p[1]);
    for (let i = right.length - 1; i >= 0; i--) ctx.lineTo(right[i][0], right[i][1]);
    ctx.closePath();
    ctx.fillStyle = biome.path;
    ctx.fill();
    ctx.lineWidth = 4;
    ctx.strokeStyle = biome.edge;
    ctx.stroke();
  }

  function drawObstacles() {
    for (const e of g.events) {
      if (e.type !== "jump" || e.hit) continue;
      const sy = worldToScreenY(e.y);
      if (sy < -40 || sy > H + 40) continue;
      const sx = W / 2 + pathOffsetAt(e.y, false);
      drawObstacle(e.kind, sx, sy);
    }
  }

  function drawObstacle(kind, x, y) {
    ctx.save();
    ctx.translate(x, y);
    ctx.fillStyle = "rgba(0,0,0,0.18)";
    ctx.beginPath();
    ctx.ellipse(4, 6, 24, 9, 0, 0, Math.PI * 2);
    ctx.fill();
    if (kind === "cactus") {
      ctx.fillStyle = "#3f8f45";
      ctx.beginPath(); ctx.arc(0, 0, 15, 0, Math.PI * 2); ctx.fill();
      ctx.beginPath(); ctx.arc(-16, -4, 7, 0, Math.PI * 2); ctx.fill();
      ctx.beginPath(); ctx.arc(16, 3, 7, 0, Math.PI * 2); ctx.fill();
      ctx.fillStyle = "#2d6b33";
      ctx.beginPath(); ctx.arc(0, 0, 6, 0, Math.PI * 2); ctx.fill();
    } else if (kind === "log") {
      ctx.fillStyle = "#7a5230";
      ctx.fillRect(-34, -9, 68, 18);
      ctx.fillStyle = "#c79a64";
      ctx.beginPath(); ctx.ellipse(34, 0, 6, 9, 0, 0, Math.PI * 2); ctx.fill();
    } else {
      ctx.fillStyle = "#8a8f96";
      ctx.beginPath(); ctx.ellipse(0, 0, 20, 14, 0.3, 0, Math.PI * 2); ctx.fill();
      ctx.fillStyle = "#a9aeb5";
      ctx.beginPath(); ctx.ellipse(-5, -4, 9, 6, 0.3, 0, Math.PI * 2); ctx.fill();
    }
    ctx.restore();
  }

  function drawAnimal() {
    const x = W / 2 + pathOffsetAt(g.dist, true);
    let lift = 0;
    if (g.jump) lift = Math.sin(Math.PI * (g.dist - g.jump.start) / JUMP_LEN);
    const scale = 1 + 0.45 * lift;
    const blink = g.invuln > 0 && Math.floor(g.invuln * 10) % 2 === 0;

    // Shadow stays on the ground and shrinks while airborne.
    ctx.fillStyle = "rgba(0,0,0,0.25)";
    ctx.beginPath();
    ctx.ellipse(x, ANIMAL_Y + 14, 16 * (1 - 0.35 * lift), 7 * (1 - 0.35 * lift), 0, 0, Math.PI * 2);
    ctx.fill();
    if (blink) return;

    // Tilt slightly toward the direction of travel.
    const ahead = pathOffsetAt(g.dist + 30, true) - pathOffsetAt(g.dist, true);
    ctx.save();
    ctx.translate(x, ANIMAL_Y - lift * 18);
    ctx.rotate(Math.atan2(ahead, 30));
    ctx.scale(scale, scale);

    const legSwing = g.jump ? 0 : Math.sin(g.runFrame) * 6;
    const a = animalLook;

    // Legs (run cycle)
    ctx.fillStyle = a.body;
    ctx.beginPath(); ctx.ellipse(-9, 8 + legSwing, 4, 7, 0, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.ellipse(9, 8 - legSwing, 4, 7, 0, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.ellipse(-8, -6 - legSwing, 3, 5, 0, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.ellipse(8, -6 + legSwing, 3, 5, 0, 0, Math.PI * 2); ctx.fill();

    // Body
    if (settings.animal === "tortoise") {
      ctx.fillStyle = a.belly;
      ctx.beginPath(); ctx.ellipse(0, 0, 14, 17, 0, 0, Math.PI * 2); ctx.fill();
      ctx.strokeStyle = a.body; ctx.lineWidth = 2; ctx.stroke();
    } else {
      ctx.fillStyle = a.body;
      ctx.beginPath(); ctx.ellipse(0, 2, 11, 16, 0, 0, Math.PI * 2); ctx.fill();
      ctx.fillStyle = a.belly;
      ctx.beginPath(); ctx.arc(0, 16, 5, 0, Math.PI * 2); ctx.fill(); // tail
    }

    // Head
    ctx.fillStyle = a.body;
    ctx.beginPath(); ctx.arc(0, -17, 8, 0, Math.PI * 2); ctx.fill();
    if (a.earLen > 0) {
      ctx.fillStyle = a.ear;
      ctx.beginPath(); ctx.ellipse(-4, -24 - a.earLen / 2, 3, a.earLen / 2, -0.15, 0, Math.PI * 2); ctx.fill();
      ctx.beginPath(); ctx.ellipse(4, -24 - a.earLen / 2, 3, a.earLen / 2, 0.15, 0, Math.PI * 2); ctx.fill();
    }
    ctx.fillStyle = "#111";
    ctx.beginPath(); ctx.arc(-3, -20, 1.5, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.arc(3, -20, 1.5, 0, Math.PI * 2); ctx.fill();
    ctx.restore();
  }

  function drawWordLabels() {
    const target = currentTarget();
    ctx.font = "bold 18px 'Courier New', monospace";
    ctx.textBaseline = "middle";
    for (const e of g.events) {
      if (e.done) continue;
      const sy = worldToScreenY(e.y);
      if (sy < -20 || sy > H) continue;
      const cx = W / 2 + pathOffsetAt(e.y, false);
      let lx, ly;
      if (e.type === "turn") {
        lx = cx + e.dir * (PATH_W / 2 + 10);
        ly = sy - 10;
      } else {
        lx = cx;
        ly = sy - 34;
      }
      const isTarget = e === g.target || (!g.target && e === target);
      const typed = e === g.target ? g.typed : "";
      drawWordTag(e, lx, ly, typed, isTarget);
    }
  }

  function drawWordTag(e, x, y, typed, isTarget) {
    const color = e.type === "turn" ? COLORS.turn : COLORS.jump;
    const arrow = e.type === "turn" ? (e.dir < 0 ? "\u2190 " : " \u2192") : "";
    const label = e.type === "turn" && e.dir < 0 ? arrow + e.word : e.word + arrow;
    const w = ctx.measureText(label).width + 16;
    let bx = x - w / 2;
    if (e.type === "turn") bx = e.dir < 0 ? x - w : x;
    bx = Math.max(4, Math.min(W - w - 4, bx));

    ctx.fillStyle = color;
    ctx.globalAlpha = isTarget ? 1 : 0.85;
    roundRect(bx, y - 14, w, 28, 8);
    ctx.fill();
    ctx.globalAlpha = 1;
    if (isTarget) {
      ctx.lineWidth = 3;
      ctx.strokeStyle = "#ffffff";
      ctx.stroke();
    }

    // Typed letters bright, remaining letters dimmed.
    let tx = bx + 8;
    const prefix = e.type === "turn" && e.dir < 0 ? arrow : "";
    ctx.fillStyle = "#ffffff";
    ctx.fillText(prefix, tx, y);
    tx += ctx.measureText(prefix).width;
    ctx.fillStyle = "#ffe066";
    ctx.fillText(typed, tx, y);
    tx += ctx.measureText(typed).width;
    ctx.fillStyle = "#ffffff";
    ctx.fillText(e.word.slice(typed.length) + (prefix ? "" : arrow), tx, y);
  }

  function roundRect(x, y, w, h, r) {
    ctx.beginPath();
    ctx.moveTo(x + r, y);
    ctx.arcTo(x + w, y, x + w, y + h, r);
    ctx.arcTo(x + w, y + h, x, y + h, r);
    ctx.arcTo(x, y + h, x, y, r);
    ctx.arcTo(x, y, x + w, y, r);
    ctx.closePath();
  }

  function drawHud() {
    const s = getStats();
    ctx.fillStyle = "rgba(20, 24, 33, 0.7)";
    ctx.fillRect(0, 0, W, 40);
    ctx.font = "bold 15px Arial, sans-serif";
    ctx.textBaseline = "middle";
    ctx.fillStyle = "#ffffff";
    ctx.textAlign = "left";
    ctx.fillText(`${s.distance} m`, 12, 20);
    ctx.fillText(`Score ${s.score}`, 90, 20);
    ctx.fillText(`${s.wpm} WPM`, 210, 20);
    ctx.textAlign = "right";
    ctx.fillStyle = "#ff5a6e";
    ctx.fillText("\u2665".repeat(Math.max(0, g.lives)) + "\u2661".repeat(Math.max(0, 3 - g.lives)), W - 12, 20);
    ctx.textAlign = "left";

    // Typing buffer at the bottom.
    ctx.fillStyle = "rgba(20, 24, 33, 0.7)";
    ctx.fillRect(0, H - 36, W, 36);
    ctx.font = "bold 18px 'Courier New', monospace";
    ctx.fillStyle = "#ffe066";
    ctx.textAlign = "center";
    ctx.fillText(g.typed ? g.typed + "_" : "type a word...", W / 2, H - 18);
    ctx.textAlign = "left";
  }

  function drawOverlay() {
    ctx.fillStyle = "rgba(15, 18, 26, 0.72)";
    ctx.fillRect(0, 0, W, H);
    ctx.fillStyle = "#ffffff";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";

    if (state === "loading") {
      ctx.font = "bold 24px Arial, sans-serif";
      ctx.fillText("Loading words...", W / 2, H / 2);
    } else if (state === "ready") {
      ctx.font = "bold 30px Arial, sans-serif";
      ctx.fillText("Typing Runner", W / 2, H / 2 - 70);
      ctx.font = "16px Arial, sans-serif";
      ctx.fillStyle = COLORS.turn;
      ctx.fillText("Blue words = turns", W / 2, H / 2 - 20);
      ctx.fillStyle = COLORS.jump;
      ctx.fillText("Orange words = jumps", W / 2, H / 2 + 6);
      ctx.fillStyle = "#ffffff";
      ctx.fillText("Press ENTER to start  \u2022  ESC to pause", W / 2, H / 2 + 50);
    } else if (state === "paused") {
      ctx.font = "bold 28px Arial, sans-serif";
      ctx.fillText("Paused", W / 2, H / 2 - 10);
      ctx.font = "16px Arial, sans-serif";
      ctx.fillText("Press ESC to resume", W / 2, H / 2 + 24);
    } else if (state === "over") {
      const s = getStats();
      ctx.font = "bold 30px Arial, sans-serif";
      ctx.fillText("Run Over", W / 2, H / 2 - 100);
      ctx.font = "18px Arial, sans-serif";
      ctx.fillText(`Score: ${s.score}`, W / 2, H / 2 - 50);
      ctx.fillText(`Distance: ${s.distance} m`, W / 2, H / 2 - 22);
      ctx.fillText(`WPM: ${s.wpm}   Accuracy: ${s.accuracy}%`, W / 2, H / 2 + 6);
      ctx.fillText(`Obstacles cleared: ${s.obstacles_cleared}`, W / 2, H / 2 + 34);
      ctx.font = "15px Arial, sans-serif";
      ctx.fillStyle = "#ffe066";
      ctx.fillText(saveStatus, W / 2, H / 2 + 68);
      ctx.fillStyle = "#ffffff";
      ctx.fillText("Press ENTER to run again", W / 2, H / 2 + 100);
    }
    ctx.textAlign = "left";
  }

  // Sound (tiny Web Audio beeps)
  let audioCtx = null;
  function beep(freq, duration) {
    if (!settings.sound) return;
    try {
      audioCtx = audioCtx || new (window.AudioContext || window.webkitAudioContext)();
      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();
      osc.frequency.value = freq;
      osc.type = "square";
      gain.gain.setValueAtTime(0.05, audioCtx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + duration);
      osc.connect(gain).connect(audioCtx.destination);
      osc.start();
      osc.stop(audioCtx.currentTime + duration);
    } catch (err) {
      settings.sound = false;
    }
  }

  // Input 
  window.addEventListener("keydown", (ev) => {
    // Don't steal keys from form fields elsewhere on the page.
    const tag = (ev.target && ev.target.tagName) || "";
    if (tag === "INPUT" || tag === "TEXTAREA" || tag === "SELECT") return;

    if (ev.key === "Enter" && (state === "ready" || state === "over")) {
      ev.preventDefault();
      resetRun();
      state = "running";
      return;
    }
    if (ev.key === "Escape" && (state === "running" || state === "paused")) {
      state = state === "running" ? "paused" : "running";
      return;
    }
    if (state !== "running") return;

    if (ev.key === " " || ev.key === "Backspace") ev.preventDefault();
    if (ev.key.length === 1 && /[a-z]/i.test(ev.key)) {
      handleKey(ev.key.toLowerCase());
    }
  });

  // ---------- Main loop ----------
  function loop(time) {
    const dt = Math.min((time - lastTime) / 1000 || 0, 0.05); // clamp big gaps
    lastTime = time;
    if (state === "running") update(dt);
    draw();
    requestAnimationFrame(loop);
  }

  resetRun();
  loadWords();
  requestAnimationFrame(loop);
})();
