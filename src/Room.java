public class Room {
    private int attribute;
    private boolean playerInRoom = false;

    public Room() {
        this.attribute = 0;
    }

    public Room(int attribute) {
        this.attribute = attribute;
    }

    public int getAttribute() {
        return this.attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public boolean getPlayerInRoom() {
        return this.playerInRoom;
    }

    public void setPlayerInRoom(boolean playerInRoom) {
        this.playerInRoom = playerInRoom;
    }

    public String toString() {
        return " " + this.attribute + " ";
    }
}
