#include <stdlib.h>
#include <string.h>

typedef char const* (*PTRFUN)();

struct Animal {
    PTRFUN* vtable;
    char* name;
};

char const* tiger_name(void* this) {
    return ((struct Animal*)this)->name;
}

char const* tiger_greet() {
    return "meow!";
}

char const* tiger_menu() {
    return "meat";
}

PTRFUN tiger_vtable[3] = {
        (PTRFUN)tiger_name,
        (PTRFUN)tiger_greet,
        (PTRFUN)tiger_menu
};

void* create(char const* name) {
    struct Animal* tiger = (struct Animal*)malloc(sizeof(struct Animal));
    tiger->vtable = tiger_vtable;
    tiger->name = (char*)malloc((strlen(name) + 1) * sizeof(char));
    strcpy(tiger->name, name);
    return tiger;
}
