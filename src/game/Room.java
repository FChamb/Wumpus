package game;

public class Room {

    // Different room types
    public static final char BSC = '.';
    public static final char WLL = ' ';
    public static final char PIT = 'o';
    public static final char BAT = 'w';
    public static final char TSR = 'G';
    public static final char EXT = 'X';

    private final String Type;
    private Artifact artifact = null;

    /**
     * Constructor for a basic room
     */
    public Room() {
        this(""+BSC);
    }

    /**
     * Constructor for a room given a type as a char
     */
    public Room(char Type) {
        this(""+Type);
    }

    /**
     * Constructor for a room given a type as a string
     */
    public Room(String Type) {
        this.Type = Type;
    }

    /**
     * Constructor for a room given a type and an artefact
     */
    public Room(String Type, Artifact artifact) {
        this(Type);
        this.artifact = artifact;
    }

    /**
     * Gets the type of the room
     * 
     * @return the type of the room
     */
    public String getType() {
        return this.Type;
    }

    /**
     * Gets the artefact in the room
     * 
     * @return the artefact in the room
     */
    public Artifact getArtifact() {
        return this.artifact;
    }

    /**
     * Sets the artefact in the room
     * 
     * @param artifact - the artefact in the room
     */
    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    /**
     * Creates a srting from the room type or artefact
     * 
     * @return a string of the room type or artefact
     */
    public String toString() {
        if (artifact == null) {
            return " " + this.Type + " ";
        } else {
            return this.artifact + "";
        }
    }
}
