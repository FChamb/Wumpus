package display;

import java.util.List;

import game.Cave;
import game.Command;
import game.Room;

public class Display {

    private static final String titleBlock = """
 ╔═╗ ╔═╗ ╔═╗ ╔═╗ ╔═╗ ╔═╗ ╔═════╗    ╔═════╗ ╔═╗ ╔═╗ ╔═════╗    ╔═╗╔═╗╔═╗ ╔═╗ ╔═╗ ╔═╗   ╔═╗ ╔═════╗ ╔═╗ ╔═╗ ╔═════╗
┌╢ ╚═╝ ║┌╢ ║┌╢ ║┌╢ ╚╗║ ║┌╚═╗ ╔╤╝   ┌╚═╗ ╔╤╝┌╢ ╚═╝ ║┌╢ ╔═══╝   ┌╢ ╠╝ ╚╣ ║┌╢ ║┌╢ ║┌╢ ╚╗ ╔╝ ║┌╢ ╔═╗ ║┌╢ ║┌║ ║┌╢ ╔═══╝
│║ ╔═╗ ║│║ ║│║ ║│║  ╚╝ ║└─┐║ ╟┘    └─┐║ ╟┘ │║ ╔═╗ ║│║ ╠═══    │║  ╔═╗  ║│║ ║│║ ║│║  ╚═╝  ║│║ ╚═╝ ║│║ ║│║ ║│║ ╚═══╗
│║ ╟┐║ ║│║ ╚╧╝ ║│║ ╔╗  ║  │║ ║       │║ ║  │║ ║┐║ ║│║ ╚═══╗   │║ ╔╝┐╚╗ ║│║ ╚╧╝ ║│║ ╠╗ ╔╣ ║│║ ╔══╤╝│║ ╚╧╝ ║│╠════ ║
│╚╤╝│╚╤╝│╚════╤╝│╚╤╝╚═╤╝  │╚╤╝       │╚╤╝  │╚╤╝│╚╤╝│╚════╤╝   │╚╤╝ └┐╚╤╝│╚════╤╝│╚╤╝╚╤╝╚╤╝│╚╤╝──┘ │╚════╤╝├╚════╤╝
└─┘ └─┘ └─────┘ └─┘└──┘   └─┘        └─┘   └─┘ └─┘ └─────┘    └─┘   └─┘ └─────┘ └─┘└─┘└─┘ └─┘     └─────┘ └─────┘
""";


    private Terminal terminal;
    private Terminal.Cursor cursor;


    public Display() {
        terminal = new Terminal();
        cursor = terminal.cursor;

        cursor.resetStyle();
        terminal.clearScreen(0, 0, 0);
    }


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
    public void setTitleCursor() {
        cursor.resetStyle();

        cursor.move(103, 24);

        cursor.setUnderline(true);
        cursor.setForegroundColour(255, 255, 255); cursor.setBold(false);
        terminal.print(" ");
        
        cursor.move(103, 24);
        cursor.setVisible(true);
    }


    public void printGameInput(int x, int y) {
        cursor.resetStyle();

        cursor.move(x  , y);
        // cursor.move(58, 30);
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
    public void setGameCursor(int x, int y) {
        cursor.resetStyle();

        cursor.move(x, y);
        // cursor.move(93, 30);

        cursor.setUnderline(true);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0);
        terminal.print("          ");

        cursor.move(x, y);
        // cursor.move(93, 30);
        cursor.setVisible(true);
    }
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
                        terminal.print(" "); //▓
                    }
                    if(show_all) {
                        terminal.print(rooms[j][i].toString().substring(1,2));
                        // if(rooms[j][i].getType().equals(""+Room.BAT)) {
                        //     terminal.print(""+Room.BAT/*"≡"*/); //▓
                        // }
                        // if(rooms[j][i].getType().equals(""+Room.PIT)) {
                        //     terminal.print("o"); //▓
                        // }
                        // if(rooms[j][i].getType().equals(""+Room.TSR)) {
                        //     terminal.print("&"); //▓
                        // }
                        // if(rooms[j][i].getType().equals(""+Room.EXT)) {
                        //     terminal.print("X"); //▓
                        // }
                    }
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

        // // set cursor for percepts
        // cursor.move(x+10, y-6);
        // cursor.setForegroundColour(255, 31, 31); cursor.setBackgroundColour(0, 0, 0);// cursor.setBold(true);

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
    public void setQuitCursor() {
        cursor.resetStyle();

        cursor.move(105, 20);

        cursor.setUnderline(true);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0); cursor.setUnderline(true);
        terminal.print(" ");

        cursor.move(105, 20);
        cursor.setVisible(true);
    }


    public void printHelp(int x, int y) {
        Command.printHelp(x, y, terminal);
        // cursor.resetStyle();

        // cursor.move(65, 40);
        // cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(191, 127, 127); cursor.setBold(true);
        // terminal.printFixed("help\n\n  H\n\n  ?\n\nquit\n\n  Q");

        // cursor.swapColours(); cursor.setBold(false);
        // cursor.move(74, 40);
        // terminal.print("--"); cursor.shift(5, 0); terminal.print("Display information about each command.");
        // cursor.move(74, 46);
        // terminal.print("--"); cursor.shift(5, 0); terminal.print("Return to the main menu.");

        // cursor.move(65, 41);
        // cursor.setForegroundColour(191, 127, 0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(false);
        // terminal.printFixed("│\n├─\n│\n└─\n\n\n│\n└─");
    }

    public void printError(int x, int y, String message) {
        cursor.resetStyle();
        // 98 30
        cursor.move(x, y);
        cursor.setForegroundColour(127, 0, 0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(true);
        terminal.print(message);
    }

    public void printWinMessage(String message) {
        cursor.resetStyle();

        cursor.move(100, 10);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(63, 191, 63); cursor.setBold(true); cursor.setBlinking(true);
        terminal.print("You WIN!");

        printEndMessage(message);
    }
    public void printLoseMessage(String message) {
        cursor.resetStyle();

        cursor.move(100, 10);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(223, 63, 63); cursor.setBold(true); cursor.setBlinking(true);
        terminal.print("You LOSE!");

        printEndMessage(message);
    }
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


    public void printSetup() {
        // cursor.resetStyle();

        // // print labels
        // cursor.setForegroundColour(191, 95, 95); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(true);

        // cursor.move(59, 20);
        // terminal.print("Player Name");
        // cursor.move(54, 22);
        // terminal.print("Number of Arrows");

        // cursor.move(58, 26);
        // terminal.print("Wumpus Lives");
        // cursor.move(42, 28);
        // terminal.print("Wumpus Must Be Killed?");

        // cursor.move(108, 20);
        // terminal.print("Width [5-20]");
        // cursor.move(107, 22);
        // terminal.print("Height [5-20]");

        // cursor.move(97, 26);
        // terminal.print("Wall Percentage [1-100]");

        // cursor.move(116, 30);
        // terminal.print("Pits");
        // cursor.move(116, 32);
        // terminal.print("Bats");
        // cursor.move(111, 34);
        // terminal.print("Artefacts");

        // // print verifications
        // cursor.swapColours();

        // cursor.move(65, 28);
        // terminal.print("[Y/N]");

        // cursor.move(114)
    }
    public void setSetupCursor() {
        cursor.resetStyle();

        cursor.setVisible(true);
    }


    public void clearScreen() {
        terminal.clearScreen(0, 0, 0);
    }


    public void dispose() {
        terminal.dispose();
        terminal = null;
    }
}