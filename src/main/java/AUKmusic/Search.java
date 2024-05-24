package AUKmusic;

import AUKmusic.song.Song;
import java.util.List;

public class Search {

    public static Song findSongByTitle(List<Song> songs, String title) {
        int left = 0;
        int right = songs.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Song midSong = songs.get(mid);
            int cmp = midSong.getTitle().compareToIgnoreCase(title);

            if (cmp == 0) {
                return midSong;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }
}