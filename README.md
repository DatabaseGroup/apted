# APTED algorithm for the Tree Edit Distance

## Information

This is an implementation of the APTED algorithm, the state-of-the-art
solution for computing the tree edit distance [1,2], which supersedes the RTED
algorithm [3].

You can find more information on our Tree Edit Distance website
http://tree-edit-distance.dbresearch.uni-salzburg.at/

### Deprecated API

As we've been pointed, our API had incorrect packaging causing some troubles
(especially, the `util` package).
We've fixed the packaging. For the sake of current users, we've left also the
old one that we've annotated as deprecated in both, source code and javadoc.
We're planning on removing it from the repository at some point.

## Citing APTED

If you want to refer to APTED in a publication, please cite [1] and [2].

## Licence

The source code is published under the **MIT licence** found in the root
directory of the project and in the header of each source file.

## Input

Currently, we support only the so-called bracket notation for the input trees,
for example, encoding `{A{B{X}{Y}{F}}{C}}` corresponds to the following tree:
```
    A
   / \
  B   C
 /|\
X Y F
```

## Output

Our tool computes two outputs:
- tree edit **distance** value - the minimum cost of transforming the source
  tree into the destination tree.
- tree edit **mapping** - a mapping between nodes that corresponds to the
  tree edit distance value. Nodes that are not mapped are deleted (source tree)
  or inserted (destination tree).

## Customising

If the nodes of your trees have labels different from simple strings and you
need a more sophisticated cost model than unit cost, you can customise that.
There are three elements that you have to consider.
See [Javadoc](#javadoc-documentation) documentation for further details.

### Parsing the input

Our current parser `BracketStringInputParser` takes the bracket-encoded input
tree as a string and transforms it to tree structure composed of `Node` objects.
If you'd like to use other encoding, you have to write a custom class that
implements `InputParser` interface.

### Node data

The parser creates nodes and stores the corresponding information in
`Node.nodeData`. We use `StringNodeData` to store simple string labels. If
you need anything else, you have to implement your own class. It can be
anything, we don't provide any interface.

### Cost model

The cost model decides on the costs of edit operations for every node
(insertion and deletion) and every node pair (rename). We've implemented a
simple `StringUnitCostModel` that returns `1` for deleting and inserting any
node. The rename cost depends on label (`StringNodeData`) equality.

Write a class that implements `CostModel` interface if you need a more
sophisticated cost model. See `PerEditOperationStringNodeDataCostModel` which
allows different costs for each edit operation.

### Using customised APTED

When you have all the bricks ready (`MyInputParser`, `MyNodeData`, `MyCostModel`),
execute APTED as follows for `sourceTree` and `destinationTree`:
```Java
// Parse the input and transform to Node objects storing node information in MyNodeData.
MyInputParser parser = new MyInputParser();
Node<MyNodeData> t1 = parser.fromString(sourceTree);
Node<MyNodeData> t2 = parser.fromString(destinationTree);
// Initialise APTED.
APTED<MyCostModel, MyNodeData> apted = new APTED<>(new MyCostModel());
// Execute APTED.
float result = apted.computeEditDistance(t1, t2);
```

## Execution manual

Execute `java -jar apted.jar -h` for manual and help.

## Building APTED

You can clone the code, compile, and build the JAR file the regular command-line
way.

We use [Gradle](https://gradle.org/) for convenience.
- [install Gradle](https://gradle.org/install)
- run `gradle test` for unit tests (currently correctness tests)
- run `gradle build` to find the `apted.jar` file in `build/libs/`

### Gradle wrapper

We intentionally do not put automatically generated Gradle wrapper files in the
repository. We don't like that. However, if it helps, we've added wrapper task section to `build.gradle` file.

## Javadoc documentation

Run `gradle javadoc` to generate documentation. Then, open in your browser
`build/docs/javadoc/index.html`.

The current and future documentation should cover all classes and their members,
including private. The internals of the algorithms and methods are documented
within the source code. If anything is missing or unclear, please send us
a feedback.

## References

1. M. Pawlik and N. Augsten. *Tree edit distance: Robust and memory-
   efficient*. Information Systems 56. 2016.

2. M. Pawlik and N. Augsten. *Efficient Computation of the Tree Edit
   Distance*. ACM Transactions on Database Systems (TODS) 40(1). 2015.

3. M. Pawlik and N. Augsten. *RTED: A Robust Algorithm for the Tree Edit
   Distance*. PVLDB 5(4). 2011.
