package com.omicron.hypertorus;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HypertorusApplication extends Application {

    public static Map<String, Color> colorMap = new HashMap<>();

    static {
        colorMap.put("BLACK", Color.BLACK);
        colorMap.put("WHITE", Color.WHITE);
        colorMap.put("RED", Color.RED);
        colorMap.put("GREEN", Color.GREEN);
        colorMap.put("BLUE", Color.BLUE);
        colorMap.put("YELLOW", Color.YELLOW);
        colorMap.put("CYAN", Color.CYAN);
        colorMap.put("MAGENTA", Color.MAGENTA);
        colorMap.put("AQUA", Color.AQUA);
        colorMap.put("ORANGE", Color.ORANGE);
        colorMap.put("PURPLE", Color.PURPLE);
        colorMap.put("PINK", Color.PINK);
        colorMap.put("BROWN", Color.BROWN);
        colorMap.put("GRAY", Color.GRAY);
        colorMap.put("DARK BLUE", Color.DARKBLUE);
        colorMap.put("DARK GREEN", Color.DARKGREEN);
        colorMap.put("GOLD", Color.GOLD);
        colorMap.put("TURQUOISE", Color.TURQUOISE);
        colorMap.put("ROYAL BLUE", Color.ROYALBLUE);
        colorMap.put("VIOLET", Color.VIOLET);
    }
    public static void setCamera(Camera camera)
    {
        double x1 = distanceToTorus * Math.cos(Math.toRadians(cameraAngleX)) * Math.sin(Math.toRadians(cameraAngleY));
        double y1 = distanceToTorus * Math.sin(Math.toRadians(cameraAngleX)) * Math.sin(Math.toRadians(cameraAngleY));
        double z1 = distanceToTorus * Math.cos(Math.toRadians(cameraAngleY));
        camera.setTranslateZ(x1);
        camera.setTranslateX(y1);
        camera.setTranslateY(-z1);
        Rotate rotateX = new Rotate(-90 + cameraAngleY, Rotate.X_AXIS);  // Initial rotation 0 degrees
        Rotate rotateY = new Rotate((180 + cameraAngleX), new Point3D(0, 1, 0));  // Initial rotation 0 degrees
        camera.getTransforms().clear();
        camera.getTransforms().addAll(
                rotateY,
                rotateX);
    }

    public static List<Point4D> listOfTorusPoints = new ArrayList<>();

    public static List<TextField> textFields = new ArrayList<>();

    public static double cameraAngleY = 90, cameraAngleX = 0, distanceToTorus = 50,
            r = 5, t = 3, d = 1,
            A = 1, B = 1, C = 1, D = 1, E = 1,
            cutoff = 0.05F, frequency = 0.1, spinSpeed = 1, pointSize = 0.2;

    public static boolean flip = false;

    public static Group mainGroup = new Group();

    public static SubScene subScene = new SubScene(mainGroup, 500, 400);

    public static ComboBox<String> backgroundColorPicker = new ComboBox<>(), pointColorPicker = new ComboBox<>();
    @Override
    public void start(Stage primaryStage) {




        PerspectiveCamera camera = new PerspectiveCamera(true);

        setCamera(camera);


        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        Slider cameraAngleXSlider = new Slider(-180, 180, 0);
        cameraAngleXSlider.setShowTickMarks(true);
        cameraAngleXSlider.setShowTickLabels(true);
        cameraAngleXSlider.setMajorTickUnit(0.25f * 20);
        cameraAngleXSlider.setBlockIncrement(0.1f * 20);
        cameraAngleXSlider.valueProperty().addListener(
                (_, _, newValue) -> {
                    cameraAngleX = newValue.doubleValue();
                    setCamera(camera);
                });
        Slider cameraAngleYSlider = new Slider(0, 180, 90);
        cameraAngleYSlider.setShowTickMarks(true);
        cameraAngleYSlider.setShowTickLabels(true);
        cameraAngleYSlider.setMajorTickUnit(0.25f * 20);
        cameraAngleYSlider.setBlockIncrement(0.1f * 20);
        cameraAngleYSlider.valueProperty().addListener(
                (_, _, newValue) -> {

                    cameraAngleY = newValue.doubleValue();
                    setCamera(camera);
                });

        VBox uiContainer = new VBox(10);
        uiContainer.setPadding(new Insets(10));

        VBox slidersContainer = new VBox(10);
        slidersContainer.setPadding(new Insets(10));
        slidersContainer.getChildren().addAll(new Label("X rotation"), cameraAngleXSlider, new Label("Y rotation"), cameraAngleYSlider);


        uiContainer.getChildren().addAll(new Label("Camera position"), slidersContainer);

        HBox firstRow = new HBox(10);
        firstRow.setMaxWidth(Double.MAX_VALUE);

        Button button = new Button("Regenerate Hypertorus");
        button.setOnAction(_ -> {
            updateGroup();
            setCamera(camera);
        });

        firstRow.getChildren().add(button);

        CheckBox spin = new CheckBox("Make it spin");

        firstRow.getChildren().add(spin);



        AnimationTimer spinAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if(spin.isSelected())
                {
                    double xValue = cameraAngleXSlider.getValue();
                    double yValue = cameraAngleYSlider.getValue();

                    double addX = spinSpeed * 2;
                    double addY = spinSpeed;

                    xValue += addX;
                    yValue += flip ? -addY : addY;

                    xValue = xValue > 180 ? xValue - 360 : xValue;
                    if(yValue > 180)
                    {
                        yValue = 180;
                        flip = !flip;
                    }
                    if(yValue < 0)
                    {
                        yValue = 0;
                        flip = !flip;
                    }
                    //yValue = yValue > 180 ? yValue - 180 : yValue;

                    //yValue = yValue < 0 ? yValue + 180 : yValue;

                    cameraAngleXSlider.setValue(xValue);
                    cameraAngleYSlider.setValue(yValue);
                }
            }
        };

        spinAnimationTimer.start();

        TextField spinSpeedField = new TextField();

        spinSpeedField.setText("1");

        spinSpeedField.textProperty().addListener((_, _, newValue) -> {
            try {
                spinSpeed = Double.parseDouble(newValue);
            } catch (NumberFormatException e) {

            }
        });

        firstRow.getChildren().add(spinSpeedField);

        firstRow.getChildren().add(new Label(" Spin speed"));

        uiContainer.getChildren().add(firstRow);

        addTextField(uiContainer, new String[]{"1", "1", "1", "1", "1"}, new String[]{" X plane position                ", "Y plane position                ", "Z plane position                ", "W plane position                ", "E value"});

        addTextField(uiContainer, new String[]{"5", "3", "1"}, new String[]{          " R value (first radius)          ", "T value (second radius)         ", "D value (third radius)          "});

        addTextField(uiContainer, new String[]{"50", "0.05", "0.1"}, new String[]{    " Distance to Hypertorus          ", "Cutoff value for points         ", "Frequency of points             "});

        //addTextField(uiContainer, new String[]{"0.2", "0.05", "0.1"}, new String[]{    " Distance to Hypertorus          ", "Cutoff value for points         ", "Frequency of points             "});

        HBox lastRow = new HBox(10);
        lastRow.setMaxWidth(Double.MAX_VALUE);

        TextField pointSize = new TextField();
        pointSize.setText("0.2");
        lastRow.getChildren().add(pointSize);
        textFields.add(pointSize);

        backgroundColorPicker.getItems().addAll(colorMap.keySet());
        backgroundColorPicker.setValue("BLACK"); // Set default value
        lastRow.getChildren().add(backgroundColorPicker);

        pointColorPicker.getItems().addAll(colorMap.keySet());
        pointColorPicker.setValue("WHITE"); // Set default value
        lastRow.getChildren().add(pointColorPicker);
        uiContainer.getChildren().add(new Label(" Point size                             Background color       Point color"));
        uiContainer.getChildren().add(lastRow);

        Button screenShotButton = new Button("Save image to file");
        screenShotButton.setOnAction((_) -> saveSubSceneToFile(subScene));
        uiContainer.getChildren().add(screenShotButton);

        updateGroup();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(uiContainer);

        BorderPane mainLayout = new BorderPane();

        subScene.widthProperty().bind(mainLayout.widthProperty());

        mainLayout.setTop(subScene);
        mainLayout.setCenter(scrollPane);

        Scene mainScene = new Scene(mainLayout, 1000, 800, true);

        primaryStage.setTitle("Hipertorus");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static Sphere createSphere(double x, double y, double z) {
        Sphere sphere = new Sphere(pointSize);
        sphere.setTranslateX(x);
        sphere.setTranslateY(y);
        sphere.setTranslateZ(z);
        sphere.setMaterial(new PhongMaterial(colorMap.get(pointColorPicker.getValue())));
        return sphere;
    }

    public static void updateGroup()
    {
        subScene.setFill(colorMap.get(backgroundColorPicker.getValue()));
        try {
            A = Double.parseDouble(textFields.get(0).getText());
            B = Double.parseDouble(textFields.get(1).getText());
            C = Double.parseDouble(textFields.get(2).getText());
            D = Double.parseDouble(textFields.get(3).getText());
            E = Double.parseDouble(textFields.get(4).getText());

            r = Double.parseDouble(textFields.get(5).getText());
            t = Double.parseDouble(textFields.get(6).getText());
            d = Double.parseDouble(textFields.get(7).getText());

            distanceToTorus = Double.parseDouble(textFields.get(8).getText());
            cutoff = Double.parseDouble(textFields.get(9).getText());
            frequency = Double.parseDouble(textFields.get(10).getText());

            pointSize = Double.parseDouble(textFields.get(11).getText());

        } catch (NumberFormatException _) {

        }

        generateHyperTorus(r, t, d);
        mainGroup.getChildren().removeIf(c -> c instanceof Sphere);
        List<Point4D> l = listOfTorusPoints.stream().filter(point -> Point4D.distance(point.x, point.y, point.z, point.w, A, B, C, D, E) < cutoff).map(point -> Point4D.closestPointOnPlane(point, A, B, C, D, E)).toList();
        for (Point4D p : l)
            mainGroup.getChildren().add(createSphere(p.x, p.y, p.z));

    }

    public static void generateHyperTorus(double r, double t, double d)
    {
        listOfTorusPoints.clear();
        for (double a = 0; a < Math.PI * 2; a = a + frequency)
        {
            for (double b = 0; b < Math.PI * 2; b = b + frequency)
            {
                for (double c = 0; c < Math.PI * 2; c = c + frequency)
                {
                    double x = (r + (t + d * Math.cos(a)) * Math.cos(b)) * Math.cos(c);
                    double y = (r + (t + d * Math.cos(a)) * Math.cos(b)) * Math.sin(c);
                    double z = (t + d * Math.cos(a)) * Math.sin(b);
                    double w = d * Math.sin(a);
                    listOfTorusPoints.add(new Point4D(x, y, z, w));
                }
            }
        }
    }

    public static void addTextField(VBox uiContainer, String[] defults, String[] labels)
    {

        HBox textFieldRow = new HBox(10);
        textFieldRow.setMaxWidth(Double.MAX_VALUE);

        for (String defult : defults) {
            TextField textField = new TextField();
            textField.setText(defult);

            textField.setMaxWidth(Double.MAX_VALUE);
            textFieldRow.getChildren().add(textField);

            textFields.add(textField);

        }


        HBox textFieldRowLabel = new HBox(10);
        textFieldRowLabel.setMaxWidth(Double.MAX_VALUE);
        for (String label : labels)
            textFieldRowLabel.getChildren().add(new Label(label));

        uiContainer.getChildren().addAll(textFieldRowLabel, textFieldRow);

    }
    private void saveSubSceneToFile(SubScene subScene) {
        WritableImage writableImage = new WritableImage((int) subScene.getWidth(), (int) subScene.getHeight());

        subScene.snapshot(new SnapshotParameters(), writableImage);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("hypertorus.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
