# APTED algorithm for the Tree Edit Distance

## Information

This is the implementation of the APTED algorithm - the state-of-the-art
solution for computing the tree edit distance [1,2,3].

You can find more information on our Tree Edit Distance website 
http://tree-edit-distance.dbresearch.uni-salzburg.at/

## Citing APTED

If you want to refer to APTED in a publication, please cite [1] and [2]. 

## Licence

The source code is published under the **MIT licence** found in the root project 
directory and in the header of each source file.

## Building APTED

You can clone the code, compile, and build the JAR file the regular Java way.

We use gradle for connvenience:
- run `gradle test` for unit correctness tests,
- run `gradle build` to find the JAR file in `build/libs/`.

## Manual

Execute `java -jar APTED.jar -h` for manual and help.

## References

1. M. Pawlik and N. Augsten. *Tree edit distance: Robust and memory-
   efficient*. Information Systems 56. 2016.

2. M. Pawlik and N. Augsten. *Efficient Computation of the Tree Edit 
   Distance*. ACM Transactions on Database Systems (TODS) 40(1). 2015.

3. M. Pawlik and N. Augsten. *RTED: A Robust Algorithm for the Tree Edit 
   Distance*. PVLDB 5(4). 2011.