package game;

public class Wumpus {
    private char icon = '!';
    private boolean alive = true;
    private int[] coords;
    // Adding multiple lives functionality
    private int lives;

    public Wumpus() {
        this('!', 1);
    }

    public Wumpus(char icon) {
        this(icon, 1);
    }

    public Wumpus(char icon, int lives){
        this.icon = icon;
        this.alive = true;
        this.lives = lives;
    }

    public Wumpus(int lives){
        this('!', lives);
    }

    public char getIcon() {
        return this.icon;
    }

    public void setIcon(char icon) {
        this.icon = icon;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    // Setter for the number of lives
    public void setLives(int lives){
        this.lives = lives;
    }

    public int getLives(){
        return this.lives;
    }
    
    // Return whether the wumpus is alive after being hit
    public boolean hitWumpus(){
        if(lives > 1){
            lives--;
            return true;
        }
        if(lives == 1){
            lives--;
            this.alive = false;
            return true;
        }
        return false;
    }

    public void setCoords(int x, int y) {
        this.coords = new int[]{x, y};
    }

    public int[] getCoords() {
        return this.coords;
    }

    public String toString() {
        return " " + this.icon + " ";
    }
}
