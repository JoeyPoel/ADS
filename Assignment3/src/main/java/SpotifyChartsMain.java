import spotifycharts.*;

import java.util.ArrayList;
import java.util.List;

public class SpotifyChartsMain {
    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Spotify Charts Calculator\n");


        sum(100);

    }

    public static int sum(int k) {
        if (k < 50000000) {
            long startTime;
            long endTime;
            boolean alreadyPrinted = false;

            ChartsCalculator chartsCalculator = new ChartsCalculator(20060423L);

            List<Song> songs = chartsCalculator.registerStreamedSongs(k);

            Sorter<Song> sorter = new SongSorter();
            startTime = System.currentTimeMillis();
            sorter.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);
            System.gc();
            endTime = System.currentTimeMillis();

            long timeResult = endTime - startTime;

            if (timeResult > 20000) {
                System.out.println("Execution time exceeded 20 seconds");
                return 0;
            } else {
                if (alreadyPrinted) {

                    System.out.println(
                            "Total execution time to add new item " + timeResult + "ms for items of size " + songs.size()
                    );

                    alreadyPrinted = true;



                }

                return sum(k * 2);
            }
        } else {
            return 0;
        }
    }


}
