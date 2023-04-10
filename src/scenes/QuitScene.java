package scenes;

import java.util.Scanner;

import display.Display;

public class QuitScene extends Scene {

    public QuitScene(Display display, Scanner in) {
        super(display, in);
    }


    @Override
    protected boolean execute(SceneManager manager) {
        display.clearScreen();
        display.printQuitScreen();
        display.setQuitCursor();

        input = in.nextLine().toLowerCase();
        if(input.equals("y"))
            return true;
        else
            manager.revertScene();
        return false;
    }
}