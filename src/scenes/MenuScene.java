package scenes;

import java.util.Scanner;

import display.Display;

public class MenuScene extends Scene {

    private boolean input_error;


    public MenuScene(Display display, Scanner in, SceneManager manager) {
        super(display, in, manager);

        input_error = false;
    }


    @Override
    protected boolean execute() {
        display.clearScreen();
        display.printTitleScreen();
        if(input_error) display.printError(98, 30, "Input Error");
        display.setTitleCursor();

        input = in.nextLine().toLowerCase();
             if(input.equals("q")) manager.changeScene(SceneManager.QUIT );
        else if(input.equals("n")) manager.changeScene(SceneManager.SETUP);
        else                       input_error = true;

        return false;
    }
}