package AUKmusic.mediaplayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.scene.media.AudioEqualizer;
import AUKmusic.song.Song;
import AUKmusic.playlist.Playlist;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MediaPlayerController {

    @FXML
    private ListView<Playlist> playlistListView;

    @FXML
    private ListView<Song> trackListView;

    @FXML
    private ImageView artworkImageView;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider bassSlider;

    @FXML
    private Slider midSlider;

    @FXML
    private Slider trebleSlider;

    @FXML
    private Label songTitleLabel;

    @FXML
    private Label songArtistLabel;

    @FXML
    private Button prevTrackButton;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button nextTrackButton;

    private MediaPlayer mediaPlayer;
    private Playlist currentPlaylist;
    private AudioEqualizer audioEqualizer;

    @FXML
    public void initialize() {
        try {
            List<Playlist> playlists = Playlist.list();
            playlistListView.getItems().addAll(playlists);
        } catch (IOException e) {
            showErrorAlert("Error Loading Playlists", "Failed to load playlists.", e.getMessage());
        }

        // Add listener for playlist selection change
        playlistListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentPlaylist = newValue;
                trackListView.getItems().setAll(newValue.getSongs().toList());
            }
        });

        // Add listener for volume change
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });

        // Add listener for equalizer change
        bassSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setBassLevel(newValue.doubleValue());
        });

        midSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setMidLevel(newValue.doubleValue());
        });

        trebleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setTrebleLevel(newValue.doubleValue());
        });

        playPauseButton.setOnAction(event -> {
            Song selectedSong = trackListView.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                playTrack(selectedSong);
            } else {
                showErrorAlert("No Track Selected", "Please select a track to play.", "");
            }
        });

        stopButton.setOnAction(event -> stopTrack());

        nextTrackButton.setOnAction(event -> playNextTrack());

        prevTrackButton.setOnAction(event -> playPrevTrack());
    }

    @FXML
    private void playTrack(Song song) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            String filePath = song.getFilePath();
            if (!new File(filePath).exists()) {
                showErrorAlert("File Not Found", "The media file does not exist: " + filePath, "");
                return;
            }

            Media media = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            media.setOnError(() -> {
                String error = media.getError() != null ? media.getError().getMessage() : "Unknown error";
                showErrorAlert("Media Error", "Failed to load media.", error);
            });

            mediaPlayer.setOnError(() -> {
                String error = mediaPlayer.getError() != null ? mediaPlayer.getError().getMessage() : "Unknown error";
                showErrorAlert("MediaPlayer Error", "Failed to play media.", error);
            });

            mediaPlayer.setOnReady(() -> {
                audioEqualizer = mediaPlayer.getAudioEqualizer();
                if (audioEqualizer == null) {
                    showErrorAlert("Equalizer Not Supported", "Audio equalizer is not supported by the media player.", "");
                }
                mediaPlayer.play();
            });

            mediaPlayer.setOnEndOfMedia(this::playNextTrack);
            updateArtwork(song.getAlbumArt());
            songTitleLabel.setText(song.getTitle());
            songArtistLabel.setText(song.getArtist());

        } catch (Exception e) {
            showErrorAlert("Playback Error", "Failed to play track.", e.getMessage());
        }
    }

    private void playNextTrack() {
        int currentIndex = trackListView.getSelectionModel().getSelectedIndex();
        if (currentIndex < trackListView.getItems().size() - 1) {
            trackListView.getSelectionModel().selectNext();
            playTrack(trackListView.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void playPrevTrack() {
        int currentIndex = trackListView.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            trackListView.getSelectionModel().selectPrevious();
            playTrack(trackListView.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void stopTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    private void skipTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playNextTrack();
        }
    }

    @FXML
    private void increaseVolume() {
        if (mediaPlayer != null) {
            double currentVolume = mediaPlayer.getVolume();
            double newVolume = Math.min(currentVolume + 0.1, 1.0);
            mediaPlayer.setVolume(newVolume);
            volumeSlider.setValue(newVolume);
        }
    }

    @FXML
    private void decreaseVolume() {
        if (mediaPlayer != null) {
            double currentVolume = mediaPlayer.getVolume();
            double newVolume = Math.max(currentVolume - 0.1, 0.0);
            mediaPlayer.setVolume(newVolume);
            volumeSlider.setValue(newVolume);
        }
    }

    private void updateArtwork(Image image) {
        artworkImageView.setImage(image);
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setBassLevel(double level) {
        if (mediaPlayer != null && audioEqualizer != null) {
            EqualizerBand bassBand = audioEqualizer.getBands().get(0); // Adjusting the first band for bass
            bassBand.setGain(level);
        }
    }

    private void setMidLevel(double level) {
        if (mediaPlayer != null && audioEqualizer != null) {
            EqualizerBand midBand = audioEqualizer.getBands().get(1); // Adjusting the second band for mid
            midBand.setGain(level);
        }
    }

    private void setTrebleLevel(double level) {
        if (mediaPlayer != null && audioEqualizer != null) {
            EqualizerBand trebleBand = audioEqualizer.getBands().get(2); // Adjusting the third band for treble
            trebleBand.setGain(level);
        }
    }
}

