package scenes;

import java.util.Scanner;

import display.Display;

public class MenuScene extends Scene {

    public MenuScene(Display display, Scanner in) {
        super(display, in);
    }


    @Override
    protected boolean execute(SceneManager manager) {
        display.printTitleScreen();
        input = in.nextLine().toLowerCase();

        display.clearScreen();
        if(input.equals("q")) {
            manager.changeScene(SceneManager.QUIT);
        } else {
            display.inputError();
        }

        return false;
    }
}