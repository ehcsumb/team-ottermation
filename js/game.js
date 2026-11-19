/*
 * Typing Runner: the animal runs up a scrolling path. Type the blue word before a turn,
 * and the orange word to jump an obstacle. Three crashes and the run is over.
 * Words come from the database through api.js, or the local list below if that fails.
 */
(function () {
  "use strict";
  const canvas = document.getElementById("gameCanvas");
  const ctx = canvas.getContext("2d");
  const W = canvas.width, H = canvas.height;

  // Settings come from the canvas data-* attributes.
  const DIFFICULTY = {
    easy:   { start: 110, accel: 2,   max: 260 },
    normal: { start: 140, accel: 3,   max: 340 },
    hard:   { start: 180, accel: 4.5, max: 420 }
  };
  const BIOMES = {
    desert: { ground: "#e9c98b", path: "#c8955a", edge: "#a8743f", obstacles: ["cactus", "rock"] },
    forest: { ground: "#5e9e4f", path: "#9c7b52", edge: "#6f5434", obstacles: ["log", "rock"] },
    snow:   { ground: "#eef4f8", path: "#b9cbd8", edge: "#8ea5b5", obstacles: ["rock", "log"] }
  };
  const ANIMALS = {
    jackrabbit: { body: "#b08a62", ear: "#d9a6a0", earLen: 16 },
    fox:        { body: "#d9682b", ear: "#3b2a20", earLen: 9 },
    tortoise:   { body: "#5f7f3c", shell: "#c9b37a", earLen: 0 }
  };
  const OBSTACLES = { cactus: ["#3f8f45", 16, 16], rock: ["#8a8f96", 20, 14], log: ["#7a5230", 34, 9] }; // color, width, height
  const diff = DIFFICULTY[canvas.dataset.difficulty] || DIFFICULTY.normal;
  const biome = BIOMES[canvas.dataset.biome] || BIOMES.desert;
  const animal = ANIMALS[canvas.dataset.animal] || ANIMALS.jackrabbit;

  let words = ("sand dune mesa sun heat trail dust hop dash sprint canyon sage yucca coyote lizard hawk quail boulder " +
    "ridge arroyo mirage oasis breeze shadow pebble thorn burrow dawn dusk wind storm river stone cliff valley meadow " +
    "pine maple frost glacier comet rocket jungle puzzle garden silver orange violet rapid swift brave quick").split(" ");

  // Layout in px: animal's screen y, path width, sideways move per turn, curve length, jump length
  const ANIMAL_Y = H - 130, PATH_W = 120, TURN_SHIFT = 110, CURVE_LEN = 220, JUMP_LEN = 130;
  const MAX_OFFSET = W / 2 - PATH_W / 2 - 20;

  let state = "loading"; // loading | ready | running | paused | over
  let g, saveStatus = "", lastTime = 0;

  function resetRun() {
    g = { dist: 0, speed: diff.start, elapsed: 0, baseOffset: 0, events: [], nextY: 420,
          target: null, typed: "", lives: 3, invuln: 0, jump: null,
          good: 0, bad: 0, jumps: 0, turns: 0, streak: 0, bonus: 0 };
    saveStatus = "";
  }

  async function loadWords() {
    try {
      const list = (await GameApi.fetchWords()).filter(w => /^[a-z]{2,10}$/.test(w));
      if (list.length >= 20) words = list;
    } catch (err) {
      console.warn("Using local word list.", err);
    }
    state = "ready";
  }

  const random = list => list[Math.floor(Math.random() * list.length)];

  function pickWord() { // short words early, longer later; avoid first letters already on screen
    const maxLen = Math.min(4 + Math.floor(g.dist / 1500), 10);
    const used = g.events.filter(e => !e.done).map(e => e.word[0]);
    const options = words.filter(w => w.length <= maxLen && !used.includes(w[0]));
    return random(options.length ? options : words);
  }

  // Path center (offset from the canvas middle) at world y. committed = only turns the player has typed.
  function pathAt(wy, committed) {
    let off = g.baseOffset;
    for (const e of g.events) {
      if (e.type !== "turn" || (committed && !e.committed)) continue;
      const t = Math.max(0, Math.min(1, (wy - e.y) / CURVE_LEN));
      off += e.dir * TURN_SHIFT * t * t * (3 - 2 * t); // smooth S-curve
    }
    return off;
  }
  const screenY = wy => ANIMAL_Y - (wy - g.dist);
  const upcoming = () => g.events // unfinished events on screen, nearest first
    .filter(e => !e.done && e.y >= g.dist - 40 && screenY(e.y) > -20)
    .sort((a, b) => a.y - b.y);

  function spawnEvents() {
    while (g.nextY < g.dist + H + 200) {
      const e = { y: g.nextY, word: pickWord() };
      if (Math.random() < 0.55) { // turn, in a direction that keeps the path on screen
        const end = g.events.reduce((off, t) => off + (t.dir || 0) * TURN_SHIFT, g.baseOffset);
        e.type = "turn";
        e.dir = Math.random() < 0.5 ? -1 : 1;
        if (Math.abs(end + e.dir * TURN_SHIFT) > MAX_OFFSET) e.dir = -e.dir;
        g.nextY += CURVE_LEN + 140 + Math.random() * 140;
      } else {
        e.type = "jump";
        e.kind = random(biome.obstacles);
        g.nextY += 240 + Math.random() * 160;
      }
      g.events.push(e);
    }
  }

  function handleKey(ch) {
    if (!g.target) { // pick a target by its first letter
      g.target = upcoming().find(e => e.word[0] === ch) || null;
      g.typed = "";
    }
    if (!g.target || g.target.word[g.typed.length] !== ch) { // wrong key
      g.bad++;
      g.streak = 0;
      if (g.target) g.target.missed = true;
      return;
    }
    g.good++;
    g.typed += ch;
    if (g.typed !== g.target.word) return;
    const e = g.target; // word finished
    e.done = true;
    if (!e.missed) g.bonus += 5 * Math.min(++g.streak, 10); // clean-typing bonus
    if (e.type === "turn") { e.committed = true; g.turns++; } else e.cleared = true;
    g.target = null;
    g.typed = "";
  }

  function crash(e) {
    e.done = true;
    if (e.type === "turn") e.committed = true; else e.hit = true; // snap back onto the path
    if (g.target === e) { g.target = null; g.typed = ""; }
    g.streak = 0;
    if (g.invuln > 0) return;
    g.lives--;
    g.invuln = 1.2; // seconds of crash protection
    if (g.lives <= 0) endRun();
  }

  function update(dt) {
    g.elapsed += dt;
    g.speed = Math.min(diff.max, g.speed + diff.accel * dt);
    g.dist += g.speed * dt;
    g.invuln = Math.max(0, g.invuln - dt);
    spawnEvents();

    // Missed turn: the path curves away until the animal is off the edge.
    if (Math.abs(pathAt(g.dist, false) - pathAt(g.dist, true)) > PATH_W / 2 - 14) {
      const missed = g.events.find(e => e.type === "turn" && !e.committed && g.dist >= e.y);
      if (missed) crash(missed);
    }
    // Obstacles: jump if the word was typed, crash if not.
    for (const e of g.events) {
      if (e.type !== "jump" || e.hit || e.jumped) continue;
      if (e.cleared && g.dist >= e.y - JUMP_LEN / 2) { e.jumped = true; g.jump = g.dist; g.jumps++; }
      else if (!e.cleared && g.dist >= e.y - 12) crash(e);
    }
    if (g.jump !== null && g.dist - g.jump > JUMP_LEN) g.jump = null;

    // Fold finished turns into baseOffset and drop events that are behind us.
    g.events = g.events.filter(e => {
      if (e.type === "turn" && g.dist > e.y + CURVE_LEN) { g.baseOffset += e.dir * TURN_SHIFT; return false; }
      return !(e.type === "jump" && g.dist > e.y + 120);
    });
  }

  function getStats() {
    const keys = g.good + g.bad, distance = Math.floor(g.dist / 10); // 10 px = 1 m
    return {
      distance,
      wpm: Math.round(g.good / 5 / Math.max(g.elapsed / 60, 1 / 60)),
      accuracy: keys ? Math.round(g.good / keys * 1000) / 10 : 100,
      obstacles_cleared: g.jumps,
      turns_made: g.turns,
      score: distance + g.bonus
    };
  }

  async function endRun() {
    state = "over";
    saveStatus = "Saving run...";
    try {
      await GameApi.saveRun(getStats());
      saveStatus = "Run saved!";
    } catch (err) {
      saveStatus = "Could not save run.";
    }
  }

  // Drawing 
  function oval(x, y, rx, ry, color) {
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.ellipse(x, y, rx, ry, 0, 0, Math.PI * 2);
    ctx.fill();
  }
  function text(str, x, y, color, align = "left") {
    ctx.fillStyle = color;
    ctx.textAlign = align;
    ctx.fillText(str, x, y);
  }

  function drawAnimal(x, y, scale) {
    const a = animal, leg = g.jump === null ? Math.sin(g.dist / 18) * 6 : 0;
    ctx.save();
    ctx.translate(x, y);
    ctx.scale(scale, scale);
    for (const [lx, ly] of [[-9, 8 + leg], [9, 8 - leg], [-8, -6 - leg], [8, -6 + leg]]) oval(lx, ly, 4, 6, a.body);
    oval(0, 2, 12, 16, a.shell || a.body);                                              // body
    oval(0, -17, 8, 8, a.body);                                                         // head
    if (a.earLen) for (const ex of [-4, 4]) oval(ex, -24 - a.earLen / 2, 3, a.earLen / 2, a.ear);
    for (const ex of [-3, 3]) oval(ex, -20, 1.5, 1.5, "#111");                          // eyes
    ctx.restore();
  }

  function draw() {
    ctx.fillStyle = biome.ground;
    ctx.fillRect(0, 0, W, H);
    ctx.textBaseline = "middle";

    // Path: trace the left edge down, then the right edge back up.
    const pathX = sy => W / 2 + pathAt(g.dist + ANIMAL_Y - sy, false);
    ctx.beginPath();
    for (let sy = -20; sy <= H + 20; sy += 8) ctx.lineTo(pathX(sy) - PATH_W / 2, sy);
    for (let sy = H + 20; sy >= -20; sy -= 8) ctx.lineTo(pathX(sy) + PATH_W / 2, sy);
    ctx.fillStyle = biome.path;
    ctx.fill();
    ctx.lineWidth = 4;
    ctx.strokeStyle = biome.edge;
    ctx.stroke();

    for (const e of g.events) {
      if (e.type === "jump" && !e.hit) {
        const [color, rx, ry] = OBSTACLES[e.kind];
        oval(W / 2 + pathAt(e.y, false), screenY(e.y), rx, ry, color);
      }
    }

    // Animal: shadow, then the body (lifted while jumping, blinking after a crash).
    const lift = g.jump === null ? 0 : Math.sin(Math.PI * (g.dist - g.jump) / JUMP_LEN);
    const ax = W / 2 + pathAt(g.dist, true);
    oval(ax, ANIMAL_Y + 14, 16, 7, "rgba(0,0,0,0.25)");
    if (!(g.invuln > 0 && Math.floor(g.invuln * 10) % 2 === 0)) drawAnimal(ax, ANIMAL_Y - lift * 18, 1 + 0.45 * lift);

    // Word labels: above obstacles, or beside the path on the side it turns to.
    const hint = g.target || upcoming()[0];
    ctx.font = "bold 18px 'Courier New', monospace";
    for (const e of g.events) {
      const sy = screenY(e.y), cx = W / 2 + pathAt(e.y, false);
      if (e.done || sy < -20 || sy > H) continue;
      const arrow = e.type === "turn" && e.dir < 0 ? "← " : "";
      const label = arrow + e.word + (e.type === "turn" && e.dir > 0 ? " →" : "");
      const w = ctx.measureText(label).width + 16;
      let x = e.type === "jump" ? cx - w / 2 : e.dir < 0 ? cx - PATH_W / 2 - 10 - w : cx + PATH_W / 2 + 10;
      x = Math.max(4, Math.min(W - w - 4, x));
      const y = e.type === "jump" ? sy - 34 : sy - 10;
      ctx.fillStyle = e.type === "turn" ? "#2f7de1" : "#f07c1c";
      ctx.beginPath();
      ctx.roundRect(x, y - 14, w, 28, 8);
      ctx.fill();
      if (e === hint) { ctx.lineWidth = 3; ctx.strokeStyle = "#fff"; ctx.stroke(); }
      text(label, x + 8, y, "#fff");
      if (e === g.target) text(g.typed, x + 8 + ctx.measureText(arrow).width, y, "#ffe066"); // typed part in yellow
    }

    // HUD: stats bar on top, typing bar at the bottom.
    const s = getStats();
    ctx.fillStyle = "rgba(20, 24, 33, 0.7)";
    ctx.fillRect(0, 0, W, 40);
    ctx.fillRect(0, H - 36, W, 36);
    text(g.typed ? g.typed + "_" : "type a word...", W / 2, H - 18, "#ffe066", "center");
    ctx.font = "bold 15px Arial";
    text(`${s.distance} m     Score ${s.score}     ${s.wpm} WPM`, 12, 20, "#fff");
    text("♥".repeat(g.lives) + "♡".repeat(3 - g.lives), W - 12, 20, "#ff5a6e", "right");

    if (state === "running") return;
    ctx.fillStyle = "rgba(15, 18, 26, 0.72)";
    ctx.fillRect(0, 0, W, H);
    const lines = { // first line is the title; [text, color] for colored lines
      loading: ["Loading words..."],
      ready: ["Typing Runner", ["Blue words = turns", "#2f7de1"], ["Orange words = jumps", "#f07c1c"],
              "Press ENTER to start, ESC to pause"],
      paused: ["Paused", "Press ESC to resume"],
      over: ["Run Over", `Score: ${s.score}   Distance: ${s.distance} m`, `WPM: ${s.wpm}   Accuracy: ${s.accuracy}%`,
             `Obstacles cleared: ${s.obstacles_cleared}`, [saveStatus, "#ffe066"], "Press ENTER to run again"]
    }[state];
    lines.forEach((line, i) => {
      const [str, color = "#fff"] = Array.isArray(line) ? line : [line];
      ctx.font = i === 0 ? "bold 30px Arial" : "17px Arial";
      text(str, W / 2, H / 2 + (i - (lines.length - 1) / 2) * 34, color, "center");
    });
  }

  // Input and main loop 
  window.addEventListener("keydown", (ev) => {
    if (["INPUT", "TEXTAREA", "SELECT"].includes(ev.target.tagName)) return; // leave form fields alone
    if (ev.key === "Enter" && (state === "ready" || state === "over")) {
      resetRun();
      state = "running";
    } else if (ev.key === "Escape" && (state === "running" || state === "paused")) {
      state = state === "running" ? "paused" : "running";
    } else if (state === "running") {
      if (ev.key === " " || ev.key === "Backspace") ev.preventDefault();
      if (/^[a-z]$/i.test(ev.key)) handleKey(ev.key.toLowerCase());
    }
  });

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
