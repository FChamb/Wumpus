public class Artifact {
    private String name;
    private String ability;

    // Altered artefact symbols because the original ones dont work:
    // D - shield
    // U - mysterious water
    // > - arrow
    // ~ - dusty room

    private static Artifact[] artifacts = new Artifact[]{new Artifact("D", "Shield: An extra one use layer of protection."),
            new Artifact("U", "Mysterious Water: Upon drinking, you get a cold and lose senses for the next 5 moves!"),
            new Artifact(">", "Arrow: I wonder who left this behind?"),
            new Artifact("~", "Dusty Room: Your eyes get covered in dust, making you stop path finding for the next 5 moves!")};

    public Artifact(String name, String ability) {
        this.name = name;
        this.ability = ability;
    }

    public Artifact(String type) {
        for (Artifact artifact : this.artifacts) {
            if (artifact.toString().equals(type.substring(0, 1))) {
                this.name = artifact.getName();
                this.ability = artifact.getAbility();
                return;
            }
        }
    }

    public static Artifact getRandom() {
        double choice = Math.random() * artifacts.length;
        return artifacts[(int) choice];
    }

    public String getName() {
        return this.name;
    }

    public String getAbility() {
        return this.ability;
    }

    public String toString() {
        return " " +  this.name + " ";
    }
}
