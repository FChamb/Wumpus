package scenes;

import java.util.Scanner;

import display.Display;
import game.TextGame;

public class SceneManager {

    public static final int MENU  = 0;
    public static final int QUIT  = 1;
    public static final int GAME  = 2;
    public static final int END   = 3;
    public static final int SETUP = 4;


    private int scene;
    private int previous_scene;

    private Scene[] scenes;

    private Display display;
    private Scanner in;
    private String input;

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


    public void revertScene() {
        scene = previous_scene;
    }
    public void changeScene(int scene) {
        previous_scene = this.scene;
        this.scene = scene;
    }


    public boolean execute() {
        return scenes[scene].execute();
    }

    public void setSceneAttribute(int scene, String attr) {
        scenes[scene].setAttribute(attr);
    }


    public void dispose() {
        display.dispose();
        display = null;
    }
}