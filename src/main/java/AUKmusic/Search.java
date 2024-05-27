package AUKmusic;

import AUKmusic.song.Song;
import java.util.List;
import java.util.stream.Collectors;

public class Search {

    public static List<Song> findSongsByTitle(List<Song> songs, String title) {
        return songs.stream()
                .filter(song -> song.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
}