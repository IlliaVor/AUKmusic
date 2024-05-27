package AUKmusic.song;

import AUKmusic.FileIO;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Path;
import AUKmusic.song.SongListCellFactory;

public class Song implements Comparable<Song> {
    private static final String songsRootPath = "storage/songs/";
    private static final String songsListPath = songsRootPath + "songs";

    private String title;
    private String artist;
    private Duration duration;
    private String filePath;
    private String songPath;
    private Image albumArt;

    public Song(String title, String artist, Duration duration, String filePath, byte[] albumArtBytes) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.filePath = filePath;
        this.songPath = songsRootPath + title;
        if (albumArtBytes != null) {
            this.albumArt = new Image(new ByteArrayInputStream(albumArtBytes));
        }
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

    public Image getAlbumArt() {
        return albumArt;
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
        String filePath = songData.get("FilePath");
        byte[] albumArtBytes = extractAlbumArt(filePath); // Extract album art bytes

        return new Song(
                songData.get("Title"),
                songData.get("Artist"),
                Duration.parse(songData.get("Duration")),
                filePath,
                albumArtBytes // Pass album art bytes to the constructor
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

    public static Song loadSongWithAlbumArt(String filePath) throws Exception {
        Mp3File mp3file = new Mp3File(filePath);
        String title = mp3file.getId3v2Tag().getTitle();
        String artist = mp3file.getId3v2Tag().getArtist();
        Duration duration = Duration.ofSeconds(mp3file.getLengthInSeconds());
        byte[] albumArtBytes = mp3file.getId3v2Tag().getAlbumImage();

        return new Song(title, artist, duration, filePath, albumArtBytes);
    }

    private static byte[] extractAlbumArt(String filePath) {
        try {
            Mp3File mp3File = new Mp3File(filePath);
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                return id3v2Tag.getAlbumImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}