package AUKmusic.playlist;

import AUKmusic.SongLinkedList;
import AUKmusic.song.Song;
import AUKmusic.QuickSort;
import AUKmusic.Search;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import AUKmusic.song.SongListCellFactory;

import java.io.IOException;
import java.util.List;

public class PlaylistController {

    @FXML
    private ListView<Playlist> playlistListView;

    @FXML
    private ListView<Song> playlistSongListView;

    @FXML
    private ListView<Song> allSongsListView;

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
    private Button sortButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<Song> searchResultsListView;

    private ObservableList<Playlist> playlistList;

    private Playlist currentPlaylist;

    private ObservableList<Song> songLibrary;

    public void initialize() {
        try {
            // Initialize playlist list
            playlistList = FXCollections.observableArrayList(Playlist.list());
            playlistListView.setItems(playlistList);

            playlistSongListView.setCellFactory(new SongListCellFactory());
            allSongsListView.setCellFactory(new SongListCellFactory());
            searchResultsListView.setCellFactory(new SongListCellFactory());

            // Set listener for playlist selection changes
            playlistListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    currentPlaylist = newValue;
                    playlistSongListView.setItems(FXCollections.observableArrayList(currentPlaylist.getSongs().toList()));
                }
            });

            // Load all songs into the song library
            songLibrary = FXCollections.observableArrayList(Song.list());
            allSongsListView.setItems(songLibrary);
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
        Song selectedSong = allSongsListView.getSelectionModel().getSelectedItem();
        if (currentPlaylist != null && selectedSong != null) {
            currentPlaylist.addSong(selectedSong);
            playlistSongListView.setItems(FXCollections.observableArrayList(currentPlaylist.getSongs().toList()));
        }
    }

    @FXML
    private void handleRemoveSongFromPlaylist(ActionEvent event) {
        Song selectedSong = playlistSongListView.getSelectionModel().getSelectedItem();
        if (currentPlaylist != null && selectedSong != null) {
            currentPlaylist.removeSong(selectedSong);
            playlistSongListView.setItems(FXCollections.observableArrayList(currentPlaylist.getSongs().toList()));
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
    private void handleSearch() {
        if (currentPlaylist != null) {
            String query = searchTextField.getText().trim();
            List<Song> songs = currentPlaylist.getSongs().toList();
            List<Song> results = Search.findSongsByTitle(songs, query);
            searchResultsListView.setItems(FXCollections.observableArrayList(results));
        }
    }

    @FXML
    private void handleSort() {
        if (currentPlaylist != null) {
            List<Song> sortedSongs = QuickSort.sort(currentPlaylist.getSongs().toList());
            playlistSongListView.setItems(FXCollections.observableArrayList(sortedSongs));
        }
    }
}


