
import sys
import csv

def file_len(fname):
    with open(fname) as f:
        for i, l in enumerate(f):
            pass
    return i + 1

count = 0
for i in range (1,int(sys.argv[1])+1):
    fname = "da_proc_{}.out".format(i)
    count += file_len(fname)

print("average n# : " + str(count/(int(sys.argv[1]))))

with open('average.csv','a+') as fd:
    writer = csv.writer(fd)
    writer.writerow({count/(int(sys.argv[1]))})