#include "myfactory.h"
#include <dlfcn.h>
#include <stdio.h>

void* myfactory(char const* libname, char const* ctorarg) {
    char filename[1024];
    sprintf(filename, "./%s.so", libname);

    void* handle = dlopen(filename, RTLD_LAZY);
    if (!handle) {
        fprintf(stderr, "%s\n", dlerror());
        return NULL;
    }

    dlerror();

    void* (*create)(char const*) = dlsym(handle, "create");
    char* error = dlerror();
    if (error) {
        fprintf(stderr, "%s\n", error);
        return NULL;
    }

    return create(ctorarg);
}