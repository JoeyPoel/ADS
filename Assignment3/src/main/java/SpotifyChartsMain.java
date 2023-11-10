import spotifycharts.*;

import java.util.ArrayList;
import java.util.List;

public class SpotifyChartsMain {
    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Spotify Charts Calculator\n");
        evaluateSortingAlgorithms();
    }

    public static void evaluateSortingAlgorithms() {
        int initialSize = 100;
        long maxSongs = 5000000;
        long maxTime = 20000; // 20 seconds

        while (initialSize <= maxSongs) {
            long startTime, endTime, totalExecutionTime;
            Sorter<Song> sorter = new SongSorter();

            for (int algorithm = 1; algorithm <= 4; algorithm++) {
                boolean algorithmExceededTime = false;
                totalExecutionTime = 0;

                System.out.println("-------------------------------------------------------");
                System.out.println("\nAlgorithm " + algorithm + ":");

                for (int i = 0; i < 10; i++) {
                    ChartsCalculator chartsCalculator = new ChartsCalculator(algorithm * i);

                    List<Song> songs = chartsCalculator.registerStreamedSongs(initialSize);

                    startTime = System.currentTimeMillis();
                    switch (algorithm) {
                        case 1:
                            sorter.selInsBubSort(new ArrayList<>(songs), Song::compareByHighestStreamsCountTotal);
                            break;
                        case 2:
                            sorter.quickSort(new ArrayList<>(songs), Song::compareByHighestStreamsCountTotal);
                            break;
                        case 3:
                            sorter.topsHeapSort(5, new ArrayList<>(songs), Song::compareByHighestStreamsCountTotal);
                            break;
                        case 4:
                            sorter.selInsBubSort(new ArrayList<>(songs), Song::compareByHighestStreamsCountTotal);
                            break;
                    }

                    System.gc();

                    endTime = System.currentTimeMillis();
                    long executionTime = endTime - startTime;

                    totalExecutionTime += executionTime;

                    if (executionTime > maxTime) {
                        algorithmExceededTime = true;
                        break;
                    }

                    System.out.println("Run " + (i + 1) + ": " + executionTime + "ms");

                    if (algorithmExceededTime) {
                        System.out.println("Algorithm " + algorithm + " exceeded 20 seconds");
                        System.out.println("Total time: " + totalExecutionTime + "ms");
                        break;
                    }
                }
            }

            initialSize *= 2;
        }
    }
}
