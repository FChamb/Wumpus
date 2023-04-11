package game;

import java.util.ArrayList;

public class Player {
    private char icon = 'P';
    private int numOfArrows = 5;
    private int[] coords;
    private boolean foundTreasure = false;
    private ArrayList<Artifact> inventory = new ArrayList<>();

    /**
     * Constructor that allows the user to choose player name. The icon is
     * set to the first letter in the name
     * @param name - a string for the look of the player
     */
    public Player(String name) {
        this.icon = name.charAt(0);
    }

    /**
     * Constructor which takes a username, number of arrows, and if the treasure
     * is found.
     * @param name - a string with the name of the player
     * @param numOfArrows - number of arrows a user has
     * @param foundTreasure - if the treasure has been found
     */
    public Player(String name, int numOfArrows, boolean foundTreasure) {
        this(name);
        this.numOfArrows = numOfArrows;
        this.foundTreasure = foundTreasure;
    }

    /**
     * Add arrow increases the arrow count by one.
     */
    public void addArrow() {
        this.numOfArrows++;
    }

    /**
     * Set arrows sets the number of arrows for a player.
     * @param arrows
     */
    public void setArrows(int arrows){
        this.numOfArrows = arrows;
    }

    /**
     * Use arrow decreases the number of arrows by one and returns true is the user
     * has arrows left over, false otherwise.
     * @return
     */
    public boolean useArrow() {
        if (this.numOfArrows > 0) {
            this.numOfArrows--;
            return true;
        }
        return false;
    }

    /**
     * Getter which returns the number of arrows a player has.
     * @return - an int with the number of arrows
     */
    public int getNumOfArrows() {
        return this.numOfArrows;
    }

    /**
     * Sets the coordinates of the player with a given height and width.
     * @param x - the height of the player
     * @param y - the width of the player
     */
    public void setCoords(int x, int y) {
        this.coords = new int[]{x, y};
    }

    /**
     * Getter that returns the coords of the player
     * @return - an int array with the players coords
     */
    public int[] getCoords() {
        return this.coords;
    }

    /**
     * Getter which returns if the treasure has been found.
     * @return - boolean for treasure found
     */
    public boolean hadFoundTreasure(){
        return foundTreasure;
    }

    /**
     * Setter which sets the foundTreasure to true.
     */
    public void findTreasure(){
        this.foundTreasure = true;
    }

    /**
     * Getter which returns the array list of artifacts the player has.
     * @return - array list of artifacts
     */
    public ArrayList<Artifact> getInventory() {
        return inventory;
    }

    /**
     * Add item add an artifact to the players inventory.
     * @param artifact -  a given artifact
     */
    public void addItem(Artifact artifact) {
        this.inventory.add(artifact);
    }

    /**
     * Sets the players name.
     * @param name - a given name for a user
     */
    public void setName(String name) {
        this.icon = name.charAt(0);
    }

    public boolean containArtifact(Artifact artifact) {
        if (this.inventory.contains(artifact)) {
            return true;
        } else {
            return false;
        }
    }

    public char getIcon() {
        return icon;
    }
    
    /**
     * Puts the player into a string and returns.
     * @return
     */
    public String toString() {
        return " " + this.icon + " ";
    }

    // Method to check if the player has a shield
    public boolean hasShield(){
        for(Artifact artefact : this.inventory){
            if(artefact.getName().equals("D")){
                return true;
            }
        }
        return false;
    }

    // Method to use a shield if the player has one (returns true if shield used and false if not)
    public boolean useShield(){
        for(Artifact artefact : this.inventory){
            // If the player has a shield use it and return true
            if(artefact.getName().equals("D")){
                inventory.remove(artefact);
                return true;
            }
        }

        return false;
    }
}
