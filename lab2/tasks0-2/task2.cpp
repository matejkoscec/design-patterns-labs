#include <iostream>
#include <vector>
#include <set>
#include "task2.h"

template<typename Iterator, typename Predicate>
Iterator mymax(Iterator first, Iterator last, Predicate pred) {
    if (first == last) {
        return last;
    }

    Iterator max_element = first;
    for (Iterator it = first; it != last; it++) {
        if (pred(*it, *max_element)) {
            max_element = it;
        }
    }

    return max_element;
}

bool gt_int(int a, int b) {
    return a > b;
}

bool gt_char(char a, char b) {
    return a > b;
}

bool gt_str(const std::string &a, const std::string &b) {
    return a > b;
}

void task2() {
    int arr_int[] = {1, 3, 5, 7, 4, 6, 9, 2, 0};
    std::string arr_str[] = {"Gle", "malu", "vocku", "poslije", "kise", "Puna", "je", "kapi", "pa", "ih", "njise"};
    std::vector<int> vec_int = {1, 3, 5, 7, 4, 6, 9, 2, 0};
    std::set<std::string> set_str = {"Gle", "malu", "vocku", "poslije", "kise", "Puna", "je", "kapi", "pa", "ih",
                                     "njise"};

    const int *max_int = mymax(std::begin(arr_int), std::end(arr_int), gt_int);
    const std::string *max_str = mymax(std::begin(arr_str), std::end(arr_str), gt_str);
    auto max_vec_int = mymax(vec_int.begin(), vec_int.end(), gt_int);
    auto max_set_str = mymax(set_str.begin(), set_str.end(), gt_str);

    std::cout << "Max int: " << *max_int << "\n";
    std::cout << "Max string: " << *max_str << "\n";
    std::cout << "Max int in vector: " << *max_vec_int << "\n";
    std::cout << "Max string in set: " << *max_set_str << "\n";
}