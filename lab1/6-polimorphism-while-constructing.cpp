#include "6-polimorphism-while-constructing.h"
#include <stdio.h>
#include <iostream>

class Base {
public:
    Base() {
        metoda();
    }

    virtual void virtualnaMetoda() {
        printf("ja sam bazna implementacija!\n");
    }

    void metoda() {
        printf("Metoda kaze: ");
        virtualnaMetoda();
    }
};

class Derived : public Base {
public:
    Derived() : Base() {
        metoda();
    }

    virtual void virtualnaMetoda() {
        printf("ja sam izvedena implementacija!\n");
    }
};

void task6() {
    auto *pd = new Derived();
    pd->metoda();
    delete pd;
    std::cout << sizeof(Derived) << std::endl;
}
