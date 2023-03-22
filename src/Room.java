public class Room {
    private int xCoord;
    private int yCoord;
    private int attribute = 0;

    public Room(int xCoord, int yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public Room(int xCoord, int yCoord, int attribute) {
        this(xCoord, yCoord);
        this.attribute = attribute;
    }

    public int getxCoord() {
        return this.xCoord;
    }

    public int getyCoord() {
        return this.yCoord;
    }

    public int getAttribute() {
        return this.attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }
}
