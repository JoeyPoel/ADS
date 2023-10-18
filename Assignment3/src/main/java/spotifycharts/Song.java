package spotifycharts;
import java.util.Map;
import java.util.HashMap;

public class Song {

    public enum Language {
        EN, // English
        NL, // Dutch
        DE, // German
        FR, // French
        SP, // Spanish
        IT, // Italian
    }

    public enum Country {
        UK, // United Kingdom
        NL, // Netherlands
        DE, // Germany
        BE, // Belgium
        FR, // France
        SP, // Spain
        IT  // Italy
    }

    private final String artist;
    private final String title;
    private final Language language;

    // TODO add instance variable(s) to track the streams counts per country
    //  choose a data structure that you deem to be most appropriate for this application.

    private Map<Country, Integer> streamsCountPerCountry;



    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;
        // TODO initialise streams counts per country as appropriate.
        this.streamsCountPerCountry = new HashMap<>();
    }

    /**
     * Sets the given streams count for the given country on this song
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        // TODO register the streams count for the given country.
        streamsCountPerCountry.put(country, streamsCount);
    }

    /**
     * retrieves the streams count of a given country from this song
     * @param country
     * @return
     */
    public int getStreamsCountOfCountry(Country country) {
        // TODO retrieve the streams count for the given country.
        return streamsCountPerCountry.getOrDefault(country, 0);
    }
    /**
     * Calculates/retrieves the total of all streams counts across all countries from this song
     * @return
     */
    public int getStreamsCountTotal() {
        // TODO calculate/get the total number of streams across all countries
        int totalStreamCount = 0;
        for (Country country : Country.values()) {
            totalStreamCount = totalStreamCount + this.getStreamsCountOfCountry(country);
        }

        return totalStreamCount;
    }


    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestStreamsCountTotal(Song other) {
        // TODO compare the total of stream counts of this song across all countries
        //  with the total of the other song
        return Integer.compare(other.getStreamsCountTotal(), this.getStreamsCountTotal());
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        // TODO compare this song with the other song
        //  ordening all Dutch songs upfront and then by decreasing total number of streams
        int compareDutch = Integer.compare(isDutch(other), isDutch(this));

        if(compareDutch == 0) {
            return Integer.compare(other.getStreamsCountTotal(), this.getStreamsCountTotal());
        } else{
            return compareDutch;
        }
    }

    /**
     * Checks if song language is Dutch, if so returns 1 if not returns 0
     * @param song
     * @return
     */
    public int isDutch(Song song){
        if(song.language == Language.NL){
            return 1;
        } else {
            return 0;
        }
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }
    @Override
    public String toString() {
        return artist + "/" + title +  "{" + language + "}" + "(" + this.getStreamsCountTotal() + ")";
    }
}
