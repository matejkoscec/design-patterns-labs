#include <stdio.h>
#include <malloc.h>
#include "1-dynamic-polymorphism.h"


typedef char const *(*PTRFUN)();

struct Animal {
    const char *name;
    const struct animal_fun_table_ *funTable;
};

struct animal_fun_table_ {
    PTRFUN greet;
    PTRFUN menu;
};

char const *dogGreet(void) {
    return "vau!";
}

char const *dogMenu(void) {
    return "kuhanu govedinu";
}

char const *catGreet(void) {
    return "mijau!";
}

char const *catMenu(void) {
    return "konzerviranu tunjevinu";
}

const struct animal_fun_table_ *dogFns = &(const struct animal_fun_table_) {dogGreet, dogMenu};

const struct animal_fun_table_ *catFns = &(const struct animal_fun_table_) {catGreet, catMenu};

static void animalPrintGreeting(struct Animal *animal) {
    printf("%s pozdravlja: %s\n", animal->name, animal->funTable->greet());
}

static void animalPrintMenu(struct Animal *animal) {
    printf("%s voli %s\n", animal->name, animal->funTable->menu());
}

void constructDog(struct Animal *dog, const char *name) {
    dog->name = name;
    dog->funTable = dogFns;
}

void constructCat(struct Animal *cat, const char *name) {
    cat->name = name;
    cat->funTable = &(const struct animal_fun_table_) {catGreet, catMenu};
}

static struct Animal *createDog(const char *name) {
    struct Animal *dog = malloc(sizeof(struct Animal));
    constructDog(dog, name);
    return dog;
}

static struct Animal *createCat(const char *name) {
    struct Animal *cat = malloc(sizeof(struct Animal));
    constructCat(cat, name);
    return cat;
}

void testAnimals(void) {
    struct Animal *p1 = createDog("Hamlet");
    struct Animal *p2 = createCat("Ofelija");
    struct Animal *p3 = createDog("Polonije");

    animalPrintGreeting(p1);
    animalPrintGreeting(p2);
    animalPrintGreeting(p3);

    animalPrintMenu(p1);
    animalPrintMenu(p2);
    animalPrintMenu(p3);

    free(p1);
    free(p2);
    free(p3);
}

static struct Animal *createNDogs(int n, const char *name) {
    struct Animal *dogs = malloc(n * sizeof(struct Animal));
    const int maxNameLen = 100;
    for (int i = 0; i < n; i++) {
        char *dogName = (char *) malloc(maxNameLen * sizeof(char));
        sprintf(dogName, "%s %d", name, i);
        constructDog(&dogs[i], dogName);
    }

    return dogs;
}

void task1() {
    testAnimals();

    printf("\n");

    printf("Stack allocated:\n");
    struct Animal dog;
    constructDog(&dog, "Rex");
    animalPrintGreeting(&dog);

    printf("Heap allocated:\n");
    struct Animal *cat = malloc(sizeof(struct Animal));
    constructCat(cat, "Tom");
    animalPrintGreeting(cat);
    free(cat);

    printf("\n");

    const int n = 3;
    printf("%d dogs:\n", n);
    struct Animal *dogs = createNDogs(n, "Rex");
    for (int i = 0; i < n; i++) {
        animalPrintGreeting(&dogs[i]);
    }
    free(dogs);
}
