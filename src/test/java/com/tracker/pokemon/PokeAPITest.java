package com.tracker.pokemon;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Eric Holm
 * @since 4/18/26
 */
class PokeAPITest {

  @Test
  void getApi() {
    try {
      URI uri = new URI("https","pokeapi.co","api/v2/",null,null);
      JsonNode node = PokeAPI.getApiJson(uri);
      JsonNode sprites = node.get("sprites");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

  }
}