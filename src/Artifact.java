public class Artifact {
    private final String name;
    private final String ability;

    public Artifact(String name, String ability) {
        this.name = name;
        this.ability = ability;
    }

    public String getName() {
        return this.name;
    }

    public String getAbility() {
        return this.ability;
    }

    public String toString() {
        return this.name.substring(0, 1);
    }
}
