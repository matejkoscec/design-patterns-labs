#include "2c-virtual-tables.h"
#include <stdio.h>
#include <stdlib.h>


struct Unary_Function {
    int lower_bound;
    int upper_bound;
    const struct unary_function_fun_table *fun_table;
};

struct unary_function_fun_table {

    double (*value_at)(struct Unary_Function *, double x);

    double (*negative_value_at)(struct Unary_Function *, double x);
};

double negative_value_at(struct Unary_Function *this, double x) {
    return -(this->fun_table->value_at(this, x));
}

struct Square {
    struct Unary_Function base;
};

double square_value_at(struct Square *this, double x) {
    return x * x;
}

const struct unary_function_fun_table *square_vtable = &(const struct unary_function_fun_table) {
        (double (*)(struct Unary_Function *, double)) square_value_at,
        negative_value_at
};

void construct_square(struct Square *this, int lb, int ub) {
    this->base.lower_bound = lb;
    this->base.upper_bound = ub;
    this->base.fun_table = square_vtable;
}

struct Linear {
    struct Unary_Function base;
    double a;
    double b;
};

double linear_value_at(struct Linear *this, double x) {
    return this->a * x + this->b;
}

struct unary_function_fun_table linear_vtable = {
        (double (*)(struct Unary_Function *, double)) linear_value_at,
        negative_value_at
};

void construct_linear(struct Linear *this, int lb, int ub, double a_coef, double b_coef) {
    this->base.lower_bound = lb;
    this->base.upper_bound = ub;
    this->base.fun_table = &linear_vtable;
    this->a = a_coef;
    this->b = b_coef;
}

void tabulate(struct Unary_Function *this) {
    for (int x = this->lower_bound; x <= this->upper_bound; x++) {
        printf("f(%d)=%lf\n", x, this->fun_table->value_at(this, x));
    }
}

int same_functions_for_ints(struct Unary_Function *f1, struct Unary_Function *f2, double tolerance) {
    if (f1->lower_bound != f2->lower_bound) return 0;
    if (f1->upper_bound != f2->upper_bound) return 0;
    for (int x = f1->lower_bound; x <= f1->upper_bound; x++) {
        double delta = f1->fun_table->value_at(f1, x) - f2->fun_table->value_at(f2, x);
        if (delta < 0) delta = -delta;
        if (delta > tolerance) return 0;
    }
    return 1;
}

void task2c() {
    struct Square f1;
    construct_square(&f1, -2, 2);
    tabulate((struct Unary_Function *) &f1);
    struct Linear f2;
    construct_linear(&f2, -2, 2, 5, -2);
    tabulate((struct Unary_Function *) &f2);
    printf("f1==f2: %s\n",
           same_functions_for_ints((struct Unary_Function *) &f1, (struct Unary_Function *) &f2, 1E-6) ? "DA" : "NE");
    printf("neg_val f2(1) = %lf\n", f2.base.fun_table->negative_value_at(&f2.base, 1.0));
}
