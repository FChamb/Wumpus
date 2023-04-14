package display;

import java.util.List;

import game.Cave;
import game.Command;
import game.Room;

/**
 * Wrapper class for {@link Terminal}.
 */
public class Display {

    /**
     * The "HUNT THE WUMPUS" title text.
     */
    private static final String titleBlock = """
 ╔═╗ ╔═╗ ╔═╗ ╔═╗ ╔═╗ ╔═╗ ╔═════╗    ╔═════╗ ╔═╗ ╔═╗ ╔═════╗    ╔═╗╔═╗╔═╗ ╔═╗ ╔═╗ ╔═╗   ╔═╗ ╔═════╗ ╔═╗ ╔═╗ ╔═════╗
┌╢ ╚═╝ ║┌╢ ║┌╢ ║┌╢ ╚╗║ ║┌╚═╗ ╔╤╝   ┌╚═╗ ╔╤╝┌╢ ╚═╝ ║┌╢ ╔═══╝   ┌╢ ╠╝ ╚╣ ║┌╢ ║┌╢ ║┌╢ ╚╗ ╔╝ ║┌╢ ╔═╗ ║┌╢ ║┌║ ║┌╢ ╔═══╝
│║ ╔═╗ ║│║ ║│║ ║│║  ╚╝ ║└─┐║ ╟┘    └─┐║ ╟┘ │║ ╔═╗ ║│║ ╠═══    │║  ╔═╗  ║│║ ║│║ ║│║  ╚═╝  ║│║ ╚═╝ ║│║ ║│║ ║│║ ╚═══╗
│║ ╟┐║ ║│║ ╚╧╝ ║│║ ╔╗  ║  │║ ║       │║ ║  │║ ║┐║ ║│║ ╚═══╗   │║ ╔╝┐╚╗ ║│║ ╚╧╝ ║│║ ╠╗ ╔╣ ║│║ ╔══╤╝│║ ╚╧╝ ║│╠════ ║
│╚╤╝│╚╤╝│╚════╤╝│╚╤╝╚═╤╝  │╚╤╝       │╚╤╝  │╚╤╝│╚╤╝│╚════╤╝   │╚╤╝ └┐╚╤╝│╚════╤╝│╚╤╝╚╤╝╚╤╝│╚╤╝──┘ │╚════╤╝├╚════╤╝
└─┘ └─┘ └─────┘ └─┘└──┘   └─┘        └─┘   └─┘ └─┘ └─────┘    └─┘   └─┘ └─────┘ └─┘└─┘└─┘ └─┘     └─────┘ └─────┘
""";


    /**
     * The {@link Terminal terminal}.
     */
    private Terminal terminal;
    /**
     * The {@link Terminal.Cursor cursor}.
     */
    private Terminal.Cursor cursor;


    public Display() {
        terminal = new Terminal();
        cursor = terminal.cursor;

        cursor.resetStyle();
        terminal.clearScreen(0, 0, 0);
    }


    /**
     * Prints the {@linkplain scenes.MenuScene menu scene}.
     */
    public void printTitleScreen() {
        cursor.resetStyle();

        cursor.move(49, 5);
        cursor.setForegroundColour(255, 127, 0); cursor.setBackgroundColour(0, 0, 0);
        terminal.printFixed(titleBlock);


        cursor.setBold(true);

        cursor.move(90, 20); cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(0, 191,  63);
        terminal.print("N");

        cursor.swapColours();
        terminal.print("ew Game");

        cursor.move(110, 20);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(191, 0, 0);
        terminal.print("Q");

        cursor.swapColours();
        terminal.print("uit Game");


        cursor.move(101, 24);
        cursor.setForegroundColour(255, 255, 255); cursor.setBold(false);
        terminal.print(": ");
    }
    /**
     * Sets the {@linkplain scenes.MenuScene menu scene's} cursor.
     */
    public void setTitleCursor() {
        cursor.resetStyle();

        cursor.move(103, 24);

        cursor.setUnderline(true);
        cursor.setForegroundColour(255, 255, 255); cursor.setBold(false);
        terminal.print(" ");
        
        cursor.move(103, 24);
        cursor.setVisible(true);
    }


    /**
     * Prints the {@linkplain scenes.GameScene game scene's} input line.
     * 
     * @param x the x position
     * @param y the y position
     */
    public void printGameInput(int x, int y) {
        cursor.resetStyle();

        cursor.move(x, y);
        cursor.setForegroundColour(191, 127,   0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(true);
        terminal.print("Enter Command ");
        
        cursor.setForegroundColour(191, 127, 127); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(false);
        terminal.print("[  or   for help]");

        cursor.swapColours(); cursor.setBold(true);
        cursor.shift(-16, 0); terminal.print("h");
        cursor.shift(  4, 0); terminal.print("?");

        cursor.shift(12, 0);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0);
        terminal.print(":");
    }
    /**
     * Sets the {@linkplain scenes.GameScene game scene's} cursor.
     * 
     * @param x the x position
     * @param y the y position
     */
    public void setGameCursor(int x, int y) {
        cursor.resetStyle();

        cursor.move(x, y);

        cursor.setUnderline(true);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0);
        terminal.print("          ");

        cursor.move(x, y);
        cursor.setVisible(true);
    }
    /**
     * Prints a status message. Used in {@link scenes.GameScene}.
     * 
     * @param x       the x position
     * @param y       the y position
     * @param message the status message
     */
    public void printGameStatus(int x, int y, String message) {
        cursor.resetStyle();

        cursor.move(x, y);
        cursor.setForegroundColour(191, 127,   0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(true);

        String sub_message,  messages[] = message.split("\n");
        int length = messages.length;
        for(int i = 0; i < length; i++) {
            sub_message = messages[i];
            cursor.move(x - message.length()/2, y + (length - i));
            terminal.print(sub_message);
        }
    }
    

    /**
     * Prints the game board as well as the percepts. Used in {@link scenes.GameScene}.
     * 
     * @param show_all        decides if each room should be displayed
     * @param cx              the center's x position
     * @param cy              the center's y position
     * @param cave            the {@linkplain game.TextGame game's} {@link game.Cave Cave} object.
     * @param player_tracks   the player's tracks
     * @param percept_message the player's percepts
     * @param is_blind        decides if the entire board should be printed
     */
    public void printGameBoard(boolean show_all, int cx, int cy, Cave cave, List<List<Boolean>> player_tracks, String percept_message, boolean is_blind) {
        int width = player_tracks.size(), height = player_tracks.get(0).size();
        int[] pos = cave.getPlayer().getCoords();
        Room[][] rooms = cave.getLayout();

        int temp = height; height = width; width = temp;

        int x = cx - width, y = cy - height/2;

        String text, message, messages[];
        int j, i, length;

        cursor.resetStyle();

        // print N E S W symbols
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(255, 255, 255); cursor.setBold(true); cursor.setItalics(true);
        cursor.move(x +   width - 1, y            - 3);
        terminal.print(" N ");

        cursor.swapColours();

        cursor.move(x + 2*width + 2, y + height/2    );
        terminal.print("E");

        cursor.move(x +   width    , y + height   + 2);
        terminal.print("S");

        cursor.move(x           - 4, y + height/2    );
        terminal.print("W");


        cursor.move(x, y);
        cursor.setItalics(false); cursor.setBold(false);
        int[] wumpusCoords = cave.getWumpus().getCoords();
        if(!is_blind) {
            // print plain board
            text = "";
            for(j = 0; j < height; j++) {
                for(i = 0; i < width; i++)
                    text += "." + (i == width-1 ? "" : " ");
                text += (j == height-1 ? "" : "\n");
            }
            terminal.printFixed(text);

            // print rooms
            cursor.setForegroundColour(95, 95, 95); cursor.setBackgroundColour(0, 0, 0);
            for(j = 0; j < height; j++)
                for(i = 0; i < width; i++) {
                    cursor.move(x + 2*i, y + j);
                    if(rooms[j][i].getType().equals(""+Room.WLL)) {
                        terminal.print(" ");
                    }
                    if(show_all) terminal.print(rooms[j][i].toString().substring(1,2));
                }
            
            // print wumpus
            if(show_all) {
                cursor.move(x + 2*wumpusCoords[1], y + wumpusCoords[0]);
                terminal.print("!");
            }
        }

        // print tracks
        cursor.setForegroundColour(127, 127, 127); cursor.setBackgroundColour(31, 31, 31);

        for(j = 0; j < height; j++)
            for(i = 0; i < width; i++)
                if(player_tracks.get(j).get(i)) {
                    cursor.move(x + 2*i, y + j);
                    terminal.print(".");

                    // connect tunnel
                    if(i+1 <  width && player_tracks.get(j).get(i+1)) {
                        cursor.move(x + 2*i + 1, y + j);
                        terminal.print(" ");
                    }
                    if(i-1 >=     0 && player_tracks.get(j).get(i-1)) {
                        cursor.move(x + 2*i - 1, y + j);
                        terminal.print(" ");
                    }
                }
        
        // print player
        cursor.move(x+2*pos[1], y+pos[0]);
        cursor.setForegroundColour(191, 127, 0); cursor.setBackgroundColour(31, 31, 31); cursor.setBold(true);
        
        terminal.print(""+cave.getPlayer().getIcon());

        // print percepts
        cursor.setForegroundColour(255, 31, 31); cursor.setBackgroundColour(0, 0, 0);

        messages = percept_message.split("\n");
        length = messages.length;
        for(i = 0; i < length; i++) {
            message = messages[i];
            cursor.move(x + width - message.length()/2, (y - 4) - (length - i));
            terminal.print(message);
        }
    }


    /**
     * Prints the {@linkplain scenes.QuitScene quit scene}.
     */
    public void printQuitScreen() {
        cursor.resetStyle();

        cursor.move(82, 14);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0);
        terminal.print("Are you sure you want to quit the game?");

        cursor.move(97, 20);
        cursor.setBold(true);
        terminal.print("[ / ] : ");

        cursor.shift(-7, 0);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(0, 191, 63); cursor.setBold(false); cursor.setUnderline(false);
        terminal.print("Y");

        cursor.shift(1, 0);
        cursor.setBackgroundColour(191, 0, 0);
        terminal.print("N");
    }
    /**
     * Sets the {@linkplain scenes.QuitScene quit scene's} cursor.
     */
    public void setQuitCursor() {
        cursor.resetStyle();

        cursor.move(105, 20);

        cursor.setUnderline(true);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0); cursor.setUnderline(true);
        terminal.print(" ");

        cursor.move(105, 20);
        cursor.setVisible(true);
    }


    /**
     * Prints the {@linkplain game.Command#printHelp help display}. Used by {@link scenes.GameScene}.
     * 
     * @param x the x position
     * @param y the y position
     */
    public void printHelp(int x, int y) {
        Command.printHelp(x, y, terminal);
    }

    /**
     * Prints an error message.
     * 
     * @param x       the x position
     * @param y       the y position
     * @param message the error message
     */
    public void printError(int x, int y, String message) {
        cursor.resetStyle();
        cursor.move(x, y);
        cursor.setForegroundColour(127, 0, 0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(true);
        terminal.print(message);
    }

    /**
     * Prints the win message. Used by {@link scenes.EndScene}.
     * 
     * @param message the {@linkplain #printEndMessage end message}
     */
    public void printWinMessage(String message) {
        cursor.resetStyle();

        cursor.move(100, 10);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(63, 191, 63); cursor.setBold(true); cursor.setBlinking(true);
        terminal.print("You WIN!");

        printEndMessage(message);
    }
    /**
     * Prints the lose message. Used by {@link scenes.EndScene}.
     * 
     * @param message the {@linkplain #printEndMessage end message}
     */
    public void printLoseMessage(String message) {
        cursor.resetStyle();

        cursor.move(100, 10);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(223, 63, 63); cursor.setBold(true); cursor.setBlinking(true);
        terminal.print("You LOSE!");

        printEndMessage(message);
    }
    /**
     * Prints the end message. Used by {@link scenes.EndScene}.
     * 
     * @param message the end message
     */
    public void printEndMessage(String message) {
        cursor.move(105 - message.length()/2, 20);
        cursor.swapColours(); cursor.setBlinking(false);
        terminal.print(message);

        cursor.move(95, 40);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(false);
        terminal.print("Press enter to exit");

        cursor.move(1, 46);
        cursor.setForegroundColour(0, 0, 0);
    }


    /**
     * Sets the {@linkplain scenes.SetupScene setup scene's} cursor.
     */
    public void setSetupCursor() {
        cursor.resetStyle();

        cursor.setVisible(true);
    }


    /**
     * Clears the screen.
     */
    public void clearScreen() {
        terminal.clearScreen(0, 0, 0);
    }


    /**
     * Dispose the display.
     */
    public void dispose() {
        terminal.dispose();
        terminal = null;
    }
}