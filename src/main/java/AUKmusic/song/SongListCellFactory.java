package AUKmusic.song;

import AUKmusic.song.Song;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class SongListCellFactory implements Callback<ListView<Song>, ListCell<Song>> {

    @Override
    public ListCell<Song> call(ListView<Song> param) {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);

                if (empty || song == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(song.getTitle() + " by " + song.getArtist());
                    if (song.getAlbumArt() != null) {
                        imageView.setImage(song.getAlbumArt());
                        imageView.setFitHeight(50); // Set the height of the album art
                        imageView.setPreserveRatio(true); // Preserve the aspect ratio
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };
    }
}
