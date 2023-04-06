package scenes;

import java.util.Scanner;

import display.Display;

public class QuitScene extends Scene {

    public QuitScene(Display display, Scanner in) {
        super(display, in);
    }


    @Override
    protected boolean execute(SceneManager manager) {
        display.printQuitScreen();
        input = in.nextLine().toLowerCase();

        display.clearScreen();
        if(input.equals("y"))
            return true;
        else
            manager.revertScene();
        return false;
    }
}