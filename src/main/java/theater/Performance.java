package theater;

/**
 * Represents a single performance of a play.
 */
public class Performance {

    // fields must be private and have accessor methods
    private final String playID;
    private final int audience;

    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    /**
     * Get the play identifier.
     *
     * @return the play id
     */
    public String getPlayID() {
        return playID;
    }

    /**
     * Get the audience size.
     *
     * @return number of audience members
     */
    public int getAudience() {
        return audience;
    }
}
