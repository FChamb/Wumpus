package game;

import java.util.*;

public class AI {
    // Attributes to store all the information about a round that the game is
    // providing
    private int roomNumber;
    private boolean[] surroundings;
    private String nsew;

    // Attributes that the logic operates around
    private HashMap<Integer, int[]> roomNumbers;
    private HashMap<Integer, Boolean> wumpus = new HashMap<>();
    private HashMap<Integer, Boolean> pits = new HashMap<>();
    private HashMap<Integer, Boolean> treasure = new HashMap<>();
    private ArrayList<Integer> bats = new ArrayList<>();
    private int caveRows;
    private int caveColumns;
    private ArrayList<Integer> previousRooms = new ArrayList<>();
    private ArrayList<String> previousMoves = new ArrayList<>();
    private int[] wumpusLocation = null;
    private int[] exitLocation = null;
    // Counter to stop it from trying to select a random number forever
    private int randomCount = 0;
    // Counters to work out when the player is blind/unable to smell
    private int blind = 0;
    private int blockedNose = 0;
    // Booleans for different game modes
    private boolean treasureMode = false;
    private boolean shooting = false;
    private boolean shot = false;
    private boolean beenNearTreasure = false;
    private boolean beenNearWumpus = false;
    private boolean exitMode = false;

    /**
     * Constructor that takes the size of the cave the AI is navigating
     * 
     * @param caveRows    - number of rows in the associated cave
     * @param caveColumns - number of columns in the associated cave
     */
    public AI(int caveRows, int caveColumns) {
        this.caveRows = caveRows;
        this.caveColumns = caveColumns;
        setUpNumbers();
    }

    /**
     * Sets up the hashMap of room numbers and their corresponding coordinates on
     * the board
     */
    public void setUpNumbers() {
        roomNumbers = new HashMap<>();
        for (int i = 0; i < caveRows; i++) {
            for (int j = 0; j < caveColumns; j++) {
                roomNumbers.put(caveColumns * i + j + 1, new int[] { i, j });
            }
        }
    }

    /**
     * Updates the info about the cave and the players surroundings so the AI can
     * make well informed choices
     * 
     * @param roomNumber   - the number of the room the player is currently in
     * @param surroundings - booleans to tell the AI about the players surroundings
     *                     in the cave
     * @param nsew         - the possible directions the player can move in
     */
    public void setInfo(int roomNumber, boolean[] surroundings, String nsew) {
        this.roomNumber = roomNumber;
        this.surroundings = surroundings;
        this.nsew = nsew;

        // Add the room number to the list of rooms the player has been to
        previousRooms.add(roomNumber);
        randomCount = 0; // Reset the number of random numbers generated to 0

        // Check senses and update maps of the cave
        checkBlind();
        checkNose();
        checkBats();
        checkShot();
        updateInfo();
    }

    /**
     * Updates all the maps about the layout of the cave with the new information
     */
    public void updateInfo() {
        wumpus.put(roomNumber, false); // Make the current cell safe

        // Only update info about the wumpus when able to smell
        if (blockedNose < 1) {
            find(wumpus, surroundings[0], beenNearWumpus);
            checkWumpusMap();

        }

        // Only update info about the treasure when not blind
        if (blind < 1) {
            find(treasure, surroundings[2], beenNearTreasure);
            checkTreasureMap();

        }
        if (!surroundings[6]) { // If the player has not found the treasure then the current room is false
            treasure.put(roomNumber, false);
        }
        // Update information about pits
        find(pits, surroundings[1], false);
        pits.put(roomNumber, false);

        // Enter treasure finding mode if near the treasure
        if (surroundings[2]) {
            treasureMode = true;
        }
        // Leave treasure finding mode after finding the treasure
        if (surroundings[6]) {
            treasureMode = false;
            //previousRooms.clear(); // clear the previous rooms after finding the treasure
            if (exitLocation != null) {
                exitMode = true;
            }
            // could try reseting knowledge of the board otherwise
        }
        // Set the exit location if on the exit
        if (surroundings[7]) {
            exitLocation = roomNumbers.get(roomNumber);
        }
    }

    /**
     * Checks if the player shot last round and updates values accordingly
     */
    public void checkShot() {
        if (shot) {
            wumpus.clear();
            wumpusLocation = null;
            shot = false;
            beenNearWumpus = false;
        }
    }

    /**
     * Decides whether the player is going to move or shoot depednding on whether
     * the player is near the wumpus
     * 
     * @return the players intended move as a string
     */
    public String makeMove() {
        // Shoot if the wumpus is guaranteed to be in an adjacent room
        if (getWumpusAdjacent() && !shot) {
            shooting = true;
            System.out.println("S");
            return "S";
        }
        // If not next to the wumpus then move
        System.out.println("M");
        return "M";
    }

    /**
     * Decides what direction the player is to move/shoot in depending on the
     * different possible strategies
     * 
     * @return the direction the player is to move/shoot in as a string
     */
    public String chooseDirection() {
        String move;
        // Shoot the wumpus if next to it
        if (shooting) {
            move = shootWumpus();
            System.out.println(move);
            shooting = false;
            shot = true;
            return move;
        }

        String safeMoves = getSafeMoves();
        move = getMove(safeMoves);
        // Keep track of the moves
        previousMoves.add(move);
        // Count down how long the ai has been blind for
        blind--;
        blockedNose--;
        System.out.println(move); // print the move
        return move;
    }

    /**
     * Gets the direction the player is going to move in based on the current
     * in-game conditions
     * 
     * @param safeMoves - all the safe locations around the player
     * @return the desired move as a string
     */
    public String getMove(String safeMoves) {
        String move;

        if (blind > 0 || blockedNose > 0) { // If unable to smell or see then walk back and forth
            move = moveBackwards();
        } else if (checkPreviousMoves()) { // If walking in back and forth, get a random move
            move = chooseRandom(nsew);
        } else if (treasureMode) { // If near the treasure move towards it
            move = moveToTreasure(safeMoves);
        } else if (exitMode) { // If found the treasure and exit, move towards the exit
            move = moveTowards(exitLocation, safeMoves);
        } else { // If nothing has been found, move randomly
            move = chooseRandom(safeMoves);
        }

        return move;
    }

    /**
     * Gets a random move for the player
     * 
     * @param safeMoves - all the possible safe moves for the player to make
     * @return a random direction as a string
     */
    public String chooseRandom(String safeMoves) {
        randomCount++; // Count the number of random numbers generated this round
        Random random = new Random();
        int index = random.nextInt(safeMoves.split("-").length);
        String direction = safeMoves.split("-")[index];

        // Get another move if it is not unique (only do this 10 times)
        if (!checkUnique(getRoomMove(direction)) && randomCount < 10) {
            direction = chooseRandom(safeMoves);
        }
        return direction;
    }

    /**
     * Gets all of the safe moves from the current position in the cave
     * 
     * @return a string containing all the safe moves
     */
    public String getSafeMoves() {
        String[] part = nsew.split("-");
        StringBuilder builder = new StringBuilder();
        // Store any rooms that are safe
        for (int i = 0; i < part.length; i++) {
            if (checkSafe(getRoomMove(part[i]))) {
                builder.append(part[i] + "-");
            }
        }
        // If there are no safe rooms then return all the moves
        if (builder.toString().isEmpty()) {
            return nsew;
        }

        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    /**
     * Gets the number of the room that the player will end up in after moving in
     * the given direction
     * 
     * @param move - the direction being moved in
     * @return the room that direction moves the player to
     */
    public int getRoomMove(String move) {
        int[] coords = roomNumbers.get(roomNumber);
        int[] roomCoords = new int[2];
        if (move.equalsIgnoreCase("N")) {
            roomCoords = new int[] { validateRow(coords[0] - 1), coords[1] };
        }
        if (move.equalsIgnoreCase("S")) {
            roomCoords = new int[] { validateRow(coords[0] + 1), coords[1] };
        }
        if (move.equalsIgnoreCase("E")) {
            roomCoords = new int[] { coords[0], validateColumn(coords[1] + 1) };
        }
        if (move.equalsIgnoreCase("W")) {
            roomCoords = new int[] { coords[0], validateColumn(coords[1] - 1) };
        }
        // Return the room number of the requested room
        return roomCoords[0] * caveRows + roomCoords[1] + 1;
    }

    /**
     * Checks if the player has already been in a room
     * 
     * @param roomNumber - the number of the room being checked
     * @return whether the player has been in a room previously
     */
    public boolean checkUnique(int roomNumber) {
        for (int i = 0; i < previousRooms.size(); i++) {
            // Look for the given room in the arrayList of previous rooms
            if (previousRooms.get(i) == roomNumber) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a given room is safe
     * 
     * @param roomNumber - the number of the room being checked
     * @return whether the given room is safe
     */
    public boolean checkSafe(int roomNumber) {
        // If the room contains a bat return false
        if (bats.contains(roomNumber)) {
            return false;
        }
        if (wumpus.containsKey(roomNumber) && pits.containsKey(roomNumber)) {
            // If there is either a wumpus or a pit return false
            return !(wumpus.get(roomNumber) || pits.get(roomNumber));
        }
        // If there isnt a wumpus, bat or pit then return true
        return true;
    }

    /**
     * Checks if the wumpus' location has been pinpointed
     */
    public void checkWumpusMap() {
        ArrayList<Integer> roomNumbers = new ArrayList<>();
        // Looks for all the possible rooms the wumpus could be in
        for (int name : wumpus.keySet()) {
            if (wumpus.get(name)) {
                roomNumbers.add(name);
            }
        }
        // If there is more than 0 then the player has been near the wumpus
        if (roomNumbers.size() > 0) {
            beenNearWumpus = true;
        }
        // If there is exactly one then set that as the location of the wumpus
        if (roomNumbers.size() == 1) {
            wumpusLocation = this.roomNumbers.get(roomNumbers.get(0));
        }
    }

    /**
     * Updates the players surroundings in the given map
     * 
     * @param map      - the map being updated
     * @param near     - whether the player is near the entity being tracked in the
     *                 map
     * @param beenNear - whether the player has been near entities of that type
     *                 before
     */
    public void find(HashMap<Integer, Boolean> map, boolean near, boolean beenNear) {
        // Get the players positions
        int[] coords = roomNumbers.get(roomNumber);
        ArrayList<Integer> newTrue = new ArrayList<>();
        // Look through all the coordinates surrounding the square
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                // Update whether that cell is safe to walk into
                if (!(i == 0 && j == 0)) {
                    updateMap(map, validateRow(coords[0] + i) * caveRows + validateColumn(coords[1] + j) + 1, near,
                            beenNear);
                    // If the player has been near the entity before then keep track of all the new
                    // true rooms
                    if (beenNear && near) {
                        newTrue.add(validateRow(coords[0] + i) * caveRows + validateColumn(coords[1] + j) + 1);
                    }
                }
            }
        }

        if (beenNear && near) {
            // Look for all the old true rooms in the new true rooms
            ArrayList<Integer> oldTrueRooms = getTrue(map);
            for (int oldTrue : oldTrueRooms) {
                // If an old true value is not in the new true values then set it to false
                if (!newTrue.contains(oldTrue)) {
                    map.put(oldTrue, false);
                }
            }
        }
    }

    /**
     * Updates the contents of the given map for the supplied room number
     * 
     * @param map        - the map that is being updated
     * @param roomNumber - number of the room being updated
     * @param near       - whether the player is near the entity being tracked
     * @param beenNear   - whather the player has previously been near the entity
     *                   being tracked
     */
    public void updateMap(HashMap<Integer, Boolean> map, int roomNumber, boolean near, boolean beenNear) {
        // Check if the player has already been near the treasure/wumpus (if so discard
        // all new true values)
        if (beenNear) {
            // If the room is not already in a map that contains true values then it is
            // false
            if (!map.containsKey(roomNumber)) {
                map.put(roomNumber, false);
            }
            // If the room is already in the map and false then return
            else if (map.containsKey(roomNumber) && !map.get(roomNumber)) {
                return;
            }
            // If it is in the map and true then update the value to be the new value
            else {
                map.put(roomNumber, near);
            }
            return;
        }

        // If the room is false in the map then leave it alone
        if (map.containsKey(roomNumber) && !map.get(roomNumber)) {
            return;
        }
        // Otherwise add the room and the corresponding value to the map
        map.put(roomNumber, near);
    }

    /**
     * Gets all the rooms with the value true currently in the given map
     * 
     * @param map - the given map
     * @return an arrayList of all the true values
     */
    public ArrayList<Integer> getTrue(HashMap<Integer, Boolean> map) {
        ArrayList<Integer> possibleRooms = new ArrayList<>();
        // Add rooms with the value true to the arrayList
        for (int roomNumber : map.keySet()) {
            if (map.get(roomNumber)) {
                possibleRooms.add(roomNumber);
            }
        }

        return possibleRooms;
    }

    /**
     * Check if the player has previously been next to the treasure
     */
    public void checkTreasureMap() {
        for (int roomNumber : treasure.keySet()) {
            if (treasure.get(roomNumber)) {
                beenNearTreasure = true;
                return;
            }
        }
    }

    /**
     * Chooses a move that moves the player towards specific coordinates
     * 
     * @param coords    - the coordinates the player is moving towards
     * @param safeMoves - the current safe moves
     * @return the optimal move
     */
    public String moveTowards(int[] coords, String safeMoves) {
        int[] location = roomNumbers.get(roomNumber);
        // If they are not in the same row
        if (location[0] != coords[0]) {
            if (location[0] > coords[0]) { // Player is further down
                // If it is further to travel through the board
                if (Math.abs(location[0] - coords[0]) > (coords[0] + caveRows - location[0])) {
                    if (safeMoves.contains("S")) {
                        return "S";
                    }
                } else {
                    if (safeMoves.contains("N")) {
                        return "N";
                    }
                }
            } else {
                // If it is further to travel through the board
                if (Math.abs(location[0] - coords[0]) > (caveRows - coords[0] + location[0])) {
                    if (safeMoves.contains("N")) {
                        return "N";
                    }
                } else {
                    if (safeMoves.contains("S")) {
                        return "S";
                    }
                }
            }
        }
        // If they are not in the same column
        if (location[1] != coords[1]) {
            if (location[1] > coords[1]) { // Player is further right
                // If it is further to travel through the board
                if (Math.abs(location[1] - coords[1]) > (coords[1] + caveColumns - location[1])) {
                    if (safeMoves.contains("E")) {
                        return "E";
                    }
                } else {
                    if (safeMoves.contains("W")) {
                        return "W";
                    }
                }
            } else {
                // If it is further to travel through the board
                if (Math.abs(location[1] - coords[1]) > (caveColumns - coords[1] + location[1])) {
                    if (safeMoves.contains("W")) {
                        return "W";
                    }
                } else {
                    if (safeMoves.contains("E")) {
                        return "E";
                    }
                }
            }
        }

        // If none of the optimal moves are safe then move randomly
        return chooseRandom(safeMoves);
    }

    /**
     * Gets the location of the superbat if the player was picked up by one
     */
    public void checkBats() {
        if (surroundings[4]) {
            // Only get the coords if there is a second last room
            if(previousRooms.size() < 2){
                return;
            }
            // Get the coords from the second to last room
            int[] previousCoords = roomNumbers.get(previousRooms.get(previousRooms.size() - 2));
            int[] batCoords = { previousCoords[0], previousCoords[1] };
            String move = previousMoves.get(previousMoves.size() - 1);
            // Get the coordinates of the room the bat was in beased on the move needed to
            // get there
            if (move.equalsIgnoreCase("N")) {
                batCoords[0] = validateRow(previousCoords[0] - 1);
            }
            if (move.equalsIgnoreCase("S")) {
                batCoords[0] = validateRow(previousCoords[0] + 1);
            }
            if (move.equalsIgnoreCase("E")) {
                batCoords[1] = validateColumn(previousCoords[1] + 1);
            }
            if (move.equalsIgnoreCase("W")) {
                batCoords[1] = validateColumn(previousCoords[1] - 1);
            }

            // Add the room to the list of bat rooms
            bats.add(batCoords[0] * caveRows + batCoords[1] + 1);
        }
    }

    /**
     * Checks if the player has been made blind
     */
    public void checkBlind() {
        // Start counting rounds if the player cannot see
        if (!surroundings[5]) {
            blind = 5;
        }
    }

    /**
     * Checks if the player has lost their sense of smell
     */
    public void checkNose() {
        // Start counting rounds if the player cannot smell
        if (!surroundings[3]) {
            blockedNose = 5;
        }
    }

    /**
     * Gets the reverse of the player's last move so they retrace their steps
     * 
     * @return the reverse of the previous move
     */
    public String moveBackwards() {
        // Get the previous move
        String previousMove = previousMoves.get(previousMoves.size() - 1);
        // Reverse the move
        if (previousMove.equalsIgnoreCase("N") && nsew.contains("S")) {
            return "S";
        }
        if (previousMove.equalsIgnoreCase("S") && nsew.contains("N")) {
            return "N";
        }
        if (previousMove.equalsIgnoreCase("E") && nsew.contains("w")) {
            return "W";
        }
        if (previousMove.equalsIgnoreCase("W") && nsew.contains("E")) {
            return "E";
        }

        // If unable to get a previous move then just get random move
        return chooseRandom(getSafeMoves());
    }

    /**
     * Gets a move in the direction of the treasure
     * 
     * @param safeMoves - the moves that are safe
     * @return a move in the direction of the treasure
     */
    public String moveToTreasure(String safeMoves) {
        // Create a list of all the possible rooms that the treasure could be in
        ArrayList<Integer> possibleRooms = new ArrayList<>();
        for (int room : treasure.keySet()) {
            if (treasure.get(room)) {
                possibleRooms.add(room);
            }
        }

        // Pick a random one to move towards
        Random random = new Random();
        int index = random.nextInt(possibleRooms.size());
        int[] coords = roomNumbers.get(possibleRooms.get(index));
        // Get a move in the direction of the random room
        return moveTowards(coords, safeMoves);
    }

    /**
     * Gets whether the player is in a room adjacent to the wumpus
     * 
     * @return whether the player is next to the wumpus
     */
    public boolean getWumpusAdjacent() {
        // If the wumpus has not been found then it is not in an adjacent square
        if (wumpusLocation == null) {
            return false;
        }
        String[] directions = nsew.split("-");
        // Check every possible direction
        for (int i = 0; i < directions.length; i++) {
            // Get the coords of the rooms in every direction
            int[] coords = roomNumbers.get(getRoomMove(directions[i]));
            // Check if they contain the wumpus
            if (coords[0] == wumpusLocation[0] && coords[1] == wumpusLocation[1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the direction to shoot in to hit the wumpus
     * 
     * @return the direction the player should shoot in
     */
    public String shootWumpus() {
        String[] directions = nsew.split("-");
        // Get the direction that the wumpus is in
        for (int i = 0; i < directions.length; i++) {
            int[] coords = roomNumbers.get(getRoomMove(directions[i]));
            if (coords[0] == wumpusLocation[0] && coords[1] == wumpusLocation[1]) {
                return directions[i];
            }
        }
        // Should never reach this because method is always called after establishing
        // the wumpus is in an adjacent room
        return chooseRandom(nsew); // If wumpus is not there then shoot in a random direction
    }

    /**
     * Makes the row values toroidal
     * 
     * @param row - the row value
     * @return the validated row value
     */
    public int validateRow(int row) {
        if (row < 0) {
            row = caveRows - 1;
        }
        if (row >= caveRows) {
            row = 0;
        }
        return row;
    }

    /**
     * Makes the column values toroidal
     * 
     * @param column - the column value
     * @return the validated column value
     */
    public int validateColumn(int column) {
        if (column < 0) {
            column = caveColumns - 1;
        }
        if (column >= caveColumns) {
            column = 0;
        }
        return column;
    }

    /**
     * Checks whether the player is stuck in an endless loop of repeating moves
     * 
     * @return whether the player has been repeating the same two moves
     */
    public boolean checkPreviousMoves() {
        if (previousRooms.size() < 8) {
            return false;
        }
        Set<Integer> duplicates = new HashSet<>();
        for (int i = 1; i < 9; i++) {
            duplicates.add(previousRooms.get(previousRooms.size() - i));
        }
        if (duplicates.size() < 3) {
            return true;
        }
        return false;
    }

}
