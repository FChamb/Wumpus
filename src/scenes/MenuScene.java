package scenes;

import java.util.Scanner;

import display.Display;

public class MenuScene extends Scene {

    private boolean input_error;


    public MenuScene(Display display, Scanner in) {
        super(display, in);

        input_error = false;
    }


    @Override
    protected boolean execute(SceneManager manager) {
        display.clearScreen();
        display.printTitleScreen();
        if(input_error) display.printError(98, 30, "Input Error");
        display.setTitleCursor();

        input = in.nextLine().toLowerCase();
             if(input.equals("q")) manager.changeScene(SceneManager.QUIT);
        else if(input.equals("n")) manager.changeScene(SceneManager.GAME);
        else                       input_error = true;

        return false;
    }
}