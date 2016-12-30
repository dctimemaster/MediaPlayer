package com.air.video;

import com.air.main.Genesis;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Author: D
 * Time: 10:36 PM
 */
class MediaViewScreen extends BorderPane {

    private final MediaView mediaView;

    public MediaViewScreen(final MediaPlayer mediaPlayer) {

        mediaView = new MediaView(mediaPlayer);

        Pane sp1 = new Pane();
        sp1.setPrefSize(1320.0, 0.0);
        setBottom(sp1);

        Pane sp2 = new Pane();
        sp2.setPrefSize(1320.0, 0.0);
        setTop(sp2);

        getMediaView().setCache(true);
        getMediaView().setManaged(true);
        getMediaView().setPickOnBounds(true);
        getMediaView().setSmooth(true);
        getMediaView().setPreserveRatio(true);

        if (new Genesis().isFull) {
            getMediaView().setPreserveRatio(false);
        }


        Pane mvPane = new Pane();

        getMediaView().setFitWidth(getPrefWidth());
        getMediaView().setFitHeight(getPrefHeight());

        mvPane.prefWidthProperty().bind(getMediaView().fitWidthProperty());
        mvPane.prefHeightProperty().bind(getMediaView().fitHeightProperty());

        mvPane.getChildren().add(getMediaView());
        setCenter(mvPane);
    }

    MediaView getMediaView() {
        return mediaView;
    }

    @Override
    protected void layoutChildren() {
        if (getMediaView() != null && getBottom() != null) {
            getMediaView().setFitWidth(getPrefWidth());
            getMediaView().setFitHeight(getPrefHeight() - getBottom().prefHeight(-1));
        }
        super.layoutChildren();
        if (getMediaView() != null && getCenter() != null) {
            getMediaView().setTranslateX((((Pane) getCenter()).getPrefWidth() - getMediaView().prefWidth(-1)) / 2);
            getMediaView().setTranslateY((((Pane) getCenter()).getPrefHeight() - getMediaView().prefHeight(-1)) / 2);
        }
    }
}
