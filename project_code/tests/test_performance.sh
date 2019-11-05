#!/bin/bash
#
# Tests the performance of the Uniform Reliable Broadcast application.
#
# usage: ./test_performance evaluation_time
#
# evaluation_time: Specifies the number of seconds the application
#                  should run.
#

evaluation_time=$1
init_time=2

#start 5 processes
for i in `seq 1 11`
do
    java Da_proc $i membership 10&
    da_proc_id[$i]=$!
done

#leave some time for process initialization
sleep $init_time

#start broadcasting
echo "Evaluating application for ${evaluation_time} seconds."
for i in `seq 1 11`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill -USR2 "${da_proc_id[$i]}"
    fi
done

#let the processes do the work for some time
sleep $evaluation_time

echo "Evaluating application for ${evaluation_time} seconds."
for i in `seq 1 11`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill "${da_proc_id[$i]}"
    fi
done


echo "Performance test done."
