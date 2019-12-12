import matplotlib.pyplot as plt
import numpy as np
import csv
import sys

x = []
y = []
y_mean = []
i = 0
filename = sys.argv[1]

with open(filename,'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    for row in plots:
        y.append(float(row[0]))
        x.append(i)
        i += 1

print(str((np.mean(y)- 100)/5) + "%% correctness")
y_mean = [np.mean(y)]*len(x)
fig,ax = plt.subplots()
data_line = ax.plot(x,y, label='Data', marker='o')
mean_line = ax.plot(x,y_mean, label='Mean', linestyle='--')
fig.suptitle(filename, fontsize=16)

legend = ax.legend(loc='upper right')

plt.show()