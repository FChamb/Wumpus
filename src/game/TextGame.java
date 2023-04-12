package game;

import java.util.*;

import scenes.GameScene;

public class TextGame {
    // attributes
    private Cave cave; // Cave for the game
    private boolean playing = true; // Boolean to run the game
    private List<List<Boolean>> displayBoard = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    // Information for the ai player
    private AI aiPlayer;
    private boolean ai;
    // nearWumpus, nearPit, nearTreasure, able to smell, picked up by bat,
    // able to see, foundTreasure, foundExit
    private boolean[] surroundings = { false, false, false, true, false, true, false, false };
    // Counters for blindness and loss of smell
    private int blind = -1;
    private int blockedNose = -1;
    // Attributes for playing against the AI
    private boolean playingAI;
    private Cave aiCave;
    private boolean killWumpus = false;

    public static void main(String[] args) {
        TextGame test = new TextGame();
        test.playGame();
    }

    /**
     * Constructor to set up the cave for the game
     */
    public TextGame() {
        // Set up the board
        setUp(new Player("player"));
        setUpBoard();
    }

    /**
     * Runs the function of the game
     */
    public void playGame() {
        // Get the players coordinates
        int[] coords = cave.getPlayer().getCoords();
        int round = 0;
        while (playing) {
            // print the cave when not blind
            if (blind < 0) {
                updateDisplayBoard();
                // printBoard();
                printCaveDetails(); // for testing purposes
            }

            // Check current cell is safe
            checkCell(coords[0], coords[1]);
            // End the game if the player has lost
            if (!playing) {
                break;
            }
            // Update the players coordinates
            coords = cave.getPlayer().getCoords();

            // Print out the room number the player is in
            int roomNumber = coords[0] * cave.xSize + coords[1] + 1;
            printRoom(roomNumber);

            // Check content of neighbouring cells
            checkNeighbours(coords[0], coords[1]);

            // Print out the neighbouring cells
            String nsew = getWalls();
            printNeighbours(nsew);

            // Give the ai the required information
            if (ai) {
                aiPlayer.setInfo(roomNumber, surroundings, nsew);
                // Reset all the booleans for next round
                surroundings = new boolean[] { false, false, false, true, false, true, false, false };
            }

            // Get the players next move
            getMove();

            // Update the players coordinates after performing move
            coords = cave.getPlayer().getCoords();
            System.out.println();

            // Update the counters
            blind--;
            blockedNose--;
            round++;

            // If the ai is playing make the whole game wait
            /*
             * if (ai) {
             * try {
             * Thread.sleep(1000);
             * } catch (Exception e) {
             * e.printStackTrace();
             * }
             * }
             */
        }

        // Print details of the game after playing (for testing the effectiveness of the
        // AI)
        System.out.println("It took " + round + " moves");
        if (!cave.getWumpus().isAlive()) {
            System.out.println("The Wumpus was successfully killed");
        }
    }

    /**
     * Runs the function of the game -- compatable with GUI
     */
    public void stepGame(GameScene scene) {
        // Get the players coordinates
        int[] coords = cave.getPlayer().getCoords();

        // End the game if the player has lost
        if (!playing) {
            return;
        }
        // Update the players coordinates
        coords = cave.getPlayer().getCoords();

        // Print out the room number the player is in
        int roomNumber = coords[0] * cave.xSize + coords[1] + 1;
        ////// scene.printRoom(roomNumber);

        // Check content of neighbouring cells
        checkNeighbours(scene, coords[0], coords[1]);

        // Print out the neighbouring cells
        String nsew = getWalls();
        ////// scene.printNeighbours(nsew);

        // Give the ai the required information
        if (ai) {
            aiPlayer.setInfo(roomNumber, surroundings, nsew);
            // Reset all the booleans for next round
            surroundings = new boolean[] { false, false, false, true, false, true, surroundings[6], false };
        }

        // print the cave when not blind
        if (blind < 0) {
            updateDisplayBoard();
            scene.printBoard(cave, displayBoard);
            // printCaveDetails(); // for testing purposes
        }

        // Get the players next move
        scene.getMove();

        // Check current cell is safe
        checkCell(scene, coords[0], coords[1]);

        // Update the players coordinates after performing move
        coords = cave.getPlayer().getCoords();
        // System.out.println();

        // Update the counters
        blind--;
        blockedNose--;

        // If the ai is playing make the whole game wait
        if (ai) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints the contents of the cave, including the locations of the treasure and
     * the wumpus
     */
    public void printCaveDetails() {
        int[] wumpusCoords = cave.getWumpus().getCoords();
        int[] playerCoords = cave.getPlayer().getCoords();
        for (int i = 0; i < cave.xSize; i++) {
            for (int j = 0; j < cave.ySize; j++) {
                if (i == wumpusCoords[0] && j == wumpusCoords[1]) {
                    // Print the wumpus
                    System.out.print(cave.getWumpus());
                } else if (i == playerCoords[0] && j == playerCoords[1]) {
                    // Print the player
                    System.out.print(cave.getPlayer());
                } else {
                    // Print the room
                    System.out.print(cave.getLayout()[i][j]);
                }
            }
            System.out.println();
        }
    }

    public void checkCell(GameScene scene, int row, int column) {
        // Get the specific room in the cave
        Room room = cave.getLayout()[row][column];
        int[] coords = cave.getWumpus().getCoords();
        // Check if the wumpus is in the room
        if (row == coords[0] && column == coords[1] && cave.getWumpus().isAlive()) {
            if (!cave.getPlayer().useShield()) { // If the player does not have a shield
                scene.printLoss("The wumpus has found you");
                playing = false; // End the game
                return;
            } else {
                // The player lives if they have a shield
                scene.setStatusMessage("You managed to narrowly survive a run in with the wumpus but your shield is now broken");
                moveWumpus(); // Move the wumpus
            }
        }

        // Check the different room types
        String type = room.getType();
        if (type.equals("w")) { // Superbat room
            scene.setStatusMessage("A superbat has picked you up");
            // Update the players position
            cave.setPlayer();
            surroundings[4] = true;
            int[] playerCoords = cave.getPlayer().getCoords();
            // Print the new cave layout
            if (blind < 0) {
                updateDisplayBoard();
                // printBoard();
                scene.printBoard(cave, displayBoard); // for testing purposes
            }
            checkCell(playerCoords[0], playerCoords[1]);
            return;
        }
        if (type.equals("o")) { // Pit room
            scene.printLoss("You have fallen in a bottomless pit and cannot escape");
            playing = false; // End the game
            return;
        }
        // Only check for artefacts if the room is safe
        checkArtefact(scene, room);

        // Look for positive room types
        if (type.equals("G") && !cave.getPlayer().hadFoundTreasure()) { // Treasure room
            scene.setStatusMessage("You have found the treasure");
            cave.getPlayer().findTreasure(); // Set the treasure as having been found
            surroundings[6] = true;
        }
        if (type.equals("X")) { // Exit room
            surroundings[7] = true;
            if (!cave.getPlayer().hadFoundTreasure()) {
                scene.setStatusMessage("You see a big door in the cave. It looks like it needs a key to be opened, maybe there is one with the treasure");
            }
            // Tell the player where the exit is if they have not killed the wumpus
            else if (killWumpus && !cave.getWumpus().isAlive()) {
                scene.setStatusMessage("You see a big door in the cave. It looks like it needs a key to be opened, maybe there is one with the wumpus");
            }
            // Print the player has won if they are not required to kill the wumpus
            if (cave.getPlayer().hadFoundTreasure() && !killWumpus) {
                scene.printVictory();
                playing = false;
            } else if (killWumpus && !cave.getWumpus().isAlive() && cave.getPlayer().hadFoundTreasure()) {
                scene.printVictory();
                playing = false;
            }
        }
    }

    /**
     * Checks that the current cell is safe
     * 
     * @param row    - row the player is in
     * @param column - column the player is in
     */
    public void checkCell(int row, int column) {
        // Get the specific room in the cave
        Room room = cave.getLayout()[row][column];
        int[] coords = cave.getWumpus().getCoords();
        // Check if the wumpus is in the room
        if (row == coords[0] && column == coords[1] && cave.getWumpus().isAlive()) {
            if (!cave.getPlayer().useShield()) { // If the player does not have a shield
                printWumpusLoss();
                printLoss();
                playing = false; // End the game
                return;
            } else {
                printShieldUse(); // The player lives if they have a shield
                moveWumpus(); // Move the wumpus
            }
        }

        // Check the different room types
        String type = room.getType();
        if (type.equals("w")) { // Superbat room
            printBat();
            // Update the players position
            cave.setPlayer();
            surroundings[4] = true;
            int[] playerCoords = cave.getPlayer().getCoords();
            // Print the new cave layout
            if (blind < 0) {
                updateDisplayBoard();
                // printBoard();
                printCaveDetails(); // for testing purposes
            }
            checkCell(playerCoords[0], playerCoords[1]);
            return;
        }
        if (type.equals("o")) { // Pit room
            printPitLoss();
            printLoss();
            playing = false; // End the game
            return;
        }
        // Only check for artefacts if the room is safe
        checkArtefact(room);

        // Look for positive room types
        if (type.equals("G") && !cave.getPlayer().hadFoundTreasure()) { // Treasure room
            printTreasureFound();
            cave.getPlayer().findTreasure(); // Set the treasure as having been found
            surroundings[6] = true;
        }
        if (type.equals("X")) { // Exit room
            surroundings[7] = true;
            if (!cave.getPlayer().hadFoundTreasure()) {
                printExit(false);
            }
            // Tell the player where the exit is if they have not killed the wumpus
            else if (killWumpus && !cave.getWumpus().isAlive()) {
                printExit(true);
            }
            // Print the player has won if they are not required to kill the wumpus
            if (cave.getPlayer().hadFoundTreasure() && !killWumpus) {
                printVictory();
                playing = false;
            } else if (killWumpus && !cave.getWumpus().isAlive() && cave.getPlayer().hadFoundTreasure()) {
                printVictory();
                playing = false;
            }
        }
    }

    public void checkArtefact(GameScene scene, Room room) {
        Artifact artefact = room.getArtifact();
        // If the artefact is null there are is no artefact
        if (artefact == null) {
            return;
        }
        // Print out the effect associated with the artefact
        scene.setStatusMessage("You see something inside the cave" + artefact.getAbility());

        // Work out what sort of artefact it is
        String name = artefact.getName();
        if (name.equals("D")) { // Shield
            // Add the shield to the inventory
            cave.getPlayer().addItem(artefact);
        }
        if (name.equals("U")) { // Weird drinking water
            // Make the player unable to smell for 5 rounds
            blockedNose = 5;
            surroundings[3] = false;
        }
        if (name.equals(">")) { // Arrow
            // Give the player another arrow
            cave.getPlayer().addArrow();
        }
        if (name.equals("~")) { // Dusty room
            // Make the player unable to see for 5 rounds
            blind = 5;
            surroundings[5] = false;
        }

        // Remove the artefact from the room once it has been picked up
        room.setArtifact(null);
    }

    /**
     * Checks if the given room contains an artefact
     * 
     * @param room - the room being checked
     */
    public void checkArtefact(Room room) {
        Artifact artefact = room.getArtifact();
        // If the artefact is null there are is no artefact
        if (artefact == null) {
            return;
        }
        // Print out the effect associated with the artefact
        printFoundArtefact(artefact);

        // Work out what sort of artefact it is
        String name = artefact.getName();
        if (name.equals("D")) { // Shield
            // Add the shield to the inventory
            cave.getPlayer().addItem(artefact);
        }
        if (name.equals("U")) { // Weird drinking water
            // Make the player unable to smell for 5 rounds
            blockedNose = 5;
            surroundings[3] = false;
        }
        if (name.equals(">")) { // Arrow
            // Give the player another arrow
            cave.getPlayer().addArrow();
        }
        if (name.equals("~")) { // Dusty room
            // Make the player unable to see for 5 rounds
            blind = 5;
            surroundings[5] = false;
        }

        // Remove the artefact from the room once it has been picked up
        room.setArtifact(null);
    }

    public void checkNeighbours(GameScene scene, int row, int column) {
        boolean pit = false; // Boolean to only print the pit message once
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                // Do not check the middle cell
                if (!(i == 0 && j == 0)) {
                    // Make the board toroidal
                    int checkRow = validateRow(row + i);
                    int checkColumn = validateColumn(column + j);

                    // Perform the actual checks on each cell
                    Room room = cave.getLayout()[checkRow][checkColumn];
                    int[] coords = cave.getWumpus().getCoords();
                    if (coords[0] == checkRow && coords[1] == checkColumn && cave.getWumpus().isAlive()) {
                        // Only smell the wumpus when the nose is not blocked
                        if (blockedNose < 0) {
                            scene.setPerceptMessage("You smell the wumpus");
                            surroundings[0] = true;
                        }
                    }

                    // Check the different room types
                    String type = room.getType();
                    // If it is a pit
                    if (type.equals("o") && !pit) {
                        scene.setPerceptMessage("You feel a breeze");
                        surroundings[1] = true;
                        pit = true; // Only print the message once
                    }
                    // Only print that the treasure is nearby if the treasure has not been found
                    if (type.equals("G") && !cave.getPlayer().hadFoundTreasure() && blind < 0) {
                        scene.setPerceptMessage("You see a shiny glitteringness");
                        surroundings[2] = true;
                    }
                }
            }
        }
    }

    /**
     * Checks the contents of the neighbouring rooms to the given room
     * 
     * @param row    - the row of the room
     * @param column - the column of the room
     */
    public void checkNeighbours(int row, int column) {
        boolean pit = false; // Boolean to only print the pit message once
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                // Do not check the middle cell
                if (!(i == 0 && j == 0)) {
                    // Make the board toroidal
                    int checkRow = validateRow(row + i);
                    int checkColumn = validateColumn(column + j);

                    // Perform the actual checks on each cell
                    Room room = cave.getLayout()[checkRow][checkColumn];
                    int[] coords = cave.getWumpus().getCoords();
                    if (coords[0] == checkRow && coords[1] == checkColumn && cave.getWumpus().isAlive()) {
                        // Only smell the wumpus when the nose is not blocked
                        if (blockedNose < 0) {
                            printWumpus();
                            surroundings[0] = true;
                        }
                    }

                    // Check the different room types
                    String type = room.getType();
                    // If it is a pit
                    if (type.equals("o") && !pit) {
                        printPit();
                        surroundings[1] = true;
                        pit = true; // Only print the message once
                    }
                    // Only print that the treasure is nearby if the treasure has not been found
                    if (type.equals("G") && !cave.getPlayer().hadFoundTreasure() && blind < 0) {
                        printTreasure();
                        surroundings[2] = true;
                    }
                }
            }
        }
    }

    /**
     * Make the rows toroidal
     * 
     * @param row - the row being checked
     * @return the validated row value
     */
    public int validateRow(int row) {
        if (row < 0) {
            row = cave.xSize - 1;
        }
        if (row >= cave.xSize) {
            row = 0;
        }
        return row;
    }

    /**
     * Make the columns toroidal
     * 
     * @param column - the column being checked
     * @return the validated column value
     */
    public int validateColumn(int column) {
        if (column < 0) {
            column = cave.ySize - 1;
        }
        if (column >= cave.ySize) {
            column = 0;
        }
        return column;
    }

    /**
     * Gets the positions of the walls surrounding the player and returns the
     * directions the player can move in based on that
     * 
     * @return the possible directions the player can move in
     */
    public String getWalls() {
        int[] coords = cave.getPlayer().getCoords();
        StringBuilder builder = new StringBuilder();

        // Check if all the adjacent squares are not walls
        if (!cave.getLayout()[validateRow(coords[0] - 1)][coords[1]].getType().equals(" ")) {
            builder.append("N-"); // N moves the played one upwards
        }
        if (!cave.getLayout()[validateRow(coords[0] + 1)][coords[1]].getType().equals(" ")) {
            builder.append("S-"); // S moves the player one downwards
        }
        if (!cave.getLayout()[coords[0]][validateColumn(coords[1] + 1)].getType().equals(" ")) {
            builder.append("E-"); // E moves the player one to the right
        }
        if (!cave.getLayout()[coords[0]][validateColumn(coords[1] - 1)].getType().equals(" ")) {
            builder.append("W-"); // W moves the player one to the left
        }

        // Return the string minus the final '-'
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    /**
     * Moves the wumpus into one of the surrounding squares
     */
    public void moveWumpus() {
        int[] coords = cave.getWumpus().getCoords();
        Random random = new Random();
        int row = random.nextInt(3) - 1; // Will return a number [-1,1]
        int column = random.nextInt(3) - 1;

        int[] playerCoords = cave.getPlayer().getCoords();
        // Make sure the wumpus moves and does not stay still
        // or move to where the player is
        while ((row == 0 && column == 0) || (playerCoords[0] == validateRow(coords[0] + row)
                && playerCoords[1] == validateColumn(coords[1] + column))) {
            row = random.nextInt(3) - 1;
            column = random.nextInt(3) - 1;
        }

        cave.getWumpus().setCoords(validateRow(coords[0] + row), validateColumn(coords[1] + column));
    }

    /**
     * Moves the players location to the provided coordinates
     * 
     * @param coords - the coordinates the player is moving to
     */
    public void moveRoom(int[] coords) {
        cave.getPlayer().setCoords(coords[0], coords[1]);
    }

    /**
     * Shoots the given room
     * 
     * @param coords - coordinates of the room being shot
     */
    public void shootRoom(int[] coords) {
        // Shooting does nothing if the wumpus is dead or if the player has no arrows
        if (!cave.getWumpus().isAlive() || !cave.getPlayer().useArrow()) {
            return;
        }
        // Check if the wumpus is in the room
        int[] wumpusCoords = cave.getWumpus().getCoords();
        if (coords[0] == wumpusCoords[0] && coords[1] == wumpusCoords[1]) {
            // Use one of the wumpus' lives
            if (cave.getWumpus().hitWumpus()) {
                // The wumpus moves after being hit
                printWumpusKill();
                printWumpusLives();
                moveWumpus();
            }
        } else {
            printWumpusMiss();
            // Move the wumpus to an adjacent room
            moveWumpus();
        }
    }

    /**
     * Creates a board of booleans containing where the player has been on the board
     */
    public void setUpBoard() {
        for (int i = 0; i < cave.xSize; i++) {
            displayBoard.add(new ArrayList<>());
            for (int j = 0; j < cave.ySize; j++) {
                // Create the entire board to be false
                displayBoard.get(i).add(false);
            }
        }
    }

    /**
     * Updates where the player has been on the board
     */
    public void updateDisplayBoard() {
        int[] coords = cave.getPlayer().getCoords();
        // Set places where the player has been to true
        displayBoard.get(coords[0]).set(coords[1], true);
    }

    /**
     * Prints the board of where the player has been (including walls)
     */
    public void printBoard() {
        // Print a different letter if the player has a name beginning with an X
        String icon = "X ";
        if (cave.getPlayer().toString().toLowerCase().contains("x")) {
            icon = "V ";
        }
        int[] playerCoords = cave.getPlayer().getCoords();
        for (int i = 0; i < displayBoard.size(); i++) {
            for (int j = 0; j < displayBoard.get(i).size(); j++) {
                // If the player is in that position
                if (i == playerCoords[0] && j == playerCoords[1]) {
                    // Print out the players symbol
                    System.out.print(cave.getPlayer().toString().substring(1));
                }
                // If it is a wall print nothing
                else if (cave.getLayout()[i][j].getType().equals(" ")) {
                    System.out.print("  ");
                }
                // If the cave is true, the player has been there
                else if (displayBoard.get(i).get(j)) {
                    System.out.print(icon); // X or V depending on player name
                }
                // If the cave is false, the player has not been there
                else if (!displayBoard.get(i).get(j)) {
                    System.out.print(". "); // .
                }
            }
            System.out.println();
        }
    }

    public void getMove(GameScene scene, String decision, String direction) {
        // System.out.println("Shoot or Move (S-M)?");
        decision = decision.toLowerCase();

        // If the player decides to shoot
        if (decision.equals("s")) {
            // Print the number of arrows the player has
            // printArrows();
            if (cave.getPlayer().getNumOfArrows() > 0) {
                shootRoom(getRoomCoords(direction));
            }
            else scene.setStatusMessage("You tried to shoot an arrow, but you have no more arrows left!");
        } // If the player decides to move
        else if (decision.equals("m")) {
            moveRoom(getRoomCoords(direction));
        }
    }

    /**
     * Gets whether the player wants to move or shoot
     */
    public void getMove() {
        System.out.println("Shoot or Move (S-M)?");
        String decision = "";
        if (ai) { // Get input from the AI if it is playing
            decision = aiPlayer.makeMove().toLowerCase();
        } else { // Otherwise get input from the player
            decision = scanner.nextLine().toLowerCase();
        }

        // If the player decides to shoot
        if (decision.equals("s")) {
            // Print the number of arrows the player has
            printArrows();
            if (cave.getPlayer().getNumOfArrows() > 0) {
                shootRoom(getRoomCoords(getDirection(false, getWalls())));
            }
        } // If the player decides to move
        else if (decision.equals("m")) {
            moveRoom(getRoomCoords(getDirection(true, getWalls())));
        } else {
            // Call the method again if the input is invalid
            getMove();
        }
    }

    /**
     * Gets the direction the player wants to move/shoot in
     * 
     * @param move - whether the player is moving or shooting
     * @param nsew - the possible directions
     * @return the decided upon direction
     */
    public String getDirection(boolean move, String nsew) {
        // Print different prompt depending on whether the player is moving or shooting
        if (move) {
            System.out.println("What direction do you want to move (" + nsew + ")?");
        } else {
            System.out.println("What direction do you want to shoot (" + nsew + ")?");
        }
        String direction = "";
        if (ai) {
            direction = aiPlayer.chooseDirection();
        } else {
            direction = scanner.nextLine();
        }
        String[] directions = nsew.split("-");

        int counter = 0;
        // Check that the direction is valid
        for (int i = 0; i < directions.length; i++) {
            if (direction.equalsIgnoreCase(directions[i])) {
                counter++;
            }
        }
        // If it is not valid then get another input
        if (counter != 1) {
            direction = getDirection(move, nsew);
            return direction;
        }

        return direction.toLowerCase();
    }

    /**
     * Gets the coordinates of the room that is being moved/shot into, based on the
     * provided move
     * 
     * @param nsew - the direction being moved in
     * @return the coordinates of the room being moved/shot into
     */
    public int[] getRoomCoords(String nsew) {
        // Get the players position
        int[] playerCoords = cave.getPlayer().getCoords();
        int[] coords = new int[] { playerCoords[0], playerCoords[1] };
        if (nsew.equals("n")) { // N moves the played one upwards
            coords[0] = validateRow(playerCoords[0] - 1);
        }
        if (nsew.equals("s")) { // S moves the player one downwards
            coords[0] = validateRow(playerCoords[0] + 1);
        }
        if (nsew.equals("e")) { // E moves the player one to the right
            coords[1] = validateColumn(playerCoords[1] + 1);
        }
        if (nsew.equals("w")) { // W moves the player one to the left
            coords[1] = validateColumn(playerCoords[1] - 1);
        }
        // Return the coordinates of the room
        return coords;
    }

    /**
     * Sets up the cave for the game based on user input
     * 
     * @param player
     */
    public void setUp(Player player) {
        // player.setName(setName());
        if (!ai) {
            playingAI = setPlayingAI();
        }
        player.setName("@tha");
        // setName();
        // int height = setDimensions(true);
        // int width = setDimensions(false);
        int height = 30; int width = 30;
        aiPlayer = new AI(height, width);
        int total = height * width - (height + width);
        // int walls = (int) ((setWalls(total) / 100) * total);
        int walls = (int) ((35d / 100) * total);
        total -= walls;
        // int bats = setLayout(false, total);
        int bats = 11;
        total -= bats;
        // int pits = setLayout(true, total);
        int pits = 20;
        total -= pits;
        // int artifacts = setArtifacts(total);
        int artifacts = 4;
        // player.setArrows(setArrows()); // Set the number of arrows the player has
        player.setArrows(5); // Set the number of arrows the player has
        cave = new Cave(height, width, pits, bats, walls, artifacts, player);
        // cave.getWumpus().setLives(setWumpusLives()); // Set the number of lives the Wumpus has
        cave.getWumpus().setLives(3); // Set the number of lives the Wumpus has
        if (!ai && !playingAI) {
            killWumpus = setWumpus();
        }
        // Set up the AI's cave to be identical to the players cave
        if (playingAI) {
            aiCave = new Cave(cave);
        }
    }

    /**
     * Gets input for the players name. Entering AI will make the AI play the game
     * 
     * @return the player's name
     */
    public String setName() {
        System.out.println("Please enter player name");
        String name = scanner.nextLine();
        // If there is no name after for new input
        if (name.length() < 1) {
            return setName();
        }
        // If the player is called 'AI' let the AI play
        if (name.equalsIgnoreCase("ai")) {
            ai = true;
            name = "@";
        }
        return name;
    }

    /**
     * Gets input for the height/width of the cave
     * 
     * @param height - boolean for whether the input is for width or height
     * @return the inputted integer
     */
    public int setDimensions(boolean height) {
        if (height) {
            System.out.println("Please enter the height of the cave");
        } else {
            System.out.println("Please enter the width of the cave");
        }
        String number = scanner.nextLine();

        int dimension = 0;

        // Check it is a number
        try {
            dimension = Integer.parseInt(number);
            // If it is not an integer then ask for input again
        } catch (NumberFormatException e) {
            dimension = setDimensions(height);
            return dimension;
        }

        // Check it is in the desired range [5,20]
        if (dimension < 5 || dimension > 20) {
            dimension = setDimensions(height);
            return dimension;
        }

        return dimension;
    }

    /**
     * Gets user input for the number or arrows the player should have
     * 
     * @return the number of arrows the player has inputted
     */
    public int setArrows() {
        System.out.println("Please enter the number of arrows the player should have");
        String number = scanner.nextLine();

        int arrows = -1;

        // Check it is a number
        try {
            arrows = Integer.parseInt(number);
            // If it is not an integer then ask for input again
        } catch (NumberFormatException e) {
            arrows = setArrows();
            return arrows;
        }

        // Check it is in the desired range [0, infinity]
        if (arrows < 0) {
            arrows = setArrows();
            return arrows;
        }

        return arrows;
    }

    /**
     * Gets user input for the number of lives the wumpus should have
     * 
     * @return the number of lives inputted by the user
     */
    public int setWumpusLives() {
        System.out.println("How many lives do you want the wumpus to have?");
        String number = scanner.nextLine();
        int lives = -1;

        try {
            lives = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            lives = setWumpusLives();
            return lives;
        }

        // Make sure the Wumpus does not have negative lives
        if (lives < 0) {
            lives = setWumpusLives();
            return lives;
        }

        return lives;
    }

    /**
     * Gets user input for the number of superbats/bottomless pits in the cave
     * 
     * @param pits  - boolean for whether the input is for the number of bats or
     *              pits
     * @param total - how many empty rooms in the cave there is
     * @return the inputted number of pits
     */
    public int setLayout(boolean pits, int total) {
        // If there is no free space default to 0
        if (total == 0) {
            return 0;
        }
        if (pits) {
            System.out.println("Please enter the number of bottomless pits in the cave");
        } else {
            System.out.println("Please enter the number of superbats in the cave");
        }
        System.out.println("You have " + total + " free spaces available.");
        String number = scanner.nextLine();

        int layout = 0;

        // Check it is a number
        try {
            layout = Integer.parseInt(number);
            // If it is not an integer then ask for input again
        } catch (NumberFormatException e) {
            layout = setLayout(pits, total);
            return layout;
        }

        // Check it is in the desired range [1, total free space]
        if (layout < 0 || layout > total) {
            layout = setLayout(pits, total);
            return layout;
        }

        return layout;
    }

    /**
     * Gets user input for the percentage of the board that should be walls
     * 
     * @param total - the total amount of free space in the cave
     * @return the percentage of the board that should be walls, inputted by the
     *         user
     */
    public double setWalls(int total) {
        System.out.println("Please enter the percent of walls you would like");
        System.out.println("You have " + total + " free spaces available.");
        String percent = scanner.nextLine();
        double layout = 0;
        // Check it is a number
        try {
            layout = Double.parseDouble(percent);
            // If it is not an integer then ask for input again
        } catch (NumberFormatException e) {
            layout = setWalls(total);
            return layout;
        }
        // Check it is in the desired range [1, 100]
        if (layout < 0 || layout > 100) {
            layout = setWalls(total);
            return layout;
        }
        return layout;
    }

    /**
     * Gets user input for the number of artefacts there should be in the cave
     * 
     * @param total - the total number of free rooms in the cave
     * @return the number of artefacts inputted by the user
     */
    public int setArtifacts(int total) {
        // If there is no free space default to 0
        if (total == 0) {
            return 0;
        }
        System.out.println("Please enter the number of artifacts you would like");
        System.out.println("You have " + total + " free spaces available.");
        String number = scanner.nextLine();
        int layout = 0;
        // Check it is a number
        try {
            layout = Integer.parseInt(number);
            // If it is not an integer then ask for input again
        } catch (NumberFormatException e) {
            layout = setArtifacts(total);
            return layout;
        }
        // Check it is in the desired range [1, total free space]
        if (layout < 0 || layout > total) {
            layout = setArtifacts(total);
            return layout;
        }

        return layout;
    }

    /**
     * Gets user input about whether the user wants to be required to kill the
     * wumpus
     * 
     * @return boolean indicating whether the player wants to be required to kill
     *         the wumpus
     */
    public Boolean setWumpus() {
        System.out.println("Do you want to have to kill the Wumpus (Y-N)?");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("Y")) {
            return true;
        } else if (answer.equalsIgnoreCase("N")) {
            return false;
        } else {
            return setWumpus();
        }
    }

    /**
     * Gets use input about whether the player would like to play against the AI
     * 
     * @return boolean about whether the player wants to play against the AI
     */
    public Boolean setPlayingAI() {
        System.out.println("Do you want to race against the AI (Y-N)?");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("Y")) {
            return true;
        } else if (answer.equalsIgnoreCase("N")) {
            return false;
        } else {
            return setPlayingAI();
        }
    }

    /**
     * Prints a message for if the wumpus is nearby
     */
    public void printWumpus() {
        System.out.println("You smell the wumpus");
    }

    /**
     * Prints a message for if a pit is nearby
     */
    public void printPit() {
        System.out.println("You feel a breeze");
    }

    /**
     * Prints a message for if the treasure is nearby
     */
    public void printTreasure() {
        System.out.println("You see a shiny glitteringness");
    }

    /**
     * Prints a message telling the player what room they are in
     * 
     * @param roomNumber - the room the player is in
     */
    public void printRoom(int roomNumber) {
        System.out.println("You are in room " + roomNumber);
    }

    /**
     * Prints the direction in which the player can move
     * 
     * @param nsew - the valid directions that do not have walls
     */
    public void printNeighbours(String nsew) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tunnels lead to the ");
        ArrayList<String> words = new ArrayList<>();
        // If a direction is valid then add it to the arrayList
        if (nsew.contains("N")) {
            words.add("north");
        }
        if (nsew.contains("S")) {
            words.add("south");
        }
        if (nsew.contains("E")) {
            words.add("east");
        }
        if (nsew.contains("W")) {
            words.add("west");
        }

        for (int i = 0; i < words.size() - 1; i++) {
            builder.append(words.get(i) + ", ");
        }

        builder.append("and " + words.get(words.size() - 1));

        System.out.println(builder);
    }

    /**
     * Prints message for falling in a pit
     */
    public void printPitLoss() {
        System.out.println("You have fallen in a bottomless pit and cannot escape");
    }

    /**
     * Prints message for getting killed by the wumpus
     */
    public void printWumpusLoss() {
        System.out.println("The wumpus has found you");
    }

    /**
     * Prints message for hitting the wumpus with an arrow
     */
    public void printWumpusKill() {
        System.out.println("You hear a terrible cry. You're arrow must have hit the wumpus");
    }

    /**
     * Prints message for winning the game
     */
    public void printVictory() {
        System.out.println("You have found the treasure and escaped the cave");
        System.out.println("Congratulations on winning the game");
    }

    /**
     * Prints message for getting picked up by a bat
     */
    public void printBat() {
        System.out.println("A superbat has picked you up");
    }

    /**
     * Prints message for finding the treasure
     */
    public void printTreasureFound() {
        System.out.println("You have found the treasure");
    }

    /**
     * Prints message for failing to hit the wumpus with an arrow
     */
    public void printWumpusMiss() {
        System.out.println("You hear the sound of an arrow hitting stone");
    }

    /**
     * Prints message for when the player has run out of arrows
     */
    public void printNoArrows() {
        System.out.println("You have run out of arrows");
    }

    /**
     * Prints the number of arrows the player has
     */
    public void printArrows() {
        System.out.println("You have " + cave.getPlayer().getNumOfArrows() + " arrows");
    }

    /**
     * Prints the number of lives the wumpus has
     */
    public void printWumpusLives() {
        int lives = cave.getWumpus().getLives();
        if (lives == 0) {
            System.out.println("That last cry was particularly pained. The wumpus must be dead now");
        } else if (lives == 1) {
            System.out.println("By the sounds of things the wumpus can only take 1 more arrow");
        } else {
            System.out.println("By the sounds of things the wumpus can only take " + lives + " more arrows");
        }
    }

    /**
     * Prints message about the player using their shield to defend an attack from
     * the wumpus
     */
    public void printShieldUse() {
        System.out.println("You managed to narrowly survive a run in with the wumpus but your shield is now broken");
    }

    /**
     * Prints message about finding an artefact
     * 
     * @param artefact - the artefact that was found
     */
    public void printFoundArtefact(Artifact artefact) {
        System.out.println("You see something inside the cave");
        System.out.println(artefact.getAbility());
    }

    /**
     * Prints message about finding the exit
     * 
     * @param wumpus - whether the player is required to kill the wumpus or not
     */
    public void printExit(boolean wumpus) {
        if (wumpus) {
            System.out.println(
                    "You see a big door in the cave. It looks like it needs a key to be opened, maybe there is one with the wumpus");
        } else {
            System.out.println(
                    "You see a big door in the cave. It looks like it needs a key to be opened, maybe there is one with the treasure");
        }
    }

    /**
     * Prints message about losing the game
     */
    public void printLoss() {
        System.out.println("You lost the game. Better luck next time");
    }
}
