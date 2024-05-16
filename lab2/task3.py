def mymax(iterable, key=lambda x: x):
    max_key = None
    max_x = None
    for x in iterable:
        k = key(x)
        if max_key is None or k > max_key:
            max_x, max_key = x, k

    return max_x

words = ["Gle", "malu", "vocku", "poslije", "kise", "Puna", "je", "kapi", "pa", "ih", "njise"]
longest_word = mymax(words, key=len)
print(f"Longest word: {longest_word}")

numbers = [1, 3, 5, 7, 4, 6, 9, 2, 0]
max_int = mymax(numbers)
print(f"Max int: {max_int}")

chars = "Suncana strana ulice"
max_char = mymax(chars)
print(f"Max char: {max_char}")

products = {'burek': 8, 'buhtla': 5}
most_expensive_product = mymax(products, key=products.get)
print(f"Most expensive product: {most_expensive_product}")

people = [("John", "Doe"), ("Jane", "Doe"), ("Alice", "Wonderland"), ("Bob", "Marley")]
last_person = mymax(people)
print(f"Last person: {last_person}")
