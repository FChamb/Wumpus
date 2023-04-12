package scenes;

import java.util.Scanner;

import display.Display;

public class QuitScene extends Scene {

    public QuitScene(Display display, Scanner in, SceneManager manager) {
        super(display, in, manager);
    }


    @Override
    protected boolean execute() {
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