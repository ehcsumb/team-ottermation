package com.tracker.pokemon;

import static org.junit.jupiter.api.Assertions.*;

import com.tracker.pokemon.PokeAPI.Pokemon;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Eric Holm
 * @since 4/19/26
 */
class PokemonTest {

  @Test
  void createFromApiTest() {
    Pokemon pokemon = Pokemon.createFromApi(1);
  }
}