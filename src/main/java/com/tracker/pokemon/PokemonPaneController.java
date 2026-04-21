package com.tracker.pokemon;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


/**
 * Random Pokemon Image!  Add it to your scene
 *
 * @author Eric Holm
 * @since 4/16/26
 */
public class PokemonPaneController {

  @FXML private Pane pokePane;
  @FXML private ImageView pokeImage;

  @FXML public void initialize() {
    Random random = new Random();
    Integer pokeInt = random.nextInt(1000);
    JsonNode apiRes = PokeAPI.getApiJson(URI.create("https://pokeapi.co/api/v2/pokemon/" + pokeInt));
    // confirm response
    if (apiRes == null) {
      System.out.println("PokemonPaneController: no API response");
      return;
    }
    // make sure that json element is there
    JsonNode frontDefault = apiRes.path("sprites").path("front_default");
    if (frontDefault.isMissingNode() || frontDefault.isNull()) {
      System.out.println("PokemonPaneController: sprites.front_default not found");
      return;
    }
    String imageURI = frontDefault.asText();
    Image sprite = new Image(imageURI);
    pokeImage.setImage(sprite);
  }
}


