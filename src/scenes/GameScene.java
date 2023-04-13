package scenes;

import java.util.List;
import java.util.Scanner;

import display.Display;
import game.Cave;
import game.Command;
import game.TextGame;

/**
 * The scene responsible for the game screen.
 */
public class GameScene extends Scene {

    /**
     * The position of the input line.
     */
    private static final int[] input_position = new int[] {20 , 5 };
    /**
     * The position of the help display.
     */
    private static final int[]  help_position = new int[] {10 , 15};
    private static final int[] board_position = new int[] {140, 25};


    private boolean show_help, input_error;
    private String status_message, percept_message;

    private String[] command;

    private TextGame game;


    public GameScene(Display display, Scanner in, SceneManager manager, TextGame game) {
        super(display, in, manager);

        show_help = false; input_error = false;
        status_message = ""; percept_message = "";

        this.game = game;
    }


    @Override
    protected boolean execute() {
        display.clearScreen();

        display.printGameInput(input_position[0], input_position[1]);
        if(  show_help) display.printHelp(help_position[0], help_position[1]);
        // else            
        if(input_error) ;//display.printError(98, 35, "Input Error");

        game.setGameScene(this);
        game.stepGame();
        
        return false;
    }


    public void printBoard(Cave cave, List<List<Boolean>> player_tracks) {
        display.printGameBoard(false, board_position[0], board_position[1], cave, player_tracks, percept_message, game.isBlind());
        if(!show_help) display.printGameStatus(input_position[0] + 24, input_position[1] + 2, status_message);
        display.setGameCursor(input_position[0] + 36, input_position[1]);
        percept_message = ""; status_message = "";
    }
    public void printEnd(String message) {
        manager.changeScene(SceneManager.END);
        manager.setSceneAttribute(SceneManager.END, message);
    }
    public void setStatusMessage(String message) {
        status_message += message + "\n";
    }
    public void setPerceptMessage(String message) {
        // System.out.println(message); in.nextLine();
        percept_message += message + "\n";
    }


    public void getMove() {

        // process input
        input = in.nextLine().toLowerCase();
        command = Command.parseCommand(input);
        if(command == null) { input_error = true; show_help = false; }

        else if(command[0].equals("help")) { show_help = true; input_error = false; }
        else if(command[0].equals("quit"))   manager.changeScene(SceneManager.QUIT);
        else if(command[0].equals("move")) {
            if(command[1].equals("n") || command[1].equals("e") || command[1].equals("s") || command[1].equals("w"))
                game.getMove("m", command[1]);
        }
        else if(command[0].equals("shoot")) {
            if(command[1].equals("n") || command[1].equals("e") || command[1].equals("s") || command[1].equals("w"))
                game.getMove("s", command[1]);
        }
    }


    // private void processInput() {
    //          if(input.equals("h") || input.equals("?") || input.equals("help")) { show_help = true; input_error = false; }
    //     else if(                     input.equals("q") || input.equals("quit")) manager.changeScene(SceneManager.QUIT);
    //     else if(                     input.equals("m") || input.equals("move")) ;
    //     else                                                                    { input_error = true; show_help = false; }

    // }
}