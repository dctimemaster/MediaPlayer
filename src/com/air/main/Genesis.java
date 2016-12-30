package com.air.main;

/**
 * Author: D
 * Date: 9/21/09
 * Time: 4:58 PM
 */


import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javafx.geometry.Point2D;

public class Genesis extends Application {

    public final String testURL = "file:///C:/Users/D/Documents/Desk/IdeaProjects/MediaPlayerTest4/media/Aboki.mp4";
    public boolean isFull;
    private Stage stage;

    //private Point2D anchorPt;
    //private Point2D previousLocation;

    public static void main(String[] args) {
        launch(args);
    }

    private void init(final Stage primaryStage) {
        this.stage = primaryStage;
        File file = new File(testURL);

        try {
            primaryStage.setTitle("Air - " + file.getName());
            double MIN_WINDOW_WIDTH = 625.0;
            primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
            double MIN_WINDOW_HEIGHT = 418.0;
            primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);

            startMedia();

            primaryStage.centerOnScreen();
            primaryStage.setResizable(true);
            primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.show();
        } catch (Exception e) {
            Logger.getLogger(Genesis.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void startMedia() {
        try {
            replaceSceneContent();
        } catch (Exception e) {
            Logger.getLogger(Genesis.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void replaceSceneContent() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Genesis.class.getResourceAsStream("airFX.fxml");
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Genesis.class.getResource("airFX.fxml"));
        final AnchorPane page;

        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        }

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(page);
        Scene scene = new Scene(stackPane);
        stackPane.setCache(true);

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newValue) {
                System.out.println("Width: " + newValue);
                page.setPrefWidth(newValue.doubleValue());
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newValue) {
                double a = newValue.doubleValue();
                System.out.println("Height: " + a);
                page.setPrefHeight(newValue.doubleValue());
            }
        });


        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (keyEvent.getCode() == KeyCode.ENTER && stage.isFullScreen()) {
                        stage.setFullScreen(false);
                    } else {
                        stage.setFullScreen(true);
                    }
                }

                if (keyEvent.getCode() == KeyCode.DOWN) {
                    stage.setIconified(true);
                }
            }
        });

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY && !stage.isFullScreen()) {
                    stage.setFullScreen(true);

                    if (stage.isFullScreen()) {
                        isFull = true;
                    }
                } else if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY && stage.isFullScreen()) {
                    stage.setFullScreen(false);

                    if (!stage.isFullScreen()) {
                        isFull = false;
                    }
                }
            }
        });

        stage.fullScreenProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                isFull = stage.isFullScreen();
            }
        });

        stage.setScene(scene);
        stage.sizeToScene();
    }

    @Override
    public void start(Stage primaryStage) {
        init(primaryStage);
    }
}
