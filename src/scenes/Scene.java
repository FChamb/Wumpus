package scenes;

import java.util.Scanner;

import display.Display;

public abstract class Scene {

    protected SceneManager manager;

    protected Display display;
    protected Scanner in;
    protected String input;


    public Scene(Display display, Scanner in, SceneManager manager) {
        this.manager = manager;
        this.display = display;
        this.in = in;
    }


    protected abstract boolean execute();
}