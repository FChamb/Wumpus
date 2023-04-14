package game;

public class Wumpus {
    private char icon = '!';
    private boolean alive = true;
    private int[] coords;
    // Adding multiple lives functionality
    private int lives;

    /**
     * Empty constructor for the wumpus. Sets the icon to !.
     * Sets the number of lives to 1.
     */
    public Wumpus() {
        this('!', 1);
    }

    /**
     * Constructor that allows the user to choose wumpus icon.
     * 
     * @param icon - a char for the look of the wumpus
     */
    public Wumpus(char icon) {
        this(icon, 1);
    }

    /**
     * Default constructor which takes an icon and number of lives.
     * 
     * @param icon  - a char for the look of the wumpus
     * @param lives - the number of lives the wumpus has
     */
    public Wumpus(char icon, int lives) {
        this.icon = icon;
        this.alive = true;
        this.lives = lives;
    }

    /**
     * Constructor that allows the user to choose wumpus lives.
     * 
     * @param lives - the number of lives the wumpus has
     */
    public Wumpus(int lives) {
        this('!', lives);
    }

    /**
     * Getter which returns the icon of the wumpus
     * 
     * @return - char value for look of wumpus
     */
    public char getIcon() {
        return this.icon;
    }

    /**
     * Setter which sets the icon for the wumpus
     * 
     * @param icon - char value for look of wumpus
     */
    public void setIcon(char icon) {
        this.icon = icon;
    }

    /**
     * Getter which returns whether the wumpus is alive or not
     * 
     * @return - a boolean value for if wumpus is alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Setter which sets the wumpus alive or dead
     * 
     * @param alive - a boolean value for if wumpus is alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Setter for setting the number of lives the wumpus has. If
     * less than 1, wumpus is killed.
     * 
     * @param lives - an int of the number of lives the wumpus has
     */
    public void setLives(int lives) {
        // If the lives is set to 0 or below
        if (lives < 1) {
            alive = false;
        }
        this.lives = lives;
    }

    /**
     * A getter which returns the number of lives the wumpus has
     * 
     * @return - returns an integer with the number of lives the wumpus has
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * Return whether the wumpus is alive after being hit.
     * 
     * @return - a boolean value if the wumpus is alive or dead
     */
    public boolean hitWumpus() {
        if (lives > 1) {
            lives--;
            return true;
        }
        if (lives == 1) {
            lives--;
            this.alive = false;
            return true;
        }
        return false;
    }

    /**
     * Setter which takes x,y coords and sets the location of the wumpus.
     * 
     * @param x - the height the wumpus is in the cave
     * @param y - the length the wumpus is in the cave
     */
    public void setCoords(int x, int y) {
        this.coords = new int[] { x, y };
    }

    /**
     * Getter which returns an array of ints with the coords of the wumpus.
     * 
     * @return - an int array of the coords
     */
    public int[] getCoords() {
        return this.coords;
    }

    /**
     * Returns the look of the wumpus of the board.
     * 
     * @return - a string value for the look of the wumpus.
     */
    public String toString() {
        if (alive) {
            return " " + this.icon + " ";
        }
        return " . ";
    }
}
