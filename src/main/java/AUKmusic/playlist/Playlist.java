package AUKmusic.playlist;

import AUKmusic.song.SongListCellFactory;
import AUKmusic.SongLinkedList;
import AUKmusic.song.Song;
import AUKmusic.FileIO;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Playlist implements Comparable<Playlist> {
    private static final String playlistsRootPath = "storage/playlists/";
    private static final String playlistsListPath = playlistsRootPath + "playlists";

    private String name;
    private SongLinkedList songs;
    private String playlistPath;

    public Playlist(String name, SongLinkedList songs) {
        this.name = name;
        this.songs = songs;
        this.playlistPath = playlistsRootPath + name;
    }

    @Override
    public int compareTo(Playlist other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        updateFileName(name);
        this.name = name;
        saveToFile();
    }

    public SongLinkedList getSongs() {
        return songs;
    }

    public void setSongs(SongLinkedList songs) {
        this.songs = songs;
        saveToFile();
    }

    public void addSong(Song song) {
        this.songs.add(song);
        saveToFile();
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
        saveToFile();
    }

    // File I/O Operations
    public static Playlist get(String name) throws IOException {
        Map<String, String> playlistData = FileIO.getAttributes(playlistsRootPath + name);
        SongLinkedList songList = new SongLinkedList();
        if (playlistData.containsKey("Songs")) {
            String[] songTitles = playlistData.get("Songs").split(";");
            for (String songTitle : songTitles) {
                if (!songTitle.isEmpty()) {
                    songList.add(Song.get(songTitle));
                }
            }
        }
        return new Playlist(playlistData.get("Name"), songList);
    }

    public static List<Playlist> list() throws IOException {
        File file = new File(playlistsListPath);
        File parentDir = file.getParentFile();

        if (!parentDir.exists() || !parentDir.isDirectory()) {
            parentDir.mkdirs();
        }
        if (!file.exists() || file.isDirectory()) {
            file.createNewFile();
        }

        List<String> playlistNames = FileIO.readLinesFromFile(playlistsListPath);
        List<Playlist> playlists = new ArrayList<>();
        for (String name : playlistNames) {
            if (!name.isEmpty()) {
                playlists.add(Playlist.get(name));
            }
        }
        return playlists;
    }

    public void saveToFile() {
        try {
            List<String> existingPlaylists = FileIO.readLinesFromFile(playlistsListPath);
            if (!existingPlaylists.contains(this.name)) {
                FileIO.appendLineToFile(playlistsListPath, this.name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(this.name).append(System.lineSeparator());

        StringBuilder songsList = new StringBuilder();
        for (Song song : this.songs) {
            songsList.append(song.getTitle()).append(";");
        }
        sb.append("Songs: ").append(songsList).append(System.lineSeparator());

        try {
            FileIO.writeStringToFile(this.playlistPath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() throws IOException {
        FileIO.deleteFile(this.playlistPath);
        FileIO.removeLineFromFile(playlistsListPath, this.name);
    }

    private void updateFileName(String newName) {
        File oldFile = new File(playlistsRootPath + this.name);
        File newFile = new File(playlistsRootPath + newName);
        if (oldFile.renameTo(newFile)) {
            this.playlistPath = playlistsRootPath + newName;
        } else {
            System.out.println("Failed to rename the file");
        }
    }
}