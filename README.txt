This is an implementation of the APTED algorithm from [2]. It builds up on the
works in [1] and [3].

The source code is published under the MIT licence found in the header of each
source file.

To build, do the following steps from within the root directory:
  mkdir build
  cd build
  cmake ..
  make

The jar file can be found in 'build'.

[1] M. Pawlik and N. Augsten. Efficient Computation of the Tree Edit 
    Distance. ACM Transactions on Database Systems (TODS) 40(1). 2015.
[2] M. Pawlik and N. Augsten. Tree edit distance: Robust and memory-
    efficient. Information Systems 56. 2016.
[3] M. Pawlik and N. Augsten. RTED: A Robust Algorithm for the Tree Edit 
    Distance. PVLDB 5(4). 2011.