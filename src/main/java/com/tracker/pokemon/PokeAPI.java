package com.tracker.pokemon;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class interacts with PokeAPI - info here https://pokeapi.co/
 * Only fetches Pokemon data at https://pokeapi.co/api/v2/pokemon/*
 *
 *
 * @author Eric Holm
 * @since 4/16/26
 */
public class PokeAPI {

  private static String apiHost = "pokeapi.co";
  private static String protocol = "https";
  private static String base = "/api/v2/";
  private static URI uri;

  private static HttpClient client = HttpClient.newHttpClient();

  static {
    try {
      uri = new URI(protocol,apiHost,base,null,null);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static JsonNode getApiJson(URI uri) {
    try {
      HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body());
      } else {
        System.out.println("GET request did not work: " + response.statusCode());
        return null;
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] getApiBytes(URI uri) {
    try {
      // let's try fetching the data from PokeAPI
      HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

      HttpResponse<byte[]> response = client.send(request,HttpResponse.BodyHandlers.ofByteArray());

      return response.body();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static class Pokemon {
    private static String base = "pokemon/";
    private int id;
    private String name;
    private ArrayList<Sprite> sprites;

    public Pokemon(int id, String name, ArrayList<Sprite> sprites) {
      this.id = id;
      this.name = name;
      this.sprites = sprites;
    }

    public static Pokemon createFromApi(int id) {
      URI uri = PokeAPI.uri.resolve(base).resolve(Integer.toString(id));
      JsonNode response = PokeAPI.getApiJson(uri);

      return new Pokemon(1,"pikachu",new ArrayList<>());
    }

  }

  public class Sprite {
    private String name;
    private URI uri;
    private byte[] imageData;

    public Sprite(String name, URI uri) {
      this.name = name;
      this.uri = uri;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    private void fetchSpriteImageData() {
      // no uri, no go
      if (uri == null) {
        System.out.println("no uri defined");
        return;
      }

      try {
        this.imageData = PokeAPI.getApiBytes(uri);
      } catch (RuntimeException e) {
        System.out.println("error getting data: " + e.getCause());
      }
    }
  }

}
