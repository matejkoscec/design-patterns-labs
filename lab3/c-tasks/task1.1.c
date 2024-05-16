#include "myfactory.h"

#include <stdio.h>
#include <stdlib.h>
#include "task1.1.h"

typedef char const *(*PTRFUN)();

struct Animal {
    PTRFUN *vtable;
    const struct vtable *entries;
};

struct vtable {
    const char *(*name)(void* this);

    const char *(*greet)();

    const char *(*menu)();
};

static void animalPrintGreeting(struct Animal *animal) {
    printf("%s pozdravlja: %s\n", animal->vtable[0](animal), animal->vtable[1]());
}

static void animalPrintMenu(struct Animal *animal) {
    printf("%s voli %s\n", animal->vtable[0](animal), animal->vtable[2]());
}

void task1_1(int argc, char *argv[]) {
    for (int i = 0; i < argc / 2; ++i) {
        struct Animal *p = (struct Animal *) myfactory(argv[1 + 2 * i], argv[1 + 2 * i + 1]);
        if (!p) {
            printf("Creation of plug-in object %s failed.\n", argv[1 + 2 * i]);
            continue;
        }

        animalPrintGreeting(p);
        animalPrintMenu(p);
        free(p);
    }
}
