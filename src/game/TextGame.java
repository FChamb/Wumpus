package game;

import java.util.*;

public class TextGame {
    // attributes
    private Cave cave; // cave object that the game works around
    // boolean to run the game
    private boolean playing = true;
    // attribute containing an arraylist of booleans representing where the player
    // has been
    private ArrayList<ArrayList<Boolean>> displayBoard = new ArrayList<>();
    // scanner to ask for user input
    private Scanner scanner = new Scanner(System.in);

    // information for the ai player
    private AI aiPlayer;
    private boolean ai;
    private boolean[] surroundings = {false, false, false};
    private boolean smell = true;

    // Counters for blindness and loss of smell
    private int blind = -1;
    private int blockedNose = -1;

    public static void main(String[] args) {
        TextGame test = new TextGame(new Player("player"));
        test.playGame();
    }

    public TextGame(Player player) {
        // Set up the board
        setUp(player);
        setUpBoard(cave);
    }

    public Cave getCave() {
        return this.cave;
    }

    // Method to play through the motions of the game
    public void playGame() {
        // Check the player has not died off the start <- not possible i dont think but
        // who knows
        int[] coords = cave.getPlayer().getCoords();
        checkCell(coords[0], coords[1]);

        while (playing) {

            // print the cave (including the players position) (only when not blind)
            if (blind < 0) {
                updateDisplayBoard(cave.getPlayer());
                // printBoard(cave.getPlayer());
                printCaveDetails(); // for testing purposes
            }

            // print out the room number the player is in
            int roomNumber = coords[0] * cave.xSize + coords[1] + 1; // calculates the correct room number
            printRoom(roomNumber);

            // check content of neighbouring cells
            checkNeighbours(coords[0], coords[1]);

            // print out the neighbouring cells
            String nsew = getWalls();
            printNeighbours(nsew);

            // give the ai player the required information before asking them to make a move
            if(ai){
                aiPlayer.setInfo(roomNumber, surroundings, nsew, smell);
                smell = true; // set smell back to true after informing the ai so it only gets told the round it happens
                surroundings = new boolean[]{false, false, false}; // reset whether it is next to something
            }


            // get the players next move
            getMove();
            coords = cave.getPlayer().getCoords();

            // check current cell is safe
            checkCell(coords[0], coords[1]);
            System.out.println();

            // count the number of rounds played with presets
            blind--;
            blockedNose--;

            // if the ai is playing make the whole game wait
            if(ai){
                try{
                    Thread.sleep(1000);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void printCaveDetails() {
        int[] wumpusCoords = cave.getWumpus().getCoords();
        int[] playerCoords = cave.getPlayer().getCoords();
        for (int i = 0; i < cave.xSize; i++) {
            for (int j = 0; j < cave.ySize; j++) {
                if (i == wumpusCoords[0] && j == wumpusCoords[1]) {
                    System.out.print(cave.getWumpus());
                } else if (i == playerCoords[0] && j == playerCoords[1]) {
                    System.out.print(cave.getPlayer());
                } else {
                    System.out.print(cave.getLayout()[i][j]);
                }
            }
            System.out.println();
        }
    }

    // Method to check the current cell
    public void checkCell(int row, int column) {
        // get the specific room in the cave + its type
        Room room = cave.getLayout()[row][column];
        int[] coords = cave.getWumpus().getCoords();
        // If the wumpus is in the room
        if (row == coords[0] && column == coords[1] && cave.getWumpus().isAlive()) {
            // action performed if the wumpus is in the room and alive
            if (!cave.getPlayer().useShield()) { // if the player does not have a shield
                printWumpusLoss();
                playing = false; // end the game
                return;
            } else {
                printShieldUse(); // the player lives if they have a shield
                moveWumpus(); // move the wumpus to another room
            }
        }

        // Check the different room types
        String type = room.getType();
        if (type.equals("o")) { // pit room
            printPitLoss();
            playing = false; // end the game
        } else if (type.equals("w")) { // superbat room
            printBat();
            // update the players position
            placePlayer();
        }
        // Only check for artefacts if the room is safe
        else {
            checkArtefact(room);
        }
        if (type.equals("G") && !cave.getPlayer().hadFoundTreasure()) { // treasure room
            printTreasureFound();
            cave.getPlayer().findTreasure(); // set the treasure as having been found
        }
        if (type.equals("X") && cave.getPlayer().hadFoundTreasure()) { // exit room
            printVictory();
            playing = false;
        }
    }

    // Method for checking if the room contains an artefact
    public void checkArtefact(Room room) {
        Artifact artefact = room.getArtifact();
        if (artefact == null) { // If the artefact is null there are is no artefact
            return;
        }
        // Print out the effect associated with the artefact
        printFoundArtefact(artefact);

        // Work out what sort of artefact it is
        String name = artefact.getName();
        if (name.equals("D")) { // shield <- defence against wumpus once
            cave.getPlayer().addItem(artefact); // add the shield to the inventory
        }
        if (name.equals("U")) { // weird drinking water <- loose sense of smell for 5 rounds
            blockedNose = 5;
            smell = false;
        }
        if (name.equals(">")) { // arrow <- gain an arrow
            cave.getPlayer().addArrow(); // Give the player another arrow
        }
        if (name.equals("~")) { // dusty room <- print empty board for 5 rounds
            blind = 5;
        }

        // Remove the artefact from the room once it has been picked up
        room.setArtifact(null);
    }

    public void checkNeighbours(int row, int column) {
        boolean pit = false; // Keeps track of if the pit message has already been printed to avoid printing
                             // it twice
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                // discard the middle cell from checking
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
                    if (type.equals("o") && !pit) {
                        printPit();
                        surroundings[1] = true;
                        pit = true; // Make sure it does not print the pit message again
                    }
                    // Only print that the treasure is nearby if the treasure has not been found
                    if (type.equals("G") && !cave.getPlayer().hadFoundTreasure()) {
                        printTreasure();
                        surroundings[2] = true;
                    }
                }
            }
        }
    }

    public int validateRow(int row) {
        if (row < 0) {
            row = cave.xSize - 1;
        }
        if (row >= cave.xSize) {
            row = 0;
        }
        return row;
    }

    public int validateColumn(int column) {
        if (column < 0) {
            column = cave.ySize - 1;
        }
        if (column >= cave.ySize) {
            column = 0;
        }
        return column;
    }

    // Method to get how many of the adjacent squares contain walls
    public String getWalls() {
        int[] playerCoords = cave.getPlayer().getCoords();
        int[] coords = new int[] { playerCoords[0], playerCoords[1] }; // dont even know if i need these but we will see
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

        // return the string minus the final '-'
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    // Method to place the player and the wumpus in the cave
    public void placePlayer() {
        int[] coords = this.cave.getRandom();
        cave.getPlayer().setCoords(coords[0], coords[1]);
    }

    public void placeWumpus() {
        int[] coords = this.cave.getRandom();
        cave.getWumpus().setCoords(coords[0], coords[1]);
    }

    // Method to move the wumpus specifically to an adjacent square
    public void moveWumpus() {
        int[] coords = cave.getWumpus().getCoords();
        Random random = new Random();
        int row = random.nextInt(3) - 1; // will return a number [-1,1]
        int column = random.nextInt(3) - 1;

        // Make sure the wumpus actually moves and does not just stay still
        while (row == 0 && column == 0) {
            row = random.nextInt(3) - 1;
            column = random.nextInt(3) - 1;
        }

        cave.getWumpus().setCoords(validateRow(coords[0] + row), validateColumn(coords[1] + column));
    }

    // Method to move without a room number
    public void moveRoom(int[] coords) {
        cave.getPlayer().setCoords(coords[0], coords[1]);
    }

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
                // the wumpus moves after being hit <- if it is still alive
                printWumpusKill();
                printWumpusLives();
                moveWumpus();
            }
        } else {
            printWumpusMiss();
            // move the wumpus to an adjacent room
            moveWumpus();
        }
    }

    // Method to set up the display board based on the board size
    public void setUpBoard(Cave cave) {
        int rows = cave.getLayout().length;
        int columns = cave.getLayout()[0].length;
        for (int i = 0; i < rows; i++) {
            displayBoard.add(new ArrayList<>());
            for (int j = 0; j < columns; j++) {
                displayBoard.get(i).add(false);
            }
        }
    }

    // updates the display board to show where the player has been
    public void updateDisplayBoard(Player player) {
        int[] coords = player.getCoords();
        displayBoard.get(coords[0]).set(coords[1], true);
    }

    // Method to display the basic board
    public void printBoard(Player player) {
        // Code to print a different letter if the player has a name beginning with an X
        String icon = "X ";
        if (cave.getPlayer().toString().toLowerCase().contains("x")) {
            icon = "V ";
        }
        int[] playerCoords = player.getCoords();
        for (int i = 0; i < displayBoard.size(); i++) {
            for (int j = 0; j < displayBoard.get(i).size(); j++) {
                // if the player is in that position print "O"
                if (i == playerCoords[0] && j == playerCoords[1]) {
                    // Print out the players symbol
                    System.out.print(cave.getPlayer().toString().substring(1));
                }
                // if the cave is true, the player has been there
                else if (displayBoard.get(i).get(j)) {
                    System.out.print(icon); // X or V depending on player name
                }
                // if the cave is false, the player has not been there
                else if (!displayBoard.get(i).get(j)) {
                    System.out.print(". "); // .
                }
            }
            System.out.println();
        }
    }

    public void getMove() {
        System.out.println("Shoot or Move (S-M)?");
        String decision = "";
        if(ai){
            decision = aiPlayer.makeMove().toLowerCase();
            // code to get input from the ai
        }
        else{
            decision = scanner.nextLine().toLowerCase();
        }
        //String decision = scanner.nextLine().toLowerCase();
        if (decision.equals("s")) {
            // print the number of arrows the player has
            printArrows(cave.getPlayer());
            // call the method for shooting
            if (cave.getPlayer().getNumOfArrows() > 0) {
                shootRoom(getRoomCoords(getDirection(false, getWalls())));
            }
        } else if (decision.equals("m")) {
            // call the method for moving
            moveRoom(getRoomCoords(getDirection(true, getWalls())));
        } else {
            // call the method again if the input is invalid
            getMove();
        }
    }

    // Method for get direction that uses only the available squares
    public String getDirection(boolean move, String nsew) {
        if (move) {
            System.out.println("What direction do you want to move (" + nsew + ")?");
        } else {
            System.out.println("What direction do you want to shoot (" + nsew + ")?");
        }
        String direction = "";
        if(ai){
            direction = aiPlayer.chooseDirection();
            // code to get input from the ai
        }
        else{
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
        // If it is not equal to any of the possible directions then get another input
        if (counter != 1) {
            direction = getDirection(move, nsew);
            return direction;
        }

        return direction.toLowerCase();
    }

    // Method for getting a room position from N/S/E/W
    public int[] getRoomCoords(String nsew) {
        // Get the players position
        int[] playerCoords = cave.getPlayer().getCoords();
        // Make new coords to stop it from also changing the location of the player when
        // the player shoots
        int[] coords = new int[] { playerCoords[0], playerCoords[1] };
        if (nsew.equals("n")) {
            coords[0] = validateRow(playerCoords[0] - 1); // N moves the played one upwards
        }
        if (nsew.equals("s")) {
            coords[0] = validateRow(playerCoords[0] + 1); // S moves the player one downwards
        }
        if (nsew.equals("e")) {
            coords[1] = validateColumn(playerCoords[1] + 1); // E moves the player one to the right
        }
        if (nsew.equals("w")) {
            coords[1] = validateColumn(playerCoords[1] - 1); // W moves the player one to the left
        }
        // Return the room number associated with the new room
        return coords;
    }

    // Methods for getting info about setting up the game
    public void setUp(Player player) {
        player.setName(setName());
        int height = setDimensions(true);
        int width = setDimensions(false);
        aiPlayer = new AI(height, width); // Tell the AI the size of the caves
        int total = height * width - (height + width) - 1;
        int walls = (int) ((setWalls(total) / 100) * total);
        total -= walls;
        int bats = setLayout(false, total);
        total -= bats;
        int pits = setLayout(true, total);
        total -= pits;
        int artifacts = setArtifacts(total);
        player.setArrows(setArrows()); // Set the number of arrows the player has
        cave = new Cave(height, width, pits, bats, walls, artifacts, player);
        cave.getWumpus().setLives(setWumpusLives()); // Set the number of lives the Wumpus has
    }

    public String setName() {
        System.out.println("Please enter player name");
        String name = scanner.nextLine();
        if (name.length() < 1) {
            return setName();
        }
        // Detect if the player wants the ai to play
        if(name.equalsIgnoreCase("ai")){
            ai = true;
        }
        return name;
    }

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

    public int setArrows() {
        System.out.println("Please enter the number of arrows the player should have");
        String number = scanner.nextLine();

        int arrows = 0;

        // Check it is a number
        try {
            arrows = Integer.parseInt(number);
            // If it is not an integer then ask for input again
        } catch (NumberFormatException e) {
            arrows = setArrows();
            return arrows;
        }

        // Check it is in the desired range [1, infinity]
        if (arrows < 1) {
            arrows = setArrows();
            return arrows;
        }

        return arrows;
    }

    public boolean setWumpus() {
        System.out.println("Do you want to be required to kill the wumpus (Y-N)?");
        String answer = scanner.nextLine().toLowerCase();
        if (answer.equals("y")) {
            return true;
        }
        if (answer.equals("n")) {
            return false;
        }
        return setWumpus();
    }

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

        // Make sure the Wumpus has at least one life
        if (lives < 1) {
            lives = setWumpusLives();
            return lives;
        }

        return lives;
    }

    public int setLayout(boolean pits, int total) {
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

        // Check it is in the desired range [1, infinity]
        if (layout < 0 || layout > total) {
            layout = setLayout(pits, total);
            return layout;
        }

        return layout;
    }

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
        // Check it is in the desired range [1, infinity]
        if (layout < 0 || layout > 100) {
            layout = setWalls(total);
            return layout;
        }
        if (layout == 100) {
            layout = 99;
        }
        return layout;
    }

    public int setArtifacts(int total) {
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
        // Check it is in the desired range [1, infinity]
        if (layout < 0 || layout > total) {
            layout = setArtifacts(total);
            return layout;
        }

        return layout;
    }

    public void printWumpus() {
        System.out.println("You smell the wumpus");
    }

    public void printPit() {
        System.out.println("You feel a breeze");
    }

    public void printTreasure() {
        System.out.println("You see a shiny glitteringness");
    }

    public void printRoom(int roomNumber) {
        System.out.println("You are in room " + roomNumber);
    }

    // Version of the print neighbours method that takes into account where the
    // walls are <- needs hella work
    public void printNeighbours(String nsew) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tunnels lead to the ");
        ArrayList<String> words = new ArrayList<>();
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

    // Messages to print when the player losses the game
    public void printPitLoss() {
        System.out.println("You have fallen in a bottomless pit and cannot escape");
    }

    public void printWumpusLoss() {
        System.out.println("The wumpus has found you");
    }

    public void printWumpusKill() {
        System.out.println("You hear a terrible cry. You're arrow must have hit the wumpus");
    }

    public void printVictory() {
        System.out.println("You have found the treasure and escaped the cave");
        System.out.println("Congratulations on winning the game");
    }

    public void printBat() {
        System.out.println("A superbat has picked you up");
    }

    public void printTreasureFound() {
        System.out.println("You have found the treasure");
    }

    public void printWumpusMiss() {
        System.out.println("You hear the sound of an arrow hitting stone");
    }

    public void printNoArrows() {
        System.out.println("You have run out of arrows");
    }

    public void printArrows(Player player) {
        System.out.println("You have " + player.getNumOfArrows() + " arrows");
    }

    public void printWumpusLives() {
        int lives = cave.getWumpus().getLives();
        if (lives == 0) {
            System.out.println("That last cry was particularly pained. The wumpus must be dead now");
        } else {
            System.out.println("By the sounds of things the wumpus can only take " + lives + " more arrows");
        }
    }

    public void printShieldUse() {
        System.out.println("You managed to narrowly survive a run in with the wumpus but your shield is now broken");
    }

    public void printFoundArtefact(Artifact artefact) {
        System.out.println("You see something inside the cave");
        System.out.println(artefact.getAbility());
    }
}
