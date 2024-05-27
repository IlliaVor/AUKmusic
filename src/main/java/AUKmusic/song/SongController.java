package AUKmusic.song;

import com.mpatric.mp3agic.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.Duration;


public class SongController {

    @FXML
    private ListView<Song> songListView;

    @FXML
    private TextField titleField;

    @FXML
    private TextField artistField;

    @FXML
    private TextField durationField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button playButton;

    @FXML
    private Button selectFileButton;

    private ObservableList<Song> songList;

    private File selectedFile;

    public void initialize() throws IOException {
        songList = FXCollections.observableArrayList(Song.list());
        songListView.setItems(songList);
        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                titleField.setText(newValue.getTitle());
                artistField.setText(newValue.getArtist());
                durationField.setText(newValue.getDuration().toString());
            }
        });
    }

    @FXML
    private void handleAddSong(ActionEvent event) {
        String title = titleField.getText();
        String artist = artistField.getText();
        String durationText = durationField.getText();

        if (title.isEmpty() || artist.isEmpty() || durationText.isEmpty() || selectedFile == null) {
            showAlert("Validation Error", "Please fill in all fields and select a file.");
            return;
        }

        try {
            Duration duration = Duration.parse(durationText);
            String filePath = selectedFile.getAbsolutePath();

            Song newSong = new Song(title, artist, duration, filePath,null);
            Song.addSong(newSong);
            songList.add(newSong);

            clearFields();
            selectedFile = null;  // Reset the selected file after adding the song

        } catch (IOException e) {
            showAlert("Error", "Failed to add song.");
            e.printStackTrace();
        }
    }



    @FXML
    private void handleSelectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Song File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                Mp3File mp3File = new Mp3File(selectedFile);
                String title = null;
                String artist = null;
                long durationInSeconds = mp3File.getLengthInSeconds();
                Duration duration = Duration.ofSeconds(durationInSeconds);

                if (mp3File.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                    title = id3v1Tag.getTitle();
                    artist = id3v1Tag.getArtist();
                } else if (mp3File.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                    title = id3v2Tag.getTitle();
                    artist = id3v2Tag.getArtist();
                }

                if (title == null || title.isEmpty()) {
                    title = selectedFile.getName();
                }
                if (artist == null) {
                    artist = "Unknown Artist";
                }

                titleField.setText(title);
                artistField.setText(artist);
                durationField.setText(duration.toString());

                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                System.out.println("Title: " + title);
                System.out.println("Artist: " + artist);
                System.out.println("Duration: " + durationField.getText());

            } catch (Exception e) {
                showAlert("Error", "Failed to read file metadata.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteSong(ActionEvent event) {
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            try {
                selectedSong.delete();
                songList.remove(selectedSong);
            } catch (IOException e) {
                showAlert("Error", "Failed to delete song.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handlePlaySong(ActionEvent event) {
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            selectedSong.playSong();
        }
    }

    private void clearFields() {
        titleField.clear();
        artistField.clear();
        durationField.clear();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}