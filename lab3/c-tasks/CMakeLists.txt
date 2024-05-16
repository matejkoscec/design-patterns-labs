cmake_minimum_required(VERSION 3.28)
project(c_tasks)

set(CMAKE_CXX_STANDARD 17)

add_executable(c_tasks main.cpp
        task1.1.c
        task1.1.h
        task1_3.cpp
        task1_3.h
        myfactory.c
        myfactory.h)
