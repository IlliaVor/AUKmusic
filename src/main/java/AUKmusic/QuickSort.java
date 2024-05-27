package AUKmusic;

import AUKmusic.song.Song;
import java.util.List;

public class QuickSort {

    public static List<Song> sort(List<Song> songs) {
        if (songs == null || songs.size() <= 1) {
            return songs;
        }
        quickSort(songs, 0, songs.size() - 1);
        return songs;
    }

    private static void quickSort(List<Song> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private static int partition(List<Song> list, int low, int high) {
        Song pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).getTitle().compareToIgnoreCase(pivot.getTitle()) <= 0) {
                i++;
                Song temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        Song temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
    }
}
