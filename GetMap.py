import numpy as np

width = 200
height = 200
final_step = 2

table = [[0 for i in range(width)] for j in range(height)]

def getMap():
    data_set = np.loadtxt(
        fname = "./csv/step"+str(final_step)+".csv",
        dtype = int,
        delimiter = ","
    )

    for data in data_set:
        table[data[1]][data[0]] = 1

    for j in range(height):
        for i in range(width):
            print(table[j][i], end="")
        print()
    print()

getMap()
