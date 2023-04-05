package display;

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
        
        cursor.setUnderline(true);
        terminal.print(" ");
        
        cursor.move(103, 24);
        cursor.setVisible(true);
    }

    public void inputError() {
        cursor.resetStyle();

        cursor.move(98, 30);
        cursor.setForegroundColour(127, 0, 0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(true);
        terminal.print("Input Error");

        printTitleScreen();
    }


    public void printGameScreen() {
        cursor.resetStyle();

        cursor.move(58, 30);
        cursor.setForegroundColour(191, 127,   0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(true);
        terminal.print("Enter Command ");
        
        cursor.setForegroundColour(191, 127, 127); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(false);
        terminal.print("[  or   for help]");

        cursor.swapColours(); cursor.setBold(true);
        cursor.shift(-16, 0); terminal.print("H");
        cursor.shift(  4, 0); terminal.print("?");

        cursor.shift(12, 0);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0);
        terminal.print(": ");

        cursor.setUnderline(true);
        terminal.print("          ");

        cursor.move(93, 30);
        cursor.setVisible(true);
    }

    public void printHelp() {
        cursor.resetStyle();

        cursor.move(65, 40);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(191, 127, 127); cursor.setBold(true);
        terminal.printFixed("help\n\n  H\n\n  ?\n\nquit\n\n  Q");

        cursor.swapColours(); cursor.setBold(false);
        cursor.move(74, 40);
        terminal.print("--"); cursor.shift(5, 0); terminal.print("Display information about each command.");
        cursor.move(74, 46);
        terminal.print("--"); cursor.shift(5, 0); terminal.print("Return to the main menu.");

        cursor.move(65, 41);
        cursor.setForegroundColour(191, 127, 0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(false);
        terminal.printFixed("│\n├─\n│\n└─\n\n\n│\n└─");
    }


    public void printQuitScreen() {
        cursor.resetStyle();

        cursor.move(82, 14);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0);
        terminal.print("Are you sure you want to quit the game?");

        cursor.move(97, 20);
        cursor.setBold(true);
        terminal.print("[ / ] : ");

        cursor.setBold(false); cursor.setUnderline(true);
        terminal.print(" ");

        cursor.shift(-8, 0);
        cursor.setForegroundColour(0, 0, 0); cursor.setBackgroundColour(0, 191, 63); cursor.setBold(false); cursor.setUnderline(false);
        terminal.print("Y");

        cursor.shift(1, 0);
        cursor.setBackgroundColour(191, 0, 0);
        terminal.print("N");

        cursor.move(105, 20);
        cursor.setForegroundColour(255, 255, 255); cursor.setBackgroundColour(0, 0, 0); cursor.setUnderline(true);
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