#include <iostream>
#include "5-manual-vtable-call.h"

class B {
public:
    virtual int __cdecl prva() = 0;

    virtual int __cdecl druga(int) = 0;
};

class D : public B {
public:
    virtual int __cdecl prva() { return 42; }

    virtual int __cdecl druga(int x) { return prva() + x; }
};


typedef int (*FirstFunc)(void *);

typedef int (*SecondFunc)(void *, int);

void manualVTableCall(B *pb) {
    void **vtable = *(void ***) pb;

    auto prva = (FirstFunc) vtable[0];
    auto druga = (SecondFunc) vtable[1];

    std::cout << "Prva: " << prva(pb) << std::endl;
    std::cout << "Druga: " << druga(pb, 5) << std::endl;
}

void task5() {
    D d;
    manualVTableCall(&d);
}
