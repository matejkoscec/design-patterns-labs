package design.patterns.task1_4;

public class AnimalFactory {

    public static Animal newInstance(String animalKind, String name) {
        String className = animalKind.substring(0, 1).toUpperCase() + animalKind.toLowerCase().substring(1);
        try {
            var clazz = Class.forName("design.patterns.task1_4.plugins." + className);
            var constructor = clazz.getConstructor(String.class);
            return (Animal) constructor.newInstance(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
