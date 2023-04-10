package scenes;

import java.util.Scanner;

import display.Display;
import game.Command;

public class GameScene extends Scene {

    private static final int[] input_position = new int[] {20, 5 };
    private static final int[]  help_position = new int[] {10, 15};


    private boolean show_help, input_error;

    private String[] command;


    public GameScene(Display display, Scanner in) {
        super(display, in);

        show_help = false; input_error = false;
    }


    @Override
    protected boolean execute(SceneManager manager) {
        display.clearScreen();
        display.printGameInput(input_position[0], input_position[1]);
        if(  show_help) display.printHelp(help_position[0], help_position[1]);
        if(input_error) display.printError(98, 35, "Input Error");
        display.setGameCursor(input_position[0] + 35, input_position[1]);

        // process input
        input = in.nextLine().toLowerCase();
        command = Command.parseCommand(input);
        if(command == null) { input_error = true; show_help = false; }

        else if(command[0].equals("help")) { show_help = true; input_error = false; }
        else if(command[0].equals("quit"))   manager.changeScene(SceneManager.QUIT);
        else if(command[0].equals("move")) ;
        
        return false;
    }


    // private void processInput() {
    //          if(input.equals("h") || input.equals("?") || input.equals("help")) { show_help = true; input_error = false; }
    //     else if(                     input.equals("q") || input.equals("quit")) manager.changeScene(SceneManager.QUIT);
    //     else if(                     input.equals("m") || input.equals("move")) ;
    //     else                                                                    { input_error = true; show_help = false; }

    // }
}