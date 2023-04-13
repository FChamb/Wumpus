package game;

import display.Terminal;

public class Command {

    public static final Command[] COMMANDS = new Command[] {
        new Command("help"     , ""         , "Display information about each command."   ,                   "h", "?"),
        new Command("quit"     , ""         , "Return to the main menu."                  ,                   "q"     ),
        new Command("move"     , "<N/S/E/W>", "Move the player in any cardinal direction.",             "go", "m", "g"),
        new Command("shoot"    , "<N/S/E/W>", "Shoot an arrow in any cardinal direction." , "fire"    ,       "s", "f")
    };

    public static final byte[]  command_fore = new byte[] {(byte)63 , (byte)63 , (byte)63 };
    public static final byte[] argument_fore = new byte[] {(byte)63 , (byte)95 , (byte)159};
    public static final byte[]  command_back = new byte[] {(byte)191, (byte)127, (byte)127};
    public static final byte[]     pipe_fore = new byte[] {(byte)191, (byte)127, (byte)0  };


    public String command, arguments, description, aliases[];


    public Command(String command, String arguments, String description, String... aliases) {
        this.command = command; this.arguments = arguments; this.description = description; this.aliases = aliases;
    }


    public static String[] parseCommand(String text) {
        String[] words = text.split(" ");
        if(words.length == 0) return null;
        
        boolean caught_alias;
        for(Command c : COMMANDS) {
            caught_alias = false;
            for(String alias : c.aliases) if(words[0].equals(alias)) { caught_alias = true; break; }

            if(caught_alias || words[0].equals(c.command)) {
                if(c.arguments.length() == 0) return new String[] {c.command};
                else {
                    String arguments = "";
                    for(int i = 1; i < words.length; i++) if(!words[i].equals("")) arguments += words[i] + (i == words.length-1 ? "" : " ");
                    return new String[] {c.command, arguments};
                }
            }
        }

        return null;
    }


    public static void printHelp(int x, int y, Terminal terminal) {
        Terminal.Cursor cursor = terminal.cursor;

        String text;
        int index, i, length, y_shift;

        cursor.resetStyle();

        // print commands
        cursor.move(x   , y  );
        cursor.setForegroundColour(command_fore); cursor.setBackgroundColour(command_back); cursor.setBold(true);

        text = "";
        index = 0;
        for(Command c : COMMANDS) {
            text += (index == 0 ? "" : "\n\n") + c.command;
            for(String alias : c.aliases) text += "\n\n  "+alias;

            index++;
        }
        terminal.printFixed(text);

        // print arguments
        cursor.setForegroundColour(argument_fore);

        text = "";
        y_shift = 0;
        for(Command c : COMMANDS) {
            if(c.arguments.length() != 0) {
                cursor.move(x + c.command.length(), y+y_shift);
                terminal.print(" "+c.arguments);
            }

            y_shift += 2*(c.aliases.length + 1);
        }

        // print "--" and description
        cursor.move(x+18 , y  );
        cursor.setForegroundColour(command_back); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(false);

        text = "";
        index = 0;
        for(Command c : COMMANDS) {
            text += "--     " + c.description;
            if(index != COMMANDS.length-1) {
                text += "\n\n";
                for(i = 0; i < c.aliases.length; i++) text += "\n\n";
            }

            index++;
        }
        terminal.printFixed(text);

        // print pipes
        cursor.move(x, y);
        cursor.setForegroundColour(pipe_fore); cursor.setBackgroundColour(0, 0, 0);

        text = "";
        for(Command c : COMMANDS) {
            length = c.aliases.length;
            for(i = 0; i < length; i++) {
                text += "\n│\n" + (i == length-1 ? "└─\n\n" : "├─");
            }
        }
        terminal.printFixed(text);

        // cursor.swapColours(); cursor.setBold(false);
        // cursor.move(x+9, y  );
        // terminal.print("--"); cursor.shift(5, 0); terminal.print("Display information about each command.");
        // cursor.move(x+9, y+6);
        // terminal.print("--"); cursor.shift(5, 0); terminal.print("Return to the main menu.");

        // cursor.move(x  , y+1);
        // cursor.setForegroundColour(191, 127, 0); cursor.setBackgroundColour(0, 0, 0); cursor.setBold(false);
        // terminal.printFixed("│\n├─\n│\n└─\n\n\n│\n└─");
    }
}