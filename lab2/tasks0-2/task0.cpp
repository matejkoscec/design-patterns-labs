#include <iostream>
#include <vector>
#include <list>
#include "task0.h"

struct Point {
    int x;
    int y;
};

struct Shape {
private:
    Point center_;
public :
    virtual void draw() = 0;

    void move(int x, int y) {
        center_.x += x;
        center_.y += y;
    }
};

struct Circle : public Shape {
private:
    double radius_;
public:
    void draw() override {
        std::cout << "in drawCircle\n";
    }
};

class Square : public Shape {
private:
    double side_;
public:
    void draw() override {
        std::cout << "in drawSquare\n";
    }
};

class Rhomb : public Shape {
private:
    double e_;
    double f_;
public:
    void draw() override {
        std::cout << "in drawRhomb\n";
    }
};

void drawShapes(const std::vector<Shape *> &shapes) {
    for (auto & shape : shapes) {
        shape->draw();
    }
}

void moveShapes(const std::vector<Shape *> &shapes, int x, int y) {
    for (auto & shape : shapes) {
        shape->move(x, y);
    }
}

void task0() {
    std::vector<Shape *> shapes;
    shapes.push_back(new Circle());
    shapes.push_back(new Square());
    shapes.push_back(new Square());
    shapes.push_back(new Circle());
    shapes.push_back(new Rhomb());

    drawShapes(shapes);
    moveShapes(shapes, 10, 10);
}
