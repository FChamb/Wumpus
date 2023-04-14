package scenes;

import java.util.Scanner;

import display.Display;

/**
 * The scene responsible for the end screen.
 */
public class EndScene extends Scene {

    public EndScene(Display display, Scanner in, SceneManager manager) {
        super(display, in, manager);
    }


    @Override
    protected boolean execute() {
        display.clearScreen();
        if(global_attribute.charAt(0) == '!') display. printWinMessage(global_attribute.substring(1));
        else                                  display.printLoseMessage(global_attribute.substring(1));

        in.nextLine();

        return true;
    }
}