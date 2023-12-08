package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.PathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javax.xml.stream.XMLStreamException;
import java.io.IOException;


import static org.junit.jupiter.api.Assertions.*;

class SummaryTest {

    private Election election;

    @BeforeEach
    void setUp() throws IOException, XMLStreamException {
        election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021_HvA_UvA"));

    }

    @Test
    void testPrepareSummaryForParty() {
        int partyId = 1;
        String summary = election.prepareSummary(partyId);

        assertNotNull(summary);

        assertTrue(summary.contains("Total number of candidates in the given party:"));

        assertTrue(summary.contains("List of all candidates in the given party:"));

        assertTrue(summary.contains("Total number of registrations for the given party:"));

        assertTrue(summary.contains("Number of registrations by constituency for the given party:"));
    }

    @Test
    void testPrepareSummaryForElection() {
        String summary = election.prepareSummary();

        assertNotNull(summary);

        assertTrue(summary.contains("Total number of parties in the election:"));

        assertTrue(summary.contains("List of all parties ordered by increasing party-Id:"));

        assertTrue(summary.contains("Total number of constituencies in the election:"));

        assertTrue(summary.contains("Total number of polling stations in the election:"));

        assertTrue(summary.contains("Total number of different candidates in the election:"));

        assertTrue(summary.contains("Candidates with duplicate names across parties:"));

    }


}
