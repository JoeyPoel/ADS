package spotifycharts;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BigOEfficiencyTest {

    int initialSize = 100;
    long maxSongs = 5000000;
    long maxTime = 20000; // 20 seconds

    long startTime, endTime, totalExecutionTime;

    boolean algorithmExceededTime = false;

    Sorter<Song> sorter = new SongSorter();



    @Test
    void evaluateSortingAlgorithms() {

        while (initialSize <= maxSongs) {

                totalExecutionTime = 0;

                System.out.println("-------------------------------------------------------");
                System.out.println("\nAlgorithm 1: ");

                for (int i = 0; i < 10; i++) {
                    ChartsCalculator chartsCalculator = new ChartsCalculator(i);

                    List<Song> songs = chartsCalculator.registerStreamedSongs(initialSize);

                    startTime = System.currentTimeMillis();
                    sorter.quickSort(new ArrayList<>(songs), Song::compareByHighestStreamsCountTotal);

                    System.gc();

                    endTime = System.currentTimeMillis();
                    long executionTime = endTime - startTime;

                    totalExecutionTime += executionTime;

                    if (executionTime > maxTime) {
                        algorithmExceededTime = true;
                        break;
                    }

                    // maak hier een gemiddelde van inplaats van elke run te meten
                    // verzamel de 10 runs en maak gemiddelde
                    // wanneer executionTime > 20 seconden stop de test en laat de gemmidelde zien!!
                    System.out.println("Run " + (i + 1) + ": " + executionTime + "ms");

                    if (algorithmExceededTime) {
                        System.out.println("Total time: " + totalExecutionTime + "ms");
                    }
                }


            initialSize *= 2;
        }
    }
}
