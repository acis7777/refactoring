package theater;

/**
 * Represents a play in the theater.
 *
 * @null This type does not accept null values for its fields.
 */
public class Play {

    // fields must be private and have accessor methods
    private final String name;
    private final String type;

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Get the play name.
     *
     * @return the play name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the play type.
     *
     * @return the play type
     */
    public String getType() {
        return type;
    }
}
