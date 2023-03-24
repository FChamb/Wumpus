public class Room {
    /**
     * A room can be one of four different types.
     * "." basic room with no special properties
     * "o" pit room - immediate death to player
     * "w" super bat room - carries the player to a random location
     * "G" treasure room - pt 1 of winning
     * "X" exit - pt 2 of winning
     */
    private String Type;
    private boolean playerInRoom = false;
    private boolean wumpusInRoom = false;

    public Room() {
        this.Type = ".";
    }

    public Room(String Type) {
        this.Type = Type;
    }

    public String getType() {
        return this.Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public boolean getPlayerInRoom() {
        return this.playerInRoom;
    }

    public void setPlayerInRoom(boolean playerInRoom) {
        this.playerInRoom = playerInRoom;
    }

    public boolean getWumpusInRoom() { return this.wumpusInRoom; }

    public void setWumpusInRoom(boolean wumpusInRoom) { this.wumpusInRoom = wumpusInRoom; }

    public String toString() {
        return " " + this.Type + " ";
    }
}
