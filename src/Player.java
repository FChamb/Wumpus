public class Player {
    private char icon = 'P';
    private int numOfArrows = 5;
    private int[] coords;
    private boolean foundTreasure = false;

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

    public void useArrow() {
        if (this.numOfArrows > 0) {
            this.numOfArrows--;
        }
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

    public String toString() {
        return " " + this.icon + " ";
    }
}
