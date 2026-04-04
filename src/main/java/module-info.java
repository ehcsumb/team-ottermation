module org.example.project02 {
  requires javafx.controls;
  requires javafx.fxml;

  opens org.example.project02 to javafx.fxml;
  exports org.example.project02;
}