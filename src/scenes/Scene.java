package scenes;

import java.util.Scanner;

import display.Display;

/**
 * Templates all other Scene classes.
 */
public abstract class Scene {

    /**
     * The {@link SceneManager} that manages this scene.
     */
    protected SceneManager manager;

    /**
     * The {@linkplain Display display}.
     */
    protected Display display;
    /**
     * The input of the terminal.
     */
    protected Scanner in;
    /**
     * The string that stores the next value of <code>in</code>.
     */
    protected String input;

    /**
     * Allows different scenes to interact with this scene.
     */
    protected String global_attribute;


    public Scene(Display display, Scanner in, SceneManager manager) {
        this.manager = manager;
        this.display = display;
        this.in = in;
    }


    /**
     * Executes the scene.
     * 
     * @return a boolean. if true, the whole program will terminate
     */
    protected abstract boolean execute();

    /**
     * Sets the value of <code>global_attribute</code>.
     * 
     * @param attr the string that <code>global_attribute</code> will become
     */
    public void setAttribute(String attr) {
        global_attribute = attr;
    }
}