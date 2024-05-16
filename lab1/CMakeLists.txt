cmake_minimum_required(VERSION 3.27)
project(lab1)

set(CMAKE_CXX_STANDARD 17)

add_executable(lab1 main.cpp
        1-dynamic-polymorphism.c
        1-dynamic-polymorphism.h
        2-virtual-tables.cpp
        2-virtual-tables.h
        2c-virtual-tables.c
        2c-virtual-tables.h
        3-memory-price.cpp
        3-memory-price.h
        4-time-price.cpp
        4-time-price.h
        5-manual-vtable-call.cpp
        5-manual-vtable-call.h
        6-polimorphism-while-constructing.cpp
        6-polimorphism-while-constructing.h)
