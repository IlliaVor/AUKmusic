package AUKmusic.playlist;

import AUKmusic.SongLinkedList;
import AUKmusic.playlist.*;
import AUKmusic.song.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Optional;
import AUKmusic.QuickSort;
import AUKmusic.Search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class PlaylistController {

    @FXML
    private ListView<Playlist> playlistListView;

    @FXML
    private ListView<Song> songListView;

    @FXML
    private TextField playlistNameField;

    @FXML
    private Button addPlaylistButton;

    @FXML
    private Button deletePlaylistButton;

    @FXML
    private Button addSongToPlaylistButton;

    @FXML
    private Button removeSongFromPlaylistButton;

    @FXML
    private Button editPlaylistButton;

    @FXML
    private Button playPlaylistButton;

    @FXML
    private Button sortButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<Song> searchResultsListView;

    private ObservableList<Playlist> playlistList;

    private Playlist currentPlaylist;

    public void initialize() {
        try {
            playlistList = FXCollections.observableArrayList(Playlist.list());
            playlistListView.setItems(playlistList);

            playlistListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    currentPlaylist = newValue;
                    songListView.setItems(FXCollections.observableArrayList(currentPlaylist.getSongs().toList()));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddPlaylist(ActionEvent event) {
        String playlistName = playlistNameField.getText().trim();
        if (!playlistName.isEmpty()) {
            Playlist newPlaylist = new Playlist(playlistName, new SongLinkedList());
            newPlaylist.saveToFile();
            playlistList.add(newPlaylist);
            playlistNameField.clear();
        }
    }

    @FXML
    private void handleDeletePlaylist(ActionEvent event) {
        Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            try {
                selectedPlaylist.delete();
                playlistList.remove(selectedPlaylist);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddSongToPlaylist(ActionEvent event) {
        Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedSong != null) {
            selectedPlaylist.addSong(selectedSong);
            songListView.setItems(FXCollections.observableArrayList(selectedPlaylist.getSongs().toList()));
        }
    }

    @FXML
    private void handleRemoveSongFromPlaylist(ActionEvent event) {
        Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedSong != null) {
            selectedPlaylist.removeSong(selectedSong);
            songListView.setItems(FXCollections.observableArrayList(selectedPlaylist.getSongs().toList()));
        }
    }

    @FXML
    private void handleEditPlaylist(ActionEvent event) {
        Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
        String newName = playlistNameField.getText().trim();
        if (selectedPlaylist != null && !newName.isEmpty()) {
            selectedPlaylist.setName(newName);
            playlistListView.refresh();
        }
    }

    @FXML
    private void handlePlayPlaylist(ActionEvent event) {
        Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            selectedPlaylist.getSongs().forEach(Song::playSong);
        }
    }

    @FXML
    private void handleSearch() {
        if (currentPlaylist != null) {
            String query = searchTextField.getText().trim();
            List<Song> songs = currentPlaylist.getSongs().toList();
            Song result = Search.findSongByTitle(songs, query);
            searchResultsListView.getItems().clear();
            if (result != null) {
                searchResultsListView.getItems().add(result);
            } else {
                javafx.util.Duration javafxDuration = Duration.ZERO;
                long millis = (long) javafxDuration.toMillis();
                java.time.Duration javaDuration = java.time.Duration.ofMillis(millis);
                searchResultsListView.getItems().add(new Song("No results found", "", javaDuration, ""));
            }
        }
    }

    @FXML
    private void handleSort() {
        if (currentPlaylist != null) {
            QuickSort.sort(currentPlaylist.getSongs().toList());
            songListView.setItems(FXCollections.observableArrayList(currentPlaylist.getSongs().toList()));
        }
    }
}


