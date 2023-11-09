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


            Sorter<Song> sorter = new SongSorter();
            Sorter<Song> sorter2 = new SongSorter();
            Sorter<Song> sorter3 = new SongSorter();
            Sorter<Song> sorter4 = new SongSorter();

            for (int i = 1; i <= 10; i++) {
                long totalTime = 0;

                for (int j = 0; j < 10; j++) {


                ChartsCalculator chartsCalculator = new ChartsCalculator(i * j);

                List<Song> songs = chartsCalculator.registerStreamedSongs(k);


                // Test 1
                startTime = System.currentTimeMillis();
                sorter.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);
                System.gc();
                endTime = System.currentTimeMillis();
                long timeResult = endTime - startTime;
                totalTime += timeResult;

                // Test 2
                startTime = System.currentTimeMillis();
                sorter2.quickSort(songs, Song::compareByHighestStreamsCountTotal);
                System.gc();
                endTime = System.currentTimeMillis();
                long timeResult2 = endTime - startTime;
                totalTime += timeResult2;


                    // Test 3
                startTime = System.currentTimeMillis();
                sorter3.topsHeapSort(5, songs, Song::compareByHighestStreamsCountTotal);
                System.gc();
                endTime = System.currentTimeMillis();
                long timeResult3 = endTime - startTime;
                totalTime += timeResult3;


                    // Test 4
                startTime = System.currentTimeMillis();
                sorter4.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);
                System.gc();
                endTime = System.currentTimeMillis();
                long timeResult4 = endTime - startTime;
                totalTime += timeResult4;


                if (timeResult > 20000 && timeResult2 > 20000 && timeResult3 > 20000 && timeResult4 > 20000) {
                    System.out.println("Execution time exceeded 20 seconds");
                    return 0;
                }
            }
                if (!alreadyPrinted) {


                    System.out.println(
                            "\nAverage execution time for Algorithm 1 " + totalTime / 10 + "ms"
                    );
                    System.out.println(
                            "\nAverage execution time for Algorithm 2 " + totalTime / 10 + "ms"
                    );
                    System.out.println(
                            "\nAverage execution time for Algorithm 3 " + totalTime / 10 + "ms"
                    );
                    System.out.println(
                            "\nAverage execution time for Algorithm 4 " + totalTime / 10 + "ms"
                    );

                    alreadyPrinted = true;
                }


                return sum(k * 2);
            }
        } else {
            return 0;
        }
        return 0;
    }


}
