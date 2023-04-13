package scenes;

import java.util.Scanner;

import display.Display;
import game.Player;
import game.TextGame;

public class SetupScene extends Scene {

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