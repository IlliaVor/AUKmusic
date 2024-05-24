package AUKmusic;

import AUKmusic.song.Song;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SongLinkedList implements Iterable<Song> {

    private Node head;
    private int size;

    private class Node {
        Song song;
        Node next;

        Node(Song song) {
            this.song = song;
        }
    }

    public SongLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(Song song) {
        Node newNode = new Node(song);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public boolean remove(Song song) {
        if (head == null) return false;

        if (head.song.equals(song)) {
            head = head.next;
            size--;
            return true;
        }

        Node current = head;
        while (current.next != null && !current.next.song.equals(song)) {
            current = current.next;
        }

        if (current.next == null) return false;

        current.next = current.next.next;
        size--;
        return true;
    }
    public List<Song> toList() {
        List<Song> list = new ArrayList<>();
        for (Song song : this) {
            list.add(song);
        }
        return list;
    }

    public Song get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.song;
    }

    @Override
    public Iterator<Song> iterator() {
        return new Iterator<Song>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Song next() {
                if (!hasNext()) throw new NoSuchElementException();
                Song song = current.song;
                current = current.next;
                return song;
            }
        };
    }

    // Additional utility methods can be added here as needed
}
