package design.patterns.task1_4;

public abstract class Animal {

    public abstract String name();

    public abstract String greet();

    public abstract String menu();

    public void animalPrintGreeting() {
        System.out.println(name() + " pozdravlja: " + greet() + "!");
    }

    public void animalPrintMenu() {
        System.out.println(name() + " voli " + menu() + ".");
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name() + '\'' +
                "greet='" + greet() + '\'' +
                "menu='" + menu() + '\'' +
                '}';
    }
}
