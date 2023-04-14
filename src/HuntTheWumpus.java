import scenes.SceneManager;

/**
 * Executes the program with the GUI.
 */
public class HuntTheWumpus {

    /**
     * The {@linkplain SceneManager scene manager}.
     */
    public static SceneManager scenes;

    /**
     * Decides whether the program should run or not.
     */
    public static boolean running;


    public static void main(String[] args) {
        scenes = new SceneManager(0);

        mainloop();
    }


    /**
     * The main loop of the program.
     */
    public static void mainloop() {
        running = true;
        while(running) {
            if(scenes.execute()) break;
        }

        scenes.dispose();
    }
}