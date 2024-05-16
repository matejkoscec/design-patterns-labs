package design.patterns.task1_4;

public class FactoryPattern {

    public static void run() {
        var animal = AnimalFactory.newInstance("parrot", "perry");
        animal.animalPrintGreeting();
        animal.animalPrintMenu();
    }
}
