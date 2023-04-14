package game;

public class Artifact {

    private String name;
    private String ability;
    private static Artifact[] artifacts = new Artifact[] {
            new Artifact("D", "Shield: An extra one use layer of protection."),
            new Artifact("U", "Mysterious Water: Upon drinking, you get a cold and lose senses for the next 5 moves!"),
            new Artifact(">", "Arrow: I wonder who left this behind?"),
            new Artifact("~",
                    "Dusty Room: Your eyes get covered in dust, making you stuggle to see for the next 5 moves!") };

    /**
     * Constructor to create an artefact using its type and ability
     * 
     * @param name    - the type of the artefact
     * @param ability - the ability associated with the artefact
     */
    public Artifact(String name, String ability) {
        this.name = name;
        this.ability = ability;
    }

    /**
     * Constructor to create an artefact from its type
     * 
     * @param type - the type of artefact being created
     */
    public Artifact(String type) {
        for (Artifact artifact : artifacts) {
            if (artifact.toString().equals(type.substring(0, 1))) {
                this.name = artifact.getName();
                this.ability = artifact.getAbility();
                return;
            }
        }
    }

    /**
     * Gets a random artefact type
     * 
     * @return a random artefact type
     */
    public static Artifact getRandom() {
        double choice = Math.random() * artifacts.length;
        return artifacts[(int) choice];
    }

    /**
     * Gets the name of the artefact
     * 
     * @return the name of the artefact
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the ability of the artefact
     * 
     * @return the ability of the artefact
     */
    public String getAbility() {
        return this.ability;
    }

    /**
     * Turns the artefact into a string of its name
     * 
     * @return the artefacts name as a string
     */
    public String toString() {
        return " " + this.name + " ";
    }
}
