cmake_minimum_required(VERSION 3.27)
project(tasks0_2)

set(CMAKE_CXX_STANDARD 17)

add_executable(tasks0_2 main.cpp
        task1.c
        task1.h
        task2.cpp
        task2.h
        task0.cpp
        task0.h
        foo.c
        foo.h)
