import os
from importlib import import_module


def printGreeting(pet):
    print(f"{pet.name()} pozdravlja: {pet.greet()}!")


def printMenu(pet):
    print(f"{pet.name()} voli {pet.menu()}.")


def myfactory(moduleName: str):
    try:
        module = import_module(f"plugins.{moduleName}")
        return getattr(module, moduleName.capitalize())
    except Exception:
        return None


def test():
    pets = []

    for mymodule in os.listdir("plugins"):
        moduleName, moduleExt = os.path.splitext(mymodule)
        if moduleExt == ".py":
            ljubimac = myfactory(moduleName)(f"Ljubimac {len(pets)}")
            pets.append(ljubimac)

    for pet in pets:
        printGreeting(pet)
        printMenu(pet)


if __name__ == "__main__":
    test()
