package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.xml.XMLParser;

import javax.naming.NameAlreadyBoundException;
import javax.xml.stream.XMLStreamException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A Constituency (kieskring) is a regional district (of multiple cities and villages).
 * Every Constituency has a unique Id
 * Parties have to register their candidates at every Constituency where they want to be electable.
 * Within each Constituency each candidate is uniquely identified by a rank in its party.
 * That is his/her position on the ballot list
 * A party may register different candidates in different constituencies
 * and also allocate a different rank to the same candidate across different constituencies.
 * Within a Constituency you find a number of PollingStations where the voting takes place.
 * Every polling station in the Constituency will use the same ballot list of parties and candidates
 * (Different Constituencies may have different ballot lists)
 * Polling stations are organised by zip code and id such that
 * Navigable SubSets of PollingStations within a range of zipcodes can be retrieved efficiently.
 * Votes can be collected by Candidate and by Party across all polling stations.
 */
public class Constituency {

    private final int id;
    private final String name;

    // All candidates that have been registered at this constituency organised by party and rank
    private Map<Party,NavigableMap<Integer,Candidate>> rankedCandidatesByParty;

    // The polling stations in this constituency organised by zipCode and id
    // such that Navigable Subsets of polling stations within a range of zipcodes can be retrieved efficiently.
    private NavigableSet<PollingStation> pollingStations;

    public Constituency(int id, String name) {
        this.id = id;
        this.name = name;

        // TODO initialise this.rankedCandidatesByParty with an appropriate Map implementation
        this.rankedCandidatesByParty = new HashMap<>();

        // TODO and this.pollingStations with an appropriate Set implementation organised by zipCode and Id
        this.pollingStations = new TreeSet<>(Comparator.comparing(PollingStation::getZipCode)
                .thenComparing(PollingStation::getId));
    }

    /**
     * registers a candidate for participation in the election for his/her party in this constituency.
     * The given rank indicates the position on the ballot list.
     * If the rank is already taken by another Candidate, this registration shall fail and return false.
     * If the given candidate has been registered already at another rank, this registration shall also fail and return false
     * Used by XML import
     * @param candidate
     * @return whether the registration has succeeded
     */
    public boolean register(int rank, Candidate candidate) {
        // TODO  register the candidate in this constituency for his/her party at the given rank (ballot position)
        //  hint1: first check if a map of registered candidates already exist for the party of the given candidate
        //        then add the candidate to that map, if the candidate has not been registered
        Party candidateParty = candidate.getParty();

        // Check if the party exists in the map of registered candidates
        if (!rankedCandidatesByParty.containsKey(candidateParty)) {
            // If the party doesn't exist, create a new entry for the party with an empty map
            rankedCandidatesByParty.put(candidateParty, new TreeMap<>());
        }

        // Get the map of candidates for the party
        NavigableMap<Integer, Candidate> candidatesByRank = rankedCandidatesByParty.get(candidateParty);

        // Check if the rank is already occupied by another candidate for the same party or the value is already containing the candidate
        if (candidatesByRank.containsKey(rank) || candidatesByRank.containsValue(candidate) || rank <= 0) {
            return false;
        } else {
            // Register the candidate at the given rank for the party
            candidatesByRank.put(rank, candidate);
            return true;
        }
    }

    /**
     * retrieves the collection of parties that have registered one or more candidates at this constituency
     * @return
     */
    public Collection<Party> getParties() {
        // TODO: return all parties that have been registered at this constituency
        //  hint: there is no need to build a new collection; just return what you have got...

        return this.getRankedCandidatesByParty().keySet();
    }

    /**
     * retrieves a candidate from the ballot list of given party and at given rank
     * @param party
     * @param rank
     * @return
     */
    public Candidate getCandidate(Party party, int rank) {
        // TODO: return the candidate at the given rank in the given party


        if(rankedCandidatesByParty.containsKey(party)){
            NavigableMap<Integer, Candidate> candidatesByRank = rankedCandidatesByParty.get(party);

            if(candidatesByRank.containsKey(rank)){
                return candidatesByRank.get(rank);
            }
        }
        return null;    // No candidate found
    }

    /**
     * retrieve a list of all registered candidates for a given party in order of their rank
     * @param party
     * @return
     */
    public final List<Candidate> getCandidates(Party party) {
        // TODO: return a list with all registered candidates of a given party in order of their rank
        //  hint: if the implementation classes of rankedCandidatesByParty are well chosen, this only takes one line of code
        //  hint: the resulting list may be immutable at your choice of implementation.

        return rankedCandidatesByParty.getOrDefault(party, new TreeMap<>()).values().stream().collect(Collectors.toList());
    }

    /**
     * finds all candidates who are electable in this Constituency
     * (via the list of candidates of a party that has been registered).
     * @return the set of all candidates in this Constituency.
     */
    public Set<Candidate> getAllCandidates() {
        // TODO collect all candidates of all parties of this Constituency into a Set.
        //  hint: flatMap may help...
        return rankedCandidatesByParty.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Retrieve the sub set of polling stations that are located within the area of the specified zip codes
     * i.e. firstZipCode <= pollingStation.zipCode <= lastZipCode
     * All valid zip codes adhere to the pattern 'nnnnXX' with 1000 <= nnnn <= 9999 and 'AA' <= XX <= 'ZZ'
     * @param firstZipCode
     * @param lastZipCode
     * @return      the sub set of polling stations within the specified zipCode range
     */
    public NavigableSet<PollingStation> getPollingStationsByZipCodeRange(String firstZipCode, String lastZipCode) {
        // TODO: return all polling stations that have been registered at this constituency
        //  hint: there is no need to build a new collection; just return what you have got...
        // Create a subset of polling stations within the specified zipCode range
        NavigableSet<PollingStation> pollingStationsInRange = new TreeSet<>(Comparator.comparing(PollingStation::getZipCode)
                .thenComparing(PollingStation::getId));

        // Iterate through all polling stations in this constituency
        for (PollingStation pollingStation : pollingStations) {
            String stationZipCode = pollingStation.getZipCode();

            // Check if the polling station's zip code falls within the specified range
            if (stationZipCode.compareTo(firstZipCode) >= 0 && stationZipCode.compareTo(lastZipCode) <= 0) {
                pollingStationsInRange.add(pollingStation); // Add the polling station to the subset
            }
        }

        return pollingStationsInRange;
    }

    /**
     * Provides a map of total number of votes per party in this constituency
     * accumulated across all polling stations and all candidates
     * @return
     */
    public Map<Party,Integer> getVotesByParty() {
        // TODO prepare a map of total number of votes per party
        Map<Party, Integer> votesByParty = new HashMap<>();

        // Iterate through all polling stations in the constituency
        for (PollingStation pollingStation : pollingStations) {
            // Get the votes for each party in this polling station
            Map<Party, Integer> votesInStation = pollingStation.getVotesByParty();

            // Accumulate the votes for each party across all polling stations
            for (Map.Entry<Party, Integer> entry : votesInStation.entrySet()) {
                Party party = entry.getKey();
                int votes = entry.getValue();

                // Add the votes to the corresponding party's total votes
                votesByParty.put(party, votesByParty.getOrDefault(party, 0) + votes);
            }
        }

        return votesByParty;
    }

    /**
     * adds a polling station to this constituency
     * @param pollingStation
     */
    public boolean add(PollingStation pollingStation) {
        if (!this.pollingStations.add(pollingStation)) {
            // polling station with duplicate id found; retrieve the original
            PollingStation existing = this.pollingStations.floor(pollingStation);

            // and merge the votes of the duplicate into the existing original
            pollingStation.combineVotesWith(existing);
        };
        return true;
    }

    @Override
    public String toString() {
        return "Constituency{" +
                "id=" + id +
                ",name='" + name + "'" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constituency)) return false;
        Constituency other = (Constituency) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    protected Map<Party, NavigableMap<Integer, Candidate>> getRankedCandidatesByParty() {
        return this.rankedCandidatesByParty;
    }

    public NavigableSet<PollingStation> getPollingStations() {
        return this.pollingStations;
    }

    protected static final Logger LOG = Logger.getLogger(Constituency.class.getName());
    public static final String CONSTITUENCY = "Contest";
    public static final String CONSTITUENCY_IDENTIFIER = "ContestIdentifier";
    public static final String ID = "Id";
    protected static final String CONSTITUENCY_NAME = "ContestName";
    public static final String INVALID_NAME = "INVALID";
    /**
     * Auxiliary method for parsing the data from the EML files
     * This method can be used as-is and does not require your investigation or extension.
     */
    public static Constituency importFromXML(XMLParser parser, Map<Integer,Party> parties) throws XMLStreamException {
        if (parser.findBeginTag(CONSTITUENCY)) {

            int id = 0;
            String name = null;
            if (parser.findBeginTag(CONSTITUENCY_IDENTIFIER)) {
                id = parser.getIntegerAttributeValue(null, ID, 0);
                if (parser.findBeginTag(CONSTITUENCY_NAME)) {
                    name = parser.getElementText();
                    parser.findAndAcceptEndTag(CONSTITUENCY_NAME);
                }
                parser.findAndAcceptEndTag(CONSTITUENCY_IDENTIFIER);
            }

            Constituency constituency = new Constituency(id, name);
            parser.findBeginTag(Party.PARTY);
            while (parser.getLocalName().equals(Party.PARTY)) {
                Party.importFromXML(parser, constituency, parties);
            }

            if (parser.findAndAcceptEndTag(CONSTITUENCY)) {
                return constituency;
            } else {
                LOG.warning("Can't find " + CONSTITUENCY + " closing tag.");
            }
        } else {
            LOG.warning("Can't find " + CONSTITUENCY + " opening tag.");
        }
        return new Constituency(-1, INVALID_NAME);
    }
}
