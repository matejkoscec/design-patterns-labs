#include <stdlib.h>
#include <string.h>

typedef char const* (*PTRFUN)();

struct Animal {
    PTRFUN* vtable;
    char* name;
};

char const* parrot_name(void* this) {
    return ((struct Animal*)this)->name;
}

char const* parrot_greet() {
    return "squawk!";
}

char const* parrot_menu() {
    return "seeds";
}

PTRFUN parrot_vtable[3] = {
        (PTRFUN)parrot_name,
        (PTRFUN)parrot_greet,
        (PTRFUN)parrot_menu
};

void* create(char const* name) {
    struct Animal* parrot = (struct Animal*)malloc(sizeof(struct Animal));
    parrot->vtable = parrot_vtable;
    parrot->name = (char*)malloc((strlen(name) + 1) * sizeof(char));
    strcpy(parrot->name, name);
    return parrot;
}
