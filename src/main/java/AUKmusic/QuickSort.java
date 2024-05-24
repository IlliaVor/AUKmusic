package AUKmusic;

import AUKmusic.song.Song;
import java.util.List;

public class QuickSort {

    public static void sort(List<Song> songs) {
        if (songs == null || songs.size() == 0) {
            return;
        }
        quickSort(songs, 0, songs.size() - 1);
    }

    private static void quickSort(List<Song> songs, int low, int high) {
        if (low < high) {
            int pi = partition(songs, low, high);
            quickSort(songs, low, pi - 1);
            quickSort(songs, pi + 1, high);
        }
    }

    private static int partition(List<Song> songs, int low, int high) {
        Song pivot = songs.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (songs.get(j).getTitle().compareToIgnoreCase(pivot.getTitle()) < 0) {
                i++;
                swap(songs, i, j);
            }
        }
        swap(songs, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Song> songs, int i, int j) {
        Song temp = songs.get(i);
        songs.set(i, songs.get(j));
        songs.set(j, temp);
    }
}
