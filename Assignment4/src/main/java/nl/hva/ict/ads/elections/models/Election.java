    package nl.hva.ict.ads.elections.models;
    
    import nl.hva.ict.ads.utils.PathUtils;
    import nl.hva.ict.ads.utils.xml.XMLParser;
    
    import javax.xml.stream.XMLStreamException;
    import java.io.FileInputStream;
    import java.io.IOException;
    import java.nio.file.Path;
    import java.util.*;
    import java.util.function.Function;
    import java.util.stream.Collectors;
    
    /**
     * Holds all election data per consituency
     * Provides calculation methods for overall election results
     */
    public class Election {
    
        private String name;
    
        // all (unique) parties in this election, organised by Id
        // will be build from the XML
        protected Map<Integer, Party> parties;
    
        // all (unique) constituencies in this election, identified by Id
        protected Set<Constituency> constituencies;
    
        public Election(String name) {
            this.name = name;
            // TODO initialise this.parties and this.constituencies with an appropriate Map implementations
            this.parties = new HashMap<>();
            this.constituencies =new HashSet<>();
        }
    
        /**
         * finds all (unique) parties registered for this election
         * @return all parties participating in at least one constituency, without duplicates
         */
        public Collection<Party> getParties() {
            // TODO: return all parties that have been registered for the election
            //  hint: there is no need to build a new collection; just return what you have got...
            return this.constituencies.stream()
                    .flatMap(constituency -> constituency.getParties().stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
    
        /**
         * finds the party with a given id
         * @param id
         * @return  the party with given id, or null if no such party exists.
         */
        public Party getParty(int id) {
            // TODO find the party with the given id
    
    
            return this.constituencies.stream()
                    .flatMap(constituency -> constituency.getParties().stream())
                    .filter(party -> party.getId() == id)
                    .findFirst()
                    .orElse(null);
    
    
        }
    
        public Set<? extends Constituency> getConstituencies() {
            return this.constituencies;
        }
    
        /**
         * finds all unique candidates across all parties across all constituencies
         * organised by increasing party-id
         * @return alle unique candidates organised by increasing party-id
         */
        public List<Candidate> getAllCandidates() {
            // TODO find all candidates organised by increasing party-id
            return this.getParties().stream()
                    .sorted(Comparator.comparingInt(Party::getId))
                    .flatMap(party -> party.getCandidates().stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
    
        /**
         * Retrieve for the given party the number of Candidates that have been registered per Constituency
         * @param party
         * @return
         */
        public Map<Constituency,Integer> numberOfRegistrationsByConstituency(Party party) {
            // TODO build a map with the number of candidate registrations per constituency
            // Initialize a map to store the number of registrations per constituency
            Map<Constituency, Integer> registrationsByConstituency = new HashMap<>();
    
            // Filter the constituencies to those that have the given party registered
            this.constituencies.forEach(constituency -> {
                long count = (int) constituency.getCandidates(party).stream()
                        .filter(candidate -> candidate.getParty().equals(party))
                        .count();
                registrationsByConstituency.put(constituency, (int) count);
            });
    
            return registrationsByConstituency;
        }
    
        /**
         * Finds all Candidates that have a duplicate name against another candidate in the election
         * (can be in the same party or in another party)
         * @return
         */
        public Set<Candidate> getCandidatesWithDuplicateNames() {
            // TODO build the collection of candidates with duplicate names across parties
            //   Hint: There are multiple approaches possible,
            //   if you cannot think of one, read the hints at the bottom of this file.
    
            Map<String, Set<Candidate>> candidateMap = new HashMap<>();
            Set<Candidate> candidatesWithDuplicateNames = new HashSet<>();
    
            // Iterate through all parties and their candidates
            for (Party party : this.getParties()) {
                for (Candidate candidate : party.getCandidates()) {
                    // Retrieve the set of candidates with the same name
                    Set<Candidate> candidatesWithName = candidateMap.computeIfAbsent(
                            candidate.getFullName(), k -> new HashSet<>()
                    );
    
                    // Check if there's already a candidate with the same name
                    if (!candidatesWithName.isEmpty()) { // if not empty
                        candidatesWithDuplicateNames.addAll(candidatesWithName);
                        candidatesWithDuplicateNames.add(candidate);
                    } else {
                        candidatesWithName.add(candidate);
                    }
                }
            }
    
            return candidatesWithDuplicateNames;
        }
    
        /**
         * Retrieve from all constituencies the combined sub set of all polling stations that are located within the area of the specified zip codes
         * i.e. firstZipCode <= pollingStation.zipCode <= lastZipCode
         * All valid zip codes adhere to the pattern 'nnnnXX' with 1000 <= nnnn <= 9999 and 'AA' <= XX <= 'ZZ'
         * @param firstZipCode
         * @param lastZipCode
         * @return      the sub set of polling stations within the specified zipCode range
         */
        public Collection<PollingStation> getPollingStationsByZipCodeRange(String firstZipCode, String lastZipCode) {
            // TODO retrieve all polling stations within the area of the given range of zip codes (inclusively)
    
            // Complete the method using stream operations to filter the polling stations
            return this.constituencies.stream()
                    .flatMap(constituency -> constituency.getPollingStations().stream())
                    .filter(pollingStation -> {
                        String zipCode = pollingStation.getZipCode();
                        return zipCode.compareTo(firstZipCode) >= 0 && zipCode.compareTo(lastZipCode) <= 0;
                    })
                    .collect(Collectors.toList()); // Collect the filtered polling stations into a list
        }
    
    
        /**
         * Retrieves per party the total number of votes across all candidates, constituencies and polling stations
         * @return
         */
        public Map<Party, Integer> getVotesByParty() {
            // TODO calculate the total number of votes per party
            Map<Party, Integer> votesPerParty = new HashMap<>();
    
            this.constituencies.stream()
                    .flatMap(constituency -> constituency.getPollingStations().stream())
                    .flatMap(pollingStation -> pollingStation.getVotesByParty().entrySet().stream())
                    .forEach(entry -> votesPerParty.merge(entry.getKey(), entry.getValue(), Integer::sum));
    
            return votesPerParty;
        }
    
        /**
         * Retrieves per party the total number of votes across all candidates,
         * that were cast in one out of the given collection of polling stations.
         * This method is useful to prepare an election result for any sub-area of a Constituency.
         * Or to obtain statistics of special types of voting, e.g. by mail.
         * @param pollingStations the polling stations that cover the sub-area of interest
         * @return
         */
        public Map<Party, Integer> getVotesByPartyAcrossPollingStations(Collection<PollingStation> pollingStations) {
            // TODO calculate the total number of votes per party across the given polling stations
    
            // Calculate the total number of votes per party across the given polling stations
            Map<Party, Integer> votesByPartyAcrossStations = new HashMap<>();
    
            // Extract votes per party for the filtered polling stations
            pollingStations.stream()
                    .flatMap(pollingStation -> pollingStation.getVotesByParty().entrySet().stream())
                    .forEach(entry -> votesByPartyAcrossStations.merge(entry.getKey(), entry.getValue(), Integer::sum));
    
            return votesByPartyAcrossStations;
        }
    
    
        /**
         * Transforms and sorts decreasingly vote counts by party into votes percentages by party
         * The party with the highest vote count shall be ranked upfront
         * The votes percentage by party is calculated from  100.0 * partyVotes / totalVotes;
         * @return  the sorted list of (party,votesPercentage) pairs with the highest percentage upfront
         */
        public static List<Map.Entry<Party,Double>> sortedElectionResultsByPartyPercentage(int tops, Map<Party, Integer> votesCounts) {
            // TODO transform the voteCounts input into a sorted list of entries holding votes percentage by party
    
            double totalVotes = votesCounts.values().stream().mapToInt(Integer::intValue).sum();
    
            // Transform vote counts to percentages and sort in descending order
            return votesCounts.entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(), (entry.getValue() / totalVotes) * 100.0))
                    .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                    .limit(tops) // Limit the number of top results if needed
                    .collect(Collectors.toList());
        }
    
        /**
         * Find the most representative Polling Station, which has got its votes distribution across all parties
         * the most alike the distribution of overall total votes.
         * A perfect match is found, if for each party the percentage of votes won at the polling station
         * is identical to the percentage of votes won by the party overall in the election.
         * The most representative Polling Station has the smallest deviation from that perfect match.
         *
         * There are different metrics possible to calculate a relative deviation between distributions.
         * You may use the helper method {@link #euclidianVotesDistributionDeviation(Map, Map)}
         * which calculates a relative least-squares deviation between two distributions.
         *
         * @return the most representative polling station.
         */
        public PollingStation findMostRepresentativePollingStation() {
    
            // TODO: calculate the overall total votes count distribution by Party
            //  and find the PollingStation with the lowest relative deviation between
            //  its votes count distribution and the overall distribution.
            //   hint: reuse euclidianVotesDistributionDeviation to calculate a difference metric between two vote counts
            //   hint: use the .min reducer on a stream of polling stations with a suitable comparator
            // Calculate the overall total votes count distribution by Party
            Map<Party, Integer> overallVotesDistribution = getVotesByParty();
    
            // Find the PollingStation with the lowest relative deviation
            return this.constituencies.stream()
                    .flatMap(constituency -> constituency.getPollingStations().stream())
                    .min(Comparator.comparingDouble(pollingStation ->
                            euclidianVotesDistributionDeviation(pollingStation.getVotesByParty(), overallVotesDistribution)))
                    .orElse(null);
        }
    
        /**
         * Calculates the Euclidian distance between the relative distribution across parties of two voteCounts.
         * If the two relative distributions across parties are identical, then the distance will be zero
         * If some parties have relatively more votes in one distribution than the other, the outcome will be positive.
         * The lower the outcome, the more alike are the relative distributions of the voteCounts.
         * ratign of votesCounts1 relative to votesCounts2.
         * see https://towardsdatascience.com/9-distance-measures-in-data-science-918109d069fa
         *
         * @param votesCounts1 one distribution of votes across parties.
         * @param votesCounts2 another distribution of votes across parties.
         * @return de relative distance between the two distributions.
         */
        private double euclidianVotesDistributionDeviation(Map<Party, Integer> votesCounts1, Map<Party, Integer> votesCounts2) {
            // calculate total number of votes in both distributions
            int totalNumberOfVotes1 = integersSum(votesCounts1.values());
            int totalNumberOfVotes2 = integersSum(votesCounts2.values());
    
            // we calculate the distance as the sum of squares of relative voteCount distribution differences per party
            // if we compare two voteCounts that have the same relative distribution across parties, the outcome will be zero
    
            return votesCounts1.entrySet().stream()
                    .mapToDouble(e -> Math.pow(e.getValue()/(double)totalNumberOfVotes1 -
                            votesCounts2.getOrDefault(e.getKey(),0)/(double)totalNumberOfVotes2, 2))
                    .sum();
        }
    
        /**
         * auxiliary method to calculate the total sum of a collection of integers
         * @param integers
         * @return
         */
        public static int integersSum(Collection<Integer> integers) {
            return integers.stream().reduce(Integer::sum).orElse(0);
        }

        public String prepareSummary(int partyId) {
            // TODO report total number of candidates in the given party
            // TODO report the list with all candidates in the given party
            // TODO report total number of registrations for the given party
            // TODO report the map of number of registrations by constituency for the given party

            Party party = this.getParty(partyId);
            StringBuilder summary = new StringBuilder()
                    .append("\nSummary of ").append(party).append(":\n");

            // Report total number of candidates in the given party
            int totalCandidatesInParty = party.getCandidates().size();
            summary.append("Total number of candidates in the given party: ").append(totalCandidatesInParty).append("\n");

            // Report the list with all candidates in the given party
            List<Candidate> candidatesInParty = new ArrayList<>(party.getCandidates());
            summary.append("List of all candidates in the given party: ").append(candidatesInParty).append("\n");

            // Report total number of registrations for the given party
            int totalRegistrationsForParty = numberOfRegistrationsByConstituency(party).values().stream().mapToInt(Integer::intValue).sum();
            summary.append("Total number of registrations for the given party: ").append(totalRegistrationsForParty).append("\n");

            // Report the map of number of registrations by constituency for the given party
            Map<Constituency, Integer> registrationsByConstituency = numberOfRegistrationsByConstituency(party);
            summary.append("Number of registrations by constituency for the given party: ").append(registrationsByConstituency).append("\n");

            return summary.toString();
        }


        public String prepareSummary() {
            // TODO report the total number of parties in the election
            // TODO report the list of all parties ordered by increasing party-Id
            // TODO report the total number of constituencies in the election
            // TODO report the total number of polling stations in the election
            // TODO report the total number of (different) candidates in the election
            // TODO report the list with all candidates which have a counter part with a duplicate name in a different party
    
            // TODO report the sorted list of overall election results ordered by decreasing party percentage
            // TODO report the polling stations within the Amsterdam Wibautstraat area with zipcodes between 1091AA and 1091ZZ
            // TODO report the top 10 sorted election results within the Amsterdam Wibautstraat area
            //   with zipcodes between 1091AA and 1091ZZ ordered by decreasing party percentage
            // TODO report the most representative polling station across the election
            // TODO report the sorted election results by decreasing party percentage of the most representative polling station
            StringBuilder summary = new StringBuilder()
                    .append("\nElection summary of ").append(this.name).append(":\n");

            // Report the total number of parties in the election
            int totalParties = this.parties.size();
            summary.append("Total number of parties in the election: ").append(totalParties).append("\n");

            // Report the list of all parties ordered by increasing party-Id
            List<Party> sortedParties = this.parties.values().stream()
                    .sorted(Comparator.comparingInt(Party::getId))
                    .collect(Collectors.toList());
            summary.append("List of all parties ordered by increasing party-Id: ").append(sortedParties).append("\n");

            // Report the total number of constituencies in the election
            int totalConstituencies = this.constituencies.size();
            summary.append("Total number of constituencies in the election: ").append(totalConstituencies).append("\n");

            // Report the total number of polling stations in the election
            int totalPollingStations = this.constituencies.stream()
                    .flatMap(constituency -> constituency.getPollingStations().stream())
                    .collect(Collectors.toSet())
                    .size();
            summary.append("Total number of polling stations in the election: ").append(totalPollingStations).append("\n");

            // Report the total number of (different) candidates in the election
            int totalCandidates = getAllCandidates().size();
            summary.append("Total number of different candidates in the election: ").append(totalCandidates).append("\n");

            // Report the list with all candidates which have a counterpart with a duplicate name in a different party
            Set<Candidate> candidatesWithDuplicateNames = getCandidatesWithDuplicateNames();
            summary.append("Candidates with duplicate names across parties: ").append(candidatesWithDuplicateNames).append("\n");

            // Report the sorted list of overall election results ordered by decreasing party percentage
            Map<Party, Integer> votesByParty = getVotesByParty();
            List<Map.Entry<Party, Double>> sortedResults = sortedElectionResultsByPartyPercentage(votesByParty.size(), votesByParty);
            summary.append("Sorted election results by decreasing party percentage: ").append(sortedResults).append("\n");

            // Report the polling stations within the Amsterdam Wibautstraat area with zip codes between 1091AA and 1091ZZ
            Collection<PollingStation> wibautstraatPollingStations = getPollingStationsByZipCodeRange("1091AA", "1091ZZ");
            summary.append("Polling stations within Amsterdam Wibautstraat area: ").append(wibautstraatPollingStations).append("\n");

            // Report the top 10 sorted election results within the Amsterdam Wibautstraat area by decreasing party percentage
            Map<Party, Integer> votesByPartyInWibautstraat = getVotesByPartyAcrossPollingStations(wibautstraatPollingStations);
            List<Map.Entry<Party, Double>> top10ResultsInWibautstraat = sortedElectionResultsByPartyPercentage(10, votesByPartyInWibautstraat);
            summary.append("Top 10 election results within Amsterdam Wibautstraat area: ").append(top10ResultsInWibautstraat).append("\n");

            // Report the most representative polling station across the election
            PollingStation mostRepresentativeStation = findMostRepresentativePollingStation();
            summary.append("Most representative polling station: ").append(mostRepresentativeStation).append("\n");

            // Report the sorted election results by decreasing party percentage of the most representative polling station
            Map<Party, Integer> votesByPartyInMostRepresentativeStation = mostRepresentativeStation.getVotesByParty();
            List<Map.Entry<Party, Double>> sortedResultsInMostRepresentativeStation = sortedElectionResultsByPartyPercentage(votesByPartyInMostRepresentativeStation.size(), votesByPartyInMostRepresentativeStation);
            summary.append("Election results of the most representative polling station: ").append(sortedResultsInMostRepresentativeStation).append("\n");

            return summary.toString();
        }
    
        /**
         * Reads all data of Parties, Candidates, Contingencies and PollingStations from available files in the given folder and its subfolders
         * This method can cope with any structure of sub folders, but does assume the file names to comply with the conventions
         * as found from downloading the files from https://data.overheid.nl/dataset/verkiezingsuitslag-tweede-kamer-2021
         * So, you can merge folders after unpacking the zip distributions of the data, but do not change file names.
         * @param folderName    the root folder with the data files of the election results
         * @return een Election met alle daarbij behorende gegevens.
         * @throws XMLStreamException bij fouten in een van de XML bestanden.
         * @throws IOException als er iets mis gaat bij het lezen van een van de bestanden.
         */
        public static Election importFromDataFolder(String folderName) throws XMLStreamException, IOException {
            System.out.println("Loading election data from " + folderName);
            Election election = new Election(folderName);
            int progress = 0;
            Map<Integer, Constituency> kieskringen = new HashMap<>();
            for (Path constituencyCandidatesFile : PathUtils.findFilesToScan(folderName, "Kandidatenlijsten_TK2021_")) {
                XMLParser parser = new XMLParser(new FileInputStream(constituencyCandidatesFile.toString()));
                Constituency constituency = Constituency.importFromXML(parser, election.parties);
                //election.constituenciesM.put(constituency.getId(), constituency);
                election.constituencies.add(constituency);
                showProgress(++progress);
            }
            System.out.println();
            progress = 0;
            for (Path votesPerPollingStationFile : PathUtils.findFilesToScan(folderName, "Telling_TK2021_gemeente")) {
                XMLParser parser = new XMLParser(new FileInputStream(votesPerPollingStationFile.toString()));
                election.importVotesFromXml(parser);
                showProgress(++progress);
            }
            System.out.println();
            return election;
        }
    
        protected static void showProgress(final int progress) {
            System.out.print('.');
            if (progress % 50 == 0) System.out.println();
        }
    
        /**
         * Auxiliary method for parsing the data from the EML files
         * This methode can be used as-is and does not require your investigation or extension.
         */
        public void importVotesFromXml(XMLParser parser) throws XMLStreamException {
            if (parser.findBeginTag(Constituency.CONSTITUENCY)) {
    
                int constituencyId = 0;
                if (parser.findBeginTag(Constituency.CONSTITUENCY_IDENTIFIER)) {
                    constituencyId = parser.getIntegerAttributeValue(null, Constituency.ID, 0);
                    parser.findAndAcceptEndTag(Constituency.CONSTITUENCY_IDENTIFIER);
                }
    
                //Constituency constituency = this.constituenciesM.get(constituencyId);
                final int finalConstituencyId = constituencyId;
                Constituency constituency = this.constituencies.stream()
                        .filter(c -> c.getId() == finalConstituencyId)
                        .findFirst()
                        .orElse(null);
    
                //parser.findBeginTag(PollingStation.POLLING_STATION_VOTES);
                while (parser.findBeginTag(PollingStation.POLLING_STATION_VOTES)) {
                    PollingStation pollingStation = PollingStation.importFromXml(parser, constituency, this.parties);
                    if (pollingStation != null) constituency.add(pollingStation);
                }
    
                parser.findAndAcceptEndTag(Constituency.CONSTITUENCY);
            }
        }
    
        /**
         * HINTS:
         * getCandidatesWithDuplicateNames:
         *  Approach-1: first build a Map that counts the number of candidates per given name
         *              then build the collection from all candidates, excluding those whose name occurs only once.
         *  Approach-2: build a stream that is sorted by name
         *              apply a mapMulti that drops unique names but keeps the duplicates
         *              this approach probably requires complex lambda expressions that are difficult to justify
         */
    
    }
