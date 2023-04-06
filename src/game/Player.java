package game;

import java.util.ArrayList;

public class Player {
    private char icon = 'P';
    private int numOfArrows = 5;
    private int[] coords;
    private boolean foundTreasure = false;
    private ArrayList<Artifact> inventory = new ArrayList<>();

    public Player(String name) {
        this.icon = name.charAt(0);
    }

    public Player(String name, int numOfArrows, boolean foundTreasure) {
        this(name);
        this.numOfArrows = numOfArrows;
        this.foundTreasure = foundTreasure;
    }

    public void addArrow() {
        this.numOfArrows++;
    }

    public void setArrows(int arrows){
        this.numOfArrows = arrows;
    }

    public boolean useArrow() {
        if (this.numOfArrows > 0) {
            this.numOfArrows--;
            return true;
        }
        return false;
    }

    public int getNumOfArrows() {
        return this.numOfArrows;
    }

    public void setCoords(int x, int y) {
        this.coords = new int[]{x, y};
    }

    public int[] getCoords() {
        return this.coords;
    }

    public boolean hadFoundTreasure(){
        return foundTreasure;
    }

    public void findTreasure(){
        this.foundTreasure = true;
    }

    public ArrayList<Artifact> getInventory() {
        return inventory;
    }

    public void addItem(Artifact artifact) {
        this.inventory.add(artifact);
    }

    public boolean containArtifact(Artifact artifact) {
        if (this.inventory.contains(artifact)) {
            return true;
        } else {
            return false;
        }
    }

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
