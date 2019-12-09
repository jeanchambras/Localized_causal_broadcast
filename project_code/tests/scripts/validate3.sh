#!/bin/bash

if [ "$1" = "" ] || [ "$2" = "" ]; then
    echo "Validates the submission (i.e., valid input for a simple test-case) for the given excercise"
    echo -e "usage: \n$ $0 <exercice_name (\"FIFO\" or \"LCausal\")> <language (\"C\" or \"JAVA\")>"
    exit 1

fi

# time to wait for correct processes to broadcast all messages (in seconds)
# (should be adapted to the number of messages to send)
time_to_finish=2
init_time=2

# create default Makefile if not existing
if [ ! -f Makefile ]; then
  echo "WARNING: Makefile not found! using default Makefile"
  if [ "$2" = "C" ]; then
    cp Makefile_c_example Makefile
  else
    cp Makefile_java_example Makefile
  fi
fi

# compile (should output: da_proc or Da_proc.class)
make clean
make

# prepare input
if [ "$1" = "FIFO" ]; then
echo "writing FIFO input..."
 
echo "5
1 127.0.0.1 12001
2 127.0.0.1 12002
3 127.0.0.1 12003
4 127.0.0.1 12004
5 127.0.0.1 12005" > membership

else 
echo "writing LCausal input..."
    
echo "5
1 127.0.0.1 1001
2 127.0.0.1 2002
3 127.0.0.1 2003
4 127.0.0.1 2004
5 127.0.0.1 2005
1 4 5
2 1
3 1 2
4
5 3 4" > membership
fi

# start 5 processes, each broadcasting 100 messages
for i in `seq 1 5`
do
    if [ "$2" = "C" ]; then
      ./da_proc $i membership 100 &
    else
      java Da_proc $i membership 100 &
    fi
    da_proc_id[$i]=$!
done

# leave some time for process initialization
sleep $init_time


# start broadcasting
for i in `seq 1 5`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill -USR2 "${da_proc_id[$i]}"
    fi
done


sleep $time_to_finish

# stop all processes
for i in `seq 1 5`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill -TERM "${da_proc_id[$i]}"
    fi
done

# wait until all processes stop
for i in `seq 1 5`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	    wait "${da_proc_id[$i]}"
    fi
done

for i in `seq 1 5`
do
    python3 count.py da_proc_${i}.out
done
python3 average.py 5

echo "test done"