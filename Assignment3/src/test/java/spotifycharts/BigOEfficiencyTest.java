package spotifycharts;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BigOEfficiencyTest {

    int initialSize = 100;
    long maxSongs = 5000000;
    long maxTime = 20000; // 20 seconds

    long startTime, endTime, totalExecutionTime;

    Sorter<Song> sorter = new SongSorter();



    @Test
    void AlgorithmQuickSort() {

        while (initialSize <= maxSongs) {

            boolean exceededTime = false;


            totalExecutionTime = 0;

                System.out.println("-------------------------------------------------------");
                System.out.println("\nAlgorithm 1 with initialSize:" + initialSize );

                for (int i = 0; i < 10; i++) {
                    ChartsCalculator chartsCalculator = new ChartsCalculator(i);

                    List<Song> songs = chartsCalculator.registerStreamedSongs(initialSize);

                    List<Song> songList = new ArrayList<>(songs);

                    startTime = System.currentTimeMillis();
                    sorter.quickSort(songList, Song::compareByHighestStreamsCountTotal);

                    System.gc();

                    endTime = System.currentTimeMillis();
                    long executionTime = endTime - startTime;

                    totalExecutionTime += executionTime;

                    if (executionTime > maxTime) {
                        System.out.println("Total time: " + totalExecutionTime + "ms");
                        exceededTime = true;
                        break;
                    }
                }

            if (exceededTime) {
                System.out.println("Execution Time exceeded 20 seconds!!");
                break;
            } else {
                System.out.println("Average: " + totalExecutionTime / 10 + "ms");

                initialSize *= 2;
            }
        }
    }

    @Test
    void AlgorithmSelInsBubSort() {

        while (initialSize <= maxSongs) {

            boolean exceededTime = false;

            totalExecutionTime = 0;

            System.out.println("-------------------------------------------------------");
            System.out.println("\nAlgorithm 2 with initialSize:" + initialSize);

            for (int i = 0; i < 10; i++) {
                ChartsCalculator chartsCalculator = new ChartsCalculator(i);

                List<Song> songs = chartsCalculator.registerStreamedSongs(initialSize);

                List<Song> songList = new ArrayList<>(songs);

                startTime = System.currentTimeMillis();
                sorter.selInsBubSort(songList, Song::compareByHighestStreamsCountTotal);

                System.gc();

                endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;

                totalExecutionTime += executionTime;

                if (executionTime > maxTime) {
                    System.out.println("Total time: " + totalExecutionTime + "ms");
                    exceededTime = true;
                    break;
                }
            }

            if (exceededTime) {
                System.out.println("Execution Time exceeded 20 seconds!!");
                break;
            } else {
                System.out.println("Average: " + totalExecutionTime / 10 + "ms");

                initialSize *= 2;
            }
        }
    }

    @Test
    void AlgorithmTopHeapsSort() {

            while (initialSize <= maxSongs) {

                boolean exceededTime = false;

                totalExecutionTime = 0;

                System.out.println("-------------------------------------------------------");
                System.out.println("\nAlgorithm 3 with initialSize:" + initialSize);

                for (int i = 0; i < 10; i++) {
                    ChartsCalculator chartsCalculator = new ChartsCalculator(i);

                    List<Song> songs = chartsCalculator.registerStreamedSongs(initialSize);

                    List<Song> songList = new ArrayList<>(songs);

                    startTime = System.currentTimeMillis();
                    sorter.topsHeapSort(1, songList, Song::compareByHighestStreamsCountTotal);

                    System.gc();

                    endTime = System.currentTimeMillis();
                    long executionTime = endTime - startTime;

                    totalExecutionTime += executionTime;


                    if (executionTime > maxTime) {
                        System.out.println("Total time: " + totalExecutionTime + "ms");
                        exceededTime = true;
                        break;
                    }
                }

                if (exceededTime) {
                    System.out.println("Execution Time exceeded 20 seconds!!");
                    break;
                } else {
                    System.out.println("Average: " + totalExecutionTime / 10 + "ms");

                    initialSize *= 2;
                }

            }
        }
}
