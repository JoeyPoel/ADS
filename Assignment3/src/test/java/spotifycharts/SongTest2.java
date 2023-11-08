package spotifycharts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SongTest2 {

    private static Comparator<Song> rankingSchemeTotal, rankingSchemeDutchNational;
    Song songBYC, songKKA, songTS, songJVT, songBB;

    @BeforeAll
    static void setupClass() {
        rankingSchemeTotal = Song::compareByHighestStreamsCountTotal;
        rankingSchemeDutchNational = Song::compareForDutchNationalChart;
    }

    @BeforeEach
    void setup() {
        songBYC = new Song("Beyoncé", "CUFF IT", Song.Language.EN);
        songBYC.setStreamsCountOfCountry(Song.Country.UK,100);
        songBYC.setStreamsCountOfCountry(Song.Country.NL,40);
        songBYC.setStreamsCountOfCountry(Song.Country.BE,20);
        songTS = new Song("Taylor Swift", "Anti-Hero", Song.Language.EN);
        songTS.setStreamsCountOfCountry(Song.Country.UK,100);
        songTS.setStreamsCountOfCountry(Song.Country.DE,60);
        songKKA = new Song("Kris Kross Amsterdam", "Vluchtstrook", Song.Language.NL);
        songKKA.setStreamsCountOfCountry(Song.Country.NL,40);
        songKKA.setStreamsCountOfCountry(Song.Country.BE,30);
        songJVT = new Song("De Jeugd Van Tegenwoordig", "Sterrenstof", Song.Language.NL);
        songJVT.setStreamsCountOfCountry(Song.Country.NL,70);
        songBB = new Song("Bad Bunny", "La Coriente", Song.Language.SP);
    }


    @Test
    void compareSameSongs() {
        assertSame(songBB, songBB, "Songs are not the same");

    }

    @Test
    void compareSymmetry() {
        int result1 = songBB.compareByHighestStreamsCountTotal(songBYC);
        int result2 = songBYC.compareByHighestStreamsCountTotal(songBB);

        assertEquals(result1, -result2);

        result1 = songBB.compareForDutchNationalChart(songBYC);
        result2 = songBYC.compareForDutchNationalChart(songBB);

        assertEquals(result1,-result2);
    }

    @Test
    void streamsCountUpdate() {
        songBYC.setStreamsCountOfCountry(Song.Country.UK, 100);

        songBYC.setStreamsCountOfCountry(Song.Country.UK, 150);

        assertEquals(150, songBYC.getStreamsCountOfCountry(Song.Country.UK),
                "Stream count update did not work as expected");
    }

    @Test
    void retrieveStreamsForUnsetCountry() {
        int streamsFR = songBYC.getStreamsCountOfCountry(Song.Country.FR);
        int streamsIT = songTS.getStreamsCountOfCountry(Song.Country.IT);

        assertEquals(0, streamsFR, "Stream count for unset country should be 0");
        assertEquals(0, streamsIT, "Stream count for unset country should be 0");
    }



}

