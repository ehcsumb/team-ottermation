package com.tracker.pokemon;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
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
    JsonNode apiRes = PokeAPI.getApiJson(URI.create("https://pokeapi.co/api/v2/pokemon/pikachu"));
    String imageURI = apiRes.get("front_default").toString();
    Image sprite = new Image(imageURI);
    pokeImage.setImage(sprite);
  }
}


