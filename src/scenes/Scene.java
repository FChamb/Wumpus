package scenes;

import java.util.Scanner;

import display.Display;

public abstract class Scene {

    protected SceneManager manager;

    protected Display display;
    protected Scanner in;
    protected String input;

    protected String global_attribute;


    public Scene(Display display, Scanner in, SceneManager manager) {
        this.manager = manager;
        this.display = display;
        this.in = in;
    }


    protected abstract boolean execute();

    public void setAttribute(String attr) {
        global_attribute = attr;
    }
}