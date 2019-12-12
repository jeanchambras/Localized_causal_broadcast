
import sys

fname = sys.argv[1]

def file_len(fname):
    with open(fname) as f:
        for i, l in enumerate(f):
            pass
    return i + 1

print(fname+ " : " + str(file_len(fname)))