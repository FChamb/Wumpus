import scenes.SceneManager;

public class HuntTheWumpus {

    public static SceneManager scenes;

    public static boolean running;


    public static void main(String[] args) {
        scenes = new SceneManager(0);

        mainloop();
    }


    public static void mainloop() {
        running = true;
        while(running) {
            if(scenes.execute()) break;
        }

        scenes.dispose();
    }
}