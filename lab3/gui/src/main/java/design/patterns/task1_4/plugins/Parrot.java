package design.patterns.task1_4.plugins;

import design.patterns.task1_4.Animal;

public class Parrot extends Animal {

    private final String animalName;

    public Parrot(String animalName) {
        this.animalName = animalName;
    }

    @Override
    public String name() {
        return animalName;
    }

    @Override
    public String greet() {
        return "Sto mu gromova";
    }

    @Override
    public String menu() {
        return "brazilske orahe";
    }
}
