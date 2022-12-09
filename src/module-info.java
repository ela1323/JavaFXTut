module JavaFXTut {
	exports trame;
	exports trame.extra;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.swing;
	opens trame.extra to javafx.graphics;
}