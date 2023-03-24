public class Wumpus {
    private char icon = '!';
    private boolean alive = true;
    private int[] coords;

    public Wumpus() {
        this.icon = '!';
        this.alive = true;
    }

    public Wumpus(char icon) {
        this.icon = icon;
        this.alive = true;
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
