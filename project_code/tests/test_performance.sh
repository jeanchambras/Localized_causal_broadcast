#!/bin/bash

evaluation_time=$1
init_time=2
numberOfmessages=10
processes=10

rm *.out

#start 5 processes
for i in `seq 1 $processes`
do
    java Da_proc $i membership $numberOfmessages&
    da_proc_id[$i]=$!
done

#leave some time for process initialization
sleep $init_time

#start broadcasting
echo "Evaluating application for ${evaluation_time} seconds."
for i in `seq 1 $processes`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill -USR2 "${da_proc_id[$i]}"
    fi
done

sleep $evaluation_time

echo "Kill all processes"
for i in `seq 1 $processes`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill -TERM "${da_proc_id[$i]}"
    fi
done


echo "Performance test done."

let "l = $numberOfmessages + $numberOfmessages * $processes"
echo "Log files should be ${l} lines long"
for i in `seq 1 $processes`
do
  filename="./da_proc_${i}.out"
  wc -l $filename
done

