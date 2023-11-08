import spotifycharts.*;

import java.util.ArrayList;
import java.util.List;

public class SpotifyChartsMain {
    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Spotify Charts Calculator\n");

        long startTime;
        long endTime;

        ChartsCalculator chartsCalculator = new ChartsCalculator(20060423L);
//        chartsCalculator.registerStreamedSongs(263);
//        chartsCalculator.showResults();


        System.out.println(sum(100));

    }

    public static int sum(int k) {
        if (k < 50000000) {
            long startTime;
            long endTime;
            ChartsCalculator chartsCalculator = new ChartsCalculator(20060423L);

            List<Song> songs = chartsCalculator.registerStreamedSongs(k);

            Sorter<Song> sorter = new SongSorter();
            startTime = System.currentTimeMillis();
            sorter.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);
            endTime = System.currentTimeMillis();
            System.out.println(
                    "Total execution time to add new item " + (endTime - startTime) + "ms for items of size " + songs.size()
            );
            return sum(k * 2);
        } else {
            return 0;
        }
    }


}
