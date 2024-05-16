#include <iostream>
#include "3-memory-price.h"

class CoolClass {
public:
    virtual void set(int x) { x_ = x; };

    virtual int get() { return x_; };
private:
    int x_;
};

class PlainOldClass {
public:
    void set(int x) { ; };

    int get() { return 0; };
//private:
//    int x_;
};

void task3() {
    std::cout << "Size of CoolClass: " << sizeof(CoolClass) << std::endl;
    std::cout << "Size of PlainOldClass: " << sizeof(PlainOldClass) << std::endl;
}
