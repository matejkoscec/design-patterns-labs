#include <math.h>
#include "foo.h"

void test() {

}

struct X {
    const struct x_vtable *vtable;
};

struct x_vtable {
    double (*action)(double);
};

double x1_action(double x) {
    return x * x;
}

double x2_action(double x) {
    return fabs(x);
}

void constructX1(struct X *x1) {
    x1->vtable = &(const struct x_vtable) {x1_action};
}

void constructX2(struct X *x2) {
    x2->vtable = &(const struct x_vtable) {x1_action};
}

struct C {
    struct X *px;
};
