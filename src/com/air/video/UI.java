package com.air.video;

import com.air.main.Genesis;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

//import javafx.scene.layout.BorderPane;

/**
 * Author: D
 * Date: 9/21/09
 * Time: 7:06 PM
 */
public class UI extends AnchorPane implements Initializable {

    private final InputStream pauseInputStream = Genesis.class.getResourceAsStream("assets/9-av-pause-over-video.png");
    private final InputStream playInputStream = Genesis.class.getResourceAsStream("assets/9-av-play-over-video.png");
    private final InputStream replayInputStream = Genesis.class.getResourceAsStream("assets/9-av-replay.png");
    private final Image pauseImg = new Image(pauseInputStream);
    private final Image playImg = new Image(playInputStream);
    private final Image replayImg = new Image(replayInputStream);
    @FXML
    private AnchorPane mainAnchor;
    //@FXML
    //private BorderPane mainBorder;
    //@FXML
    //private BorderPane header;
    //@FXML
    //private BorderPane footer;
    //@FXML
    //private StackPane progressPane;
    @FXML
    private StackPane timePane;
    //@FXML
    //private BorderPane rightControl;
    //@FXML
    //private StackPane volPane;
    //@FXML
    //private StackPane fullScreenPane;
    @FXML
    private StackPane mediaView;
    //@FXML
    //private ImageView starBt;
    //@FXML
    //private ImageView avatar;
    @FXML
    private Slider seek;
    @FXML
    private Label timeIn;
    @FXML
    private Slider volSLider;
    //@FXML
    //private ImageView fullBt;
    @FXML
    private ImageView ppBt;
    @FXML
    private ImageView onScreenPlay;
    //@FXML
    //private Label menuL;
    @FXML
    private Label title;
    private MediaPlayer mediaPlayer;
    private MediaViewScreen mediaViewScreen;
    private MediaPlayer.Status status;
    private Duration currentTime;
    private Duration duration;

    private void init() {
        final Media media = new Media(new Genesis().testURL);
        final File file = new File(new Genesis().testURL);

        mediaPlayer = new MediaPlayer(media);
        mediaViewScreen = new MediaViewScreen(mediaPlayer);
        status = mediaPlayer.getStatus();

        timePane.prefWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                timePane.setPrefWidth(number2.doubleValue());
            }
        });

        //Change the slider and time label to fit the media playing.
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                updateValues();
                setSeekSlider();
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {

                //Checks if media is at end. If true, media stops and rewind to the start.
                if ((int) Math.round(seek.getMax()) == (int) Math.round(mediaPlayer.getCurrentTime().toSeconds())) {
                    System.out.println("OK");


                    status = MediaPlayer.Status.STOPPED;
                    mediaPlayer.stop();
                    mediaPlayer.seek(mediaPlayer.getStartTime());

                    if (status == MediaPlayer.Status.PAUSED
                            || status == MediaPlayer.Status.READY) {

                        ppBt.setImage(playImg);
                    } else if (status == MediaPlayer.Status.STOPPED) {
                        ppBt.setImage(replayImg);
                    } else if (status == MediaPlayer.Status.PLAYING) {
                        ppBt.setImage(pauseImg);
                    }
                }
            }
        });

        mediaPlayer.setOnError(new Runnable() {
            @Override
            public void run() {

                setPpBt();
                setVolSlider();

                System.out.println("Something as gone wrong.");
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {

                System.out.println(file.getAbsoluteFile());

                if (file.getName().endsWith(".mp4")) {
                    duration = mediaPlayer.getMedia().getDuration();
                    setMediaView();
                    title.setText(file.getName());

                    mediaPlayer.setAutoPlay(true);
                    status = MediaPlayer.Status.PLAYING;
                    setSeekSlider();
                    setMainAnchorPane();

                    System.out.println("<< Path OK. >>");
                } else if (file.getName().endsWith(".mp3") ||
                        file.getName().endsWith(".wav")) {

                    duration = mediaPlayer.getMedia().getDuration();
                    //setMediaView();
                    title.setText(file.getName());

                    mediaPlayer.setAutoPlay(true);
                    status = MediaPlayer.Status.PLAYING;
                    setSeekSlider();
                    setMainAnchorPane();

                    System.out.println("<< Path OK. >>");
                } else {
                    System.out.println("<< Format not supported. >>");
                }
                setPpBt();
                setVolSlider();
            }
        });


    }

    private void setMainAnchorPane() {
        mainAnchor.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                //Add space bar pause and play events.
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    if (keyEvent.getCode() == KeyCode.SPACE && status == MediaPlayer.Status.UNKNOWN
                            || status == MediaPlayer.Status.HALTED) {
                        System.out.println("Player is in a bad or unknown state, can't play.");
                        return;
                    }

                    if (keyEvent.getCode() == KeyCode.SPACE && status == MediaPlayer.Status.PAUSED
                            || status == MediaPlayer.Status.STOPPED
                            || status == MediaPlayer.Status.READY) {

                        ppBt.setImage(pauseImg);
                        status = MediaPlayer.Status.PLAYING;
                        mediaPlayer.play();
                    } else if (keyEvent.getCode() == KeyCode.SPACE && status == MediaPlayer.Status.PLAYING) {

                        ppBt.setImage(playImg);
                        status = MediaPlayer.Status.PAUSED;
                        mediaPlayer.pause();
                    }
                }
            }
        });
    }

    private void setPpBt() {
        ppBt.setCursor(Cursor.HAND);

        if (status == MediaPlayer.Status.PLAYING) {
            ppBt.setImage(pauseImg);
        } else if (status == MediaPlayer.Status.READY || status == MediaPlayer.Status.PAUSED) {
            ppBt.setImage(playImg);
        } else if (status == MediaPlayer.Status.STOPPED) {
            ppBt.setImage(replayImg);
        }

        ppBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.PRIMARY && status == MediaPlayer.Status.PLAYING) {

                    mediaPlayer.pause();
                    status = MediaPlayer.Status.PAUSED;

                    if (status == MediaPlayer.Status.PLAYING) {
                        ppBt.setImage(pauseImg);
                    } else if (status == MediaPlayer.Status.READY || status == MediaPlayer.Status.PAUSED) {
                        ppBt.setImage(playImg);
                    } else if (status == MediaPlayer.Status.STOPPED) {
                        ppBt.setImage(replayImg);
                    }
                } else if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.PRIMARY && status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED) {

                    mediaPlayer.play();
                    status = MediaPlayer.Status.PLAYING;

                    if (status == MediaPlayer.Status.PLAYING) {
                        ppBt.setImage(pauseImg);
                    } else if (status == MediaPlayer.Status.READY || status == MediaPlayer.Status.PAUSED) {
                        ppBt.setImage(playImg);
                    } else if (status == MediaPlayer.Status.STOPPED) {
                        ppBt.setImage(replayImg);
                    }
                }
            }
        });
    }

    private void setSeekSlider() {

        seek.setMin(0.0);
        seek.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
        seek.setCursor(Cursor.HAND);

        seek.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (seek.isValueChanging()) {
                    //Sets time slider to seconds.
                    if (duration != null) {
                        mediaPlayer.seek(Duration.seconds(seek.getValue()));
                    }
                    updateValues();

                }
            }
        });


        seek.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    mediaPlayer.seek(Duration.seconds(seek.getValue()));

                    seek.setOnScroll(new EventHandler<ScrollEvent>() {
                        @Override
                        public void handle(ScrollEvent scrollEvent) {
                            mediaPlayer.seek(Duration.seconds(seek.getValue()));
                        }
                    });
                    updateValues();
                }
            }
        });

        seek.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    mediaPlayer.seek(Duration.seconds(seek.getValue() + 10.0));
                    updateValues();
                }

                if (keyEvent.getCode() == KeyCode.LEFT) {
                    mediaPlayer.seek(Duration.seconds(seek.getValue() - 10.0));
                    updateValues();
                }
            }
        });
    }

    private void setVolSlider() {
        volSLider.setCache(true);
        volSLider.setCursor(Cursor.HAND);
        volSLider.setMin(0.0);
        volSLider.setMax(100.0);

        volSLider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
            }
        });
        volSLider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (volSLider.isValueChanging()) {
                    mediaPlayer.setVolume(volSLider.getValue() / 100.0);
                }
            }
        });

        volSLider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.setVolume(volSLider.getValue() / 100.0);
            }
        });

        volSLider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.setVolume(volSLider.getValue() / 100.0);
            }
        });

        volSLider.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    mediaPlayer.seek(Duration.seconds(seek.getValue() + 10.0));
                    updateValues();
                }

                if (keyEvent.getCode() == KeyCode.LEFT) {
                    mediaPlayer.seek(Duration.seconds(seek.getValue() - 10.0));
                    updateValues();
                }
            }
        });


    }

    private void setMediaView() {
        mediaView.getChildren().remove(onScreenPlay);
        mediaViewScreen.prefHeightProperty().bind(mediaView.heightProperty());
        mediaViewScreen.prefWidthProperty().bind(mediaView.widthProperty());

        mediaView.setStyle("-fx-background-color: black;");

        mediaView.getChildren().add(mediaViewScreen);
    }

    //private void setMediaViewError() {

    //}

    void updateValues() {
        if (timeIn != null && seek != null && volSLider != null && duration != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    currentTime = mediaPlayer.getCurrentTime();
                    timeIn.setText(formatTime(currentTime, duration));
                    seek.setDisable(duration.isUnknown());
                    if (!seek.isDisabled() && duration.greaterThan(Duration.ZERO) && !seek.isValueChanging()) {

                        seek.setValue(currentTime.toSeconds());
                    }
                    if (!volSLider.isValueChanging()) {
                        volSLider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
                    }
                }
            });
        }
    }

    private String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,
                        durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",
                        elapsedMinutes, elapsedSeconds);
            }
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }
}
