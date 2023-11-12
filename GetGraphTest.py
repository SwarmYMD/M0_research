import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as patches

width = 200
height = 200
steps = 100

def getGraph():
    for step in range(steps):
        fig=plt.figure(figsize=(7,7))
        ax=plt.axes()
        data_set = np.loadtxt(
            fname = "./csv_test/step"+str(step)+".csv",
            dtype = int,
            delimiter = ","
        )
        for i in range(height):
            for j in range(width):
                ax.add_patch(patches.Rectangle(xy=(i-0.5,j-0.5),width=1,height=1,fc="#000000"))

        for data in data_set:
            ax.add_patch(patches.Rectangle(xy=(data[0]-0.5, data[1]-0.5),width=0.5,height=0.5,fc="#5aff19"))
        plt.axis("scaled")
        ax.set_aspect("equal")
        ax.axis("off")
        fig.subplots_adjust(left=0, right=1, bottom=0, top=1)
        ax.invert_yaxis()
        fig.savefig("./pics_test/step"+str(step)+".png")
        plt.close()

getGraph()