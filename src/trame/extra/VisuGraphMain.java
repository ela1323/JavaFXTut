package trame.extra;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.security.Policy;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class VisuGraphMain extends Application {
    Button button;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/trame/extra/Main1.fxml"));

        // Group root = new Group();
        // // StackPane layout = new StackPane();
        // Scene scene = new Scene(root, 600, 650, Color.ALICEBLUE);
        Scene scene = new Scene(root);

        String css = this.getClass().getResource("application.css").toExternalForm();
        scene.getStylesheets().add(css);

        // scene.getStylesheets().add(getClass().getResource("/trame/extra/application.css").toExternalForm());

        primaryStage.setTitle("Visualisateur traffic reseau");

        primaryStage.setScene(scene);
        primaryStage.show();

        // BufferedImage bi = createCompatibleImage(width, height);
        // WritableRaster raster = bi.getRaster();
        // DataBufferInt dataBuffer = (DataBufferInt) raster.getDataBuffer();

        // System.arraycopy(pixels, 0, dataBuffer.getData(), 0, pixels.length);
        // WritableImage image = root.snapshot(new SnapshotParameters(), null);
        // // PixelWriter pw = image.getPixelWriter();
        // // WritablePixelFormat<IntBuffer> pf =
        // WritablePixelFormat.getIntArgbInstance();
        // File file = new File("output.pdf");
        // // pw.setPixels(0, 0, image.getWidth(), image.getHeight(), pf);
        // ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg", file);
        

        // primaryStage.setWidth(420);
        // primaryStage.setHeight(420);

        // // primaryStage.setResizable(false);
        // // primaryStage.setFullScreen(true);
        // primaryStage.setFullScreenExitHint("press q");
        // primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));

        // Text text = new Text();
        // text.setText("whoo");
        // text.setX(50);
        // text.setY(50);
        // text.setFont(Font.font("Verdana", 50));
        // text.setFill(Color.GREEN);

        // Line line = new Line();
        // line.setStartX(text.getX());
        // line.setStartY(text.getY());

        // line.setEndX(text.getX() + 100);
        // line.setEndY(text.getY());
        // line.setStrokeWidth(3);
        // line.setStroke(Color.RED);
        // line.setOpacity(1);
        // // line.setRotate(45);

        // Rectangle rect = new Rectangle();
        // rect.setX(100);
        // rect.setY(100);
        // rect.setWidth(100);
        // rect.setHeight(100);
        // rect.setFill(Color.AZURE);
        // rect.setStrokeWidth(5);
        // rect.setStroke(Color.BLACK);

        // Polygon triangle = new Polygon();
        // triangle.getPoints().setAll(200., 200., 300., 300., 200., 300.);
        // triangle.setFill(Color.YELLOW);

        // Circle circle = new Circle();
        // circle.setCenterX((350));
        // circle.setCenterY((350));

        // circle.setRadius(50);
        // circle.setFill(Color.ORANGE);

        // root.getChildren().add(text);
        // root.getChildren().add(line);
        // root.getChildren().add(rect);
        // root.getChildren().add(triangle);
        // root.getChildren().add(circle);

        // // button = new Button();
        // // button.setText("Click me");
        // // root.getChildren().add(button);

    }
}
