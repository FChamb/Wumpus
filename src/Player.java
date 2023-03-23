public class Player {
    private char icon = 'P';
    private int numOfArrows = 5;
    private int[] coords;

    public Player(String name) {
        this.icon = name.charAt(0);
    }

    public Player(String name, int numOfArrows) {
        this(name);
        this.numOfArrows = numOfArrows;
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
        return " " + String.valueOf(this.icon) + " ";
    }
}
