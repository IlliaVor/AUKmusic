package AUKmusic.song;

import AUKmusic.FileIO;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Song implements Comparable<Song> {
    private static final String songsRootPath = "storage/songs/";
    private static final String songsListPath = songsRootPath + "songs";

    private String title;
    private String artist;
    private Duration duration;
    private String filePath;
    private String songPath;

    public Song(String title, String artist, Duration duration, String filePath) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.filePath = filePath;
        this.songPath = songsRootPath + title;
    }

    @Override
    public int compareTo(Song other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        return String.format("%s by %s", title, artist);
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        updateFileName(title);
        this.title = title;
        saveToFile();
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
        saveToFile();
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        saveToFile();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        saveToFile();
    }

    // File I/O Operations
    public static Song get(String title) throws IOException {
        Map<String, String> songData = FileIO.getAttributes(songsRootPath + title);
        return new Song(
                songData.get("Title"),
                songData.get("Artist"),
                Duration.parse(songData.get("Duration")),
                songData.get("FilePath")
        );
    }

    public static List<Song> list() throws IOException {
        File file = new File(songsListPath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists() || !parentDir.isDirectory()) {
            parentDir.mkdirs();
        }
        if (!file.exists() || file.isDirectory()) {
            file.createNewFile();
        }

        String songs = FileIO.readFileAsString(songsListPath);
        String[] songTitles = songs.split(System.lineSeparator());
        List<Song> songList = new ArrayList<>();
        for (String title : songTitles) {
            if (!title.isEmpty()) {
                songList.add(Song.get(title));
            }
        }
        return songList;
    }


    public static void addSong(Song song) throws IOException {
        // Check if the song already exists
        List<Song> existingSongs = list();
        for (Song existingSong : existingSongs) {
            if (existingSong.getTitle().equalsIgnoreCase(song.getTitle())) {
                throw new IOException("Song already exists.");
            }
        }
        song.saveToFile();
        FileIO.appendLineToFile(songsListPath, song.getTitle());
    }

    private void saveToFile() {
        try {
            List<String> existingSongs = FileIO.readLinesFromFile(songsListPath);
            if (!existingSongs.contains(this.title)) {
                FileIO.appendLineToFile(songsListPath, this.title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(this.title).append(System.lineSeparator());
        sb.append("Artist: ").append(this.artist).append(System.lineSeparator());
        sb.append("Duration: ").append(this.duration).append(System.lineSeparator());
        sb.append("FilePath: ").append(this.filePath).append(System.lineSeparator());

        try {
            FileIO.writeStringToFile(this.songPath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() throws IOException {
        FileIO.deleteFile(this.songPath);
        FileIO.removeLineFromFile(songsListPath, this.title);
    }

    private void updateFileName(String newTitle) {
        File oldFile = new File(songsRootPath + this.title);
        File newFile = new File(songsRootPath + newTitle);
        if (oldFile.renameTo(newFile)) {
            this.songPath = songsRootPath + newTitle;
        } else {
            System.out.println("Failed to rename the file");
        }
    }

    public void playSong() {
        String mediaFilePath = new File(filePath).toURI().toString();

        Media media = new Media(mediaFilePath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}