package design.patterns.task1_4.plugins;

import design.patterns.task1_4.Animal;

public class Tiger extends Animal {

    private final String animalName;

    public Tiger(String animalName) {
        this.animalName = animalName;
    }

    @Override
    public String name() {
        return animalName;
    }

    @Override
    public String greet() {
        return "Mijau";
    }

    @Override
    public String menu() {
        return "mlako mlijeko";
    }
}
