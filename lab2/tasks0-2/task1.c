#include <stdio.h>
#include "task1.h"

#include <string.h>

const void *mymax(const void *base, size_t nmemb, size_t size, int (*compar)(const void *, const void *)) {
    const char *max_element = base;
    const char *element;
    for (size_t i = 1; i < nmemb; i++) {
        element = (const char *) base + i * size;
        if (compar(element, max_element) > 0) {
            max_element = element;
        }
    }

    return max_element;
}

int gt_int(const void *a, const void *b) {
    return (*(int *) a > *(int *) b);
}

int gt_char(const void *a, const void *b) {
    return (*(char *) a > *(char *) b);
}

int gt_str(const void *a, const void *b) {
    return (strcmp(*(const char **) a, *(const char **) b) > 0);
}

void task1() {
    int arr_int[] = {1, 3, 5, 7, 4, 6, 9, 2, 0};
    char arr_char[] = "Suncana strana ulice";
    const char *arr_str[] = {
            "Gle", "malu", "vocku", "poslije", "kise",
            "Puna", "je", "kapi", "pa", "ih", "njise"
    };

    int *max_int = (int *) mymax(arr_int, sizeof(arr_int) / sizeof(int), sizeof(int), gt_int);
    char *max_char = (char *) mymax(arr_char, strlen(arr_char), sizeof(char), gt_char);
    const char **max_str = (const char **) mymax(arr_str, sizeof(arr_str) / sizeof(char *), sizeof(char *), gt_str);

    printf("Max int: %d\n", *max_int);
    printf("Max char: %c\n", *max_char);
    printf("Max string: %s\n", *max_str);
}
