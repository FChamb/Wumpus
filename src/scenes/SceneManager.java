package scenes;

import java.util.Scanner;

import display.Display;
import game.TextGame;

/**
 * Manages all of the scenes.
 */
public class SceneManager {

    public static final int MENU  = 0;
    public static final int QUIT  = 1;
    public static final int GAME  = 2;
    public static final int END   = 3;
    public static final int SETUP = 4;


    /**
     * The current scene.
     */
    private int scene;
    /**
     * The previous scene.
     */
    private int previous_scene;

    /**
     * The list of all {@linkplain Scene scenes}.
     */
    private Scene[] scenes;

    /**
     * The {@linkplain Display display}.
     */
    private Display display;
    /**
     * The input of the terminal.
     */
    private Scanner in;
    

    /**
     * The {@linkplain TextGame game}.
     */
    private TextGame game;


    public SceneManager(int scene) {
        changeScene(scene);

        display = new Display();
        in = new Scanner(System.in);

        game = new TextGame(false);

        scenes = new Scene[] {
            new  MenuScene(display, in, this      ),
            new  QuitScene(display, in, this      ),
            new  GameScene(display, in, this, game),
            new   EndScene(display, in, this      ),
            new SetupScene(display, in, this, game)
        };
    }


    /**
     * Go back to the previous scene.
     */
    public void revertScene() {
        scene = previous_scene;
    }
    /**
     * Set the currents scene.
     * 
     * @param scene the scene to change into
     */
    public void changeScene(int scene) {
        previous_scene = this.scene;
        this.scene = scene;
    }


    /**
     * Execute the current scene.
     */
    public boolean execute() {
        return scenes[scene].execute();
    }

    /**
     * Set the attribute of a specific scene.
     * 
     * @param scene the target scene
     * @param attr  the new attribute
     */
    public void setSceneAttribute(int scene, String attr) {
        scenes[scene].setAttribute(attr);
    }


    /**
     * Dispose the scene manager.
     */
    public void dispose() {
        display.dispose();
        display = null;
    }
}