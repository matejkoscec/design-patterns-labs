#include "myfactory.h"

void *myfactory(const char *libname, const char *ctorarg) {
    return (void *) libname;
}
