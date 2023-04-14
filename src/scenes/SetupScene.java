package scenes;

import java.util.Scanner;

import display.Display;
import game.Player;
import game.TextGame;

/**
 * The scene responsible with setting up the game.
 */
public class SetupScene extends Scene {

    /**
     * The {@linkplain TextGame game}.
     */
    private TextGame game;


    public SetupScene(Display display, Scanner in, SceneManager manager, TextGame game) {
        super(display, in, manager);

        this.game = game;
    }


    @Override
    protected boolean execute() {
        display.clearScreen();
        display.setSetupCursor();

        game.setUp(new Player("Player"));
        game.setUpBoard();

        manager.changeScene(SceneManager.GAME);

        return false;
    }
}