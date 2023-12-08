package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NavigableSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ÀdditionalTest1 {

    private Constituency constituency;
    private Party additionalParty;
    private Candidate additionalCandidate;

    @BeforeEach
    public void setup() {
        this.constituency = new Constituency(1, "Additional Constituency");
        this.additionalParty = new Party(103, "Additional Party");
        this.additionalCandidate = new Candidate("C.", null, "Member", this.additionalParty);
    }

    @Test
    void registerShouldFailOnInvalidRank() {
        // Attempt to register a candidate with an invalid rank
        assertFalse(this.constituency.register(0, this.additionalCandidate),
                "Registration with invalid rank should fail");
        assertFalse(this.constituency.register(-1, this.additionalCandidate),
                "Registration with invalid rank should fail");

        // Verify that the candidate was not registered
        assertNull(this.constituency.getCandidate(this.additionalParty, 0),
                "Candidate with invalid rank should not be registered");
    }

    @Test
    void getPollingStationsByZipCodeRangeShouldReturnEmptySet() {
        // Add a polling station to the constituency
        PollingStation pollingStation = new PollingStation("XYZ", "1234AB", "Test Station");
        this.constituency.add(pollingStation);

        // Attempt to retrieve polling stations with an invalid zip code range
        NavigableSet<PollingStation> pollingStations = this.constituency.getPollingStationsByZipCodeRange("0000AA", "0000ZZ");

        // Verify that the result is an empty set
        assertEquals(Set.of(), pollingStations,
                "Could not retrieve exactly the polling stations within the zip code range.");
    }
}
