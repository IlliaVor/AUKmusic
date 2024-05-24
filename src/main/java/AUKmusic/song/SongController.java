package AUKmusic.song;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;

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

    private ObservableList<Song> songList;

    public void initialize() throws IOException {
        songList = FXCollections.observableArrayList(Song.list());
        songListView.setItems(songList);

        // Set listener for song selection
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
        try {
            String title = titleField.getText();
            String artist = artistField.getText();
            Duration duration = Duration.parse(durationField.getText());

            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String filePath = selectedFile.getAbsolutePath();

                Song newSong = new Song(title, artist, duration, filePath);
                Song.addSong(newSong);
                songList.add(newSong);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}