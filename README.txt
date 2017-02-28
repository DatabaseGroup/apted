This is an implementation of the APTED algorithm from [2]. It builds on the
works in [1] and [3].

If you want to refer to APTED in a publication, please cite [1] and [2]. 

The source code is published under the MIT licence found in the header of each
source file.

We use gradle:
- run 'gradle test' for unit correctness tests,
- run 'gradle build' to find to JAR file in 'build/libs/'.

[1] M. Pawlik and N. Augsten. Efficient Computation of the Tree Edit 
    Distance. ACM Transactions on Database Systems (TODS) 40(1). 2015.
[2] M. Pawlik and N. Augsten. Tree edit distance: Robust and memory-
    efficient. Information Systems 56. 2016.
[3] M. Pawlik and N. Augsten. RTED: A Robust Algorithm for the Tree Edit 
    Distance. PVLDB 5(4). 2011.