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


            Sorter<Song> sorter = new SongSorter();


            for (int algorithm = 1; algorithm <= 4; algorithm++) {
                boolean algorithmExceededTime = false;
                long totalExecutionTime = 0;


                for (int i = 0; i < 10; i++) {


                    ChartsCalculator chartsCalculator = new ChartsCalculator(algorithm * i);

                    List<Song> songs = chartsCalculator.registerStreamedSongs(k);

                    startTime = System.currentTimeMillis();
                    switch (algorithm) {
                        case 1:
                            sorter.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);
                            break;
                        case 2:
                            sorter.quickSort(songs, Song::compareByHighestStreamsCountTotal);
                            break;
                        case 3:
                            sorter.topsHeapSort(5, songs, Song::compareByHighestStreamsCountTotal);
                            break;
                        case 4:
                            sorter.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);
                            break;
                    }

                    System.gc();

                    endTime = System.currentTimeMillis();
                    long executionTime = endTime - startTime;

                    totalExecutionTime += executionTime;

                    if (executionTime > 20000) {
                        algorithmExceededTime = true;
                        break;
                    }

                    System.out.println("-------------------------------------------------------");
                    System.out.println("\nAlgorithm " + algorithm + ":");
                     System.out.println("Run " + i + ": " + executionTime + "ms");

                    if (algorithmExceededTime) {
                        System.out.println("Algorithm " + algorithm + " exceeded 20 seconds");
                        System.out.println(totalExecutionTime);
                    }

                    k = k * 2;


                }
            }
        }
        return 0;

    }
}
