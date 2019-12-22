# Distributed algorithms project

*Distributed algorithms EPFL (CS-451), 2019 summer session*


```
   _______________                        |*\_/*|________
  |  ___________  |     .-.     .-.      ||_/-\_|______  |
  | |           | |    .****. .****.     | |           | |
  | |   0   0   | |    .*****.*****.     | |   0   0   | |
  | |     -     | |     .*********.      | |     -     | |
  | |   \___/   | |      .*******.       | |   \___/   | |
  | |___     ___| |       .*****.        | |___________| |
  |_____|\_/|_____|        .***.         |_______________|
    _|__|/ \|_|_.............*.............._|________|_
   / ********** \                          / ********** \
 /  ************  \                      /  ************  \
--------------------                    --------------------
```

The goal of this practical project is to implement certain building blocks necessary for a decentralized system. To this end, some underlying abstractions will be used:

1. Perfect Links,
2. Best Effort Broadcast
3. Uniform Reliable Broadcast,
4. Localized Causal Broadcast (submission #2)

Various applications can be built upon these lower-level abstractions. 

The implementation must take into account that messages exchanged between processes may be dropped, delayed or reordered by the network. The execution of processes may be paused for an arbitrary amount of time and resumed later. Processes may also fail by crashing at arbitrary points of their execution.

# Test LCB

To test the project, execute the  ```validate.sh``` shell script. The script shuffle packets received order and add some random loss and delay. 

        ./validate.sh LCausal JAVA

The script creates 5 instances of ```Da_proc.java```, and manages these pid (pausing, stopping, resuming). Each instance will broadcast messages (100 by default) to the other processes. 

Each process ouput a log file (name matching ```da\_proc\_[0-9]+\.out```). Each log file contains broadcasted messages and delivered messages. Messages are delivered according to the Localized Causal Broadcast specs.