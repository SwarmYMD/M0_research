import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as patches

width = 20
height = 20
steps = 100

def getGraph():
    for step in range(steps):
        fig=plt.figure(figsize=(7,7))
        ax=plt.axes()
        data_set = np.loadtxt(
            fname = "./csv/step"+str(step+1)+".csv",
            dtype = int,
            delimiter = ","
        )
        for i in range(height):
            for j in range(width):
                ax.add_patch(patches.Rectangle(xy=(i-0.5,j-0.5),width=1,height=1,fc="#000000"))

        for data in data_set:
            ax.add_patch(patches.Circle(xy=(data[0], data[1]),radius=0.5,fc="#ffffff"))
        plt.axis("scaled")
        ax.set_aspect("equal")
        ax.axis("off")
        fig.subplots_adjust(left=0, right=1, bottom=0, top=1)
        fig.savefig("./pics/step"+str(step+1)+".png")
        plt.close()

getGraph()
    