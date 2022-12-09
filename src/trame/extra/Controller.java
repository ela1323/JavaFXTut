package trame.extra;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import trame.ITrame;
import trame.ListeTrames;

public class Controller {

    private Scene scene;
    private Stage stage;
    private Parent root;
    private ListeTrames lt;
    private File selectedFile;

    @FXML
    public VBox vbox;

    @FXML
    public ScrollPane sp;
    @FXML
    public ScrollPane sp2;

    @FXML
    public Button filtreBut;

    @FXML
    public TextField textF;

    // @FXML
    // public CategoryAxis xAxis;

    // @FXML
    // public NumberAxis yAxis;

    @FXML
    public AnchorPane ap;

    @FXML
    public void chooseFile(ActionEvent e) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Frame File");
        selectedFile = fileChooser.showOpenDialog(stage);
        lt = ListeTrames.loadTrames(selectedFile.getAbsolutePath());
        initDiag();
        filtreBut.setDisable(false);
        textF.setDisable(false);
        

    }

    private void initDiag() {

        vbox.getChildren().clear();
        NumberAxis yAxis = new NumberAxis(0, lt.getTrames().size(), 1);
        CategoryAxis xAxis = new CategoryAxis();
        // NumberAxis yAxis = new NumberAxis(-lt.getTrames().size(), 0, 1);
        // NumberAxis yAxis = new NumberAxis();
        // yaxis.setLowerBound(-lt.getTrames().size());
        // yaxis.setUpperBound(0);

        yAxis.setLowerBound(lt.getTrames().size() + 1);
        yAxis.setUpperBound(0);
        xAxis.setSide(Side.TOP);
        xAxis.focusTraversableProperty().set(true);
        yAxis.autoRangingProperty().set(false);
        // yaxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yaxis) {
        // @Override
        // public String toString(Number value) {
        // // note we are printing minus value
        // return String.format("%7.1f", value.doubleValue());
        // }
        // });

        // xaxis = new CategoryAxis();

        LineChart linechart = new LineChart<String, Number>(xAxis, yAxis);
        linechart.focusTraversableProperty().set(true);

        // XYChart.Series series = new XYChart.Series();
        // series.setName("My portfolio");
        // populating the series with data
        // series.getData().add(new XYChart.Data<String, Number>("a", 5));
        // series.getData().add(new XYChart.Data<String, Number>("b", 6));
        // series.getData().add(new XYChart.Data<String, Number>("g", 4));
        // series.getData().add(new XYChart.Data<String, Number>("b", 7));
        // series.getData().add(new XYChart.Data<String, Number>("b", 8));
        // series.getData().add(new XYChart.Data<String, Number>("b", 9));

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // for (int i = 0; i < lt.getListIP().size(); i++) {
        // series.getData().add(new XYChart.Data(lt.getListIP().get(i), -i - 1));
        // // series.getData().add(new XYChart.Data(lt.getListIP().get(i), i, new
        // // Rectangle()));
        // }
        // linechart.getData().add(series);
        // double rowHeight = 30.;
        Label te = new Label("Comment");
        te.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        te.setMinHeight(Control.USE_PREF_SIZE);
        te.setPrefHeight(30);
        vbox.setPadding(new javafx.geometry.Insets(13, 0, 30, 0));
        te.setMaxHeight(Control.USE_PREF_SIZE);
        vbox.getChildren().add(te);

        for (int i = 0; i < lt.getTrames().size(); i++) {
            series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data(lt.getTrames().get(i).getMesSrc(), i + 1));
            series.getData().add(new XYChart.Data(lt.getTrames().get(i).getMesDst(), i + 1));
            linechart.getData().add(series);

            Node node = series.getData().get(0).getNode();
            node.setScaleX(Double.parseDouble("0.0"));

            te = new Label(lt.getTrames().get(i).getComment());
            te.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            te.setMinHeight(Control.USE_PREF_SIZE);
            te.setPrefHeight(30);
            te.setMaxHeight(Control.USE_PREF_SIZE);
            vbox.getChildren().add(te);

        }
        linechart.setLegendVisible(false);

        linechart.horizontalGridLinesVisibleProperty();

        // for (ITrame t : lt.getTrames()) {
        // Label te = new Label(t.getComment());
        // te.setMinHeight(Control.USE_PREF_SIZE);
        // te.setPrefHeight(30);
        // te.setMaxHeight(Control.USE_PREF_SIZE);
        // // lv.getChildren().addAll(te);
        // }
        // // lv.setFixedCellSize(30.);
        // lv.setPadding(new javafx.geometry.Insets(40, 0, 0, 0));
        // vbox.setPadding(new javafx.geometry.Insets(20, 0, 0, 0));
        linechart.setPadding(new javafx.geometry.Insets(0, 400, 0, 0));
        linechart.setPrefSize((lt.getListIP().size() + 5) * 150., (lt.getTrames().size() + 2) * 30.);
        // linechart.setMinSize(500, 500);
        linechart.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_PREF_SIZE);
        linechart.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        ap.getChildren().add(linechart);
        // stp.getChildren().add(linechart);
        sp.setContent(linechart);
        sp2.setContent(vbox);
        sp.vvalueProperty().bindBidirectional(sp2.vvalueProperty());
        System.out.println(lt.getListIP());

        // WritableImage nodeshot = linechart.snapshot(new SnapshotParameters(), null);
        // File file = new File("chart.png");

        // try {
        //     ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
        // } catch (IOException e) {

        // }
    }

    @FXML
    public void filtrage(ActionEvent e) {
        lt = ListeTrames.loadTrames(selectedFile.getAbsolutePath());
        String text = textF.getText();
        String[] fil = text.split("\\ ");
        for (int i = 0; i < fil.length; i++) {
            if (fil[i].equals("-ip") || fil[i].equals("-i")) {
                i++;
                String[] ipa = fil[i].split("\\ ");
                lt.filtreParIp(ipa);
                initDiag();
            } else if (fil[i].equals("-protocol") || fil[i].equals("-p")) {
                {
                    i++;
                    String[] ipa = fil[i].split("\\ ");
                    lt.filtreParProtocol(ipa);
                    initDiag();
                }
            }
        }
    }

}
