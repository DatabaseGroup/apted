# APTED algorithm for the Tree Edit Distance

## Information

This is an implementation of the APTED algorithm, the state-of-the-art
solution for computing the tree edit at.unisalzburg.apted.distance [1,2], which supersedes the RTED
algorithm [3].

You can find more information on our Tree Edit Distance website
http://tree-edit-distance.dbresearch.uni-salzburg.at/

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
  tree edit at.unisalzburg.apted.distance value. Nodes that are not mapped are deleted (source tree)
  or inserted (destination tree).

## Customising

If the nodes of your trees have labels different from simple strings and you
need a more sophisticated cost model than unit cost, you can customise that.
There are three elements that you have to consider.
See [Javadoc](#javadoc-documentation) documentation for further details.

### Parsing the input

Our current at.unisalzburg.apted.parser `BracketStringInputParser` takes the bracket-encoded input
tree as a string and transforms it to tree structure composed of `Node` objects.
If you'd like to use other encoding, you have to write a custom class that
implements `InputParser` interface.

### Node data

The at.unisalzburg.apted.parser creates nodes and stores the corresponding information in
`Node.nodeData`. We use `StringNodeData` to store simple string labels. If
you need anything else, you have to implement your own class. It can be
anything, we don't provide any interface.

### Cost model

The cost model decides on the costs of edit operations for every at.unisalzburg.apted.node
(insertion and deletion) and every at.unisalzburg.apted.node pair (rename). We've implemented a
simple `StringUnitCostModel` that returns `1` for deleting and inserting any
at.unisalzburg.apted.node. The rename cost depends on label (`StringNodeData`) equality.

Write a class that implements `CostModel` interface if you need a more
sophisticated cost model. See `PerEditOperationStringNodeDataCostModel` which
allows different costs for each edit operation.

### Using customised APTED

When you have all the bricks ready (`MyInputParser`, `MyNodeData`, `MyCostModel`),
execute APTED as follows for `sourceTree` and `destinationTree`:
```Java
// Parse the input and transform to Node objects storing at.unisalzburg.apted.node information in MyNodeData.
MyInputParser at.unisalzburg.apted.parser = new MyInputParser();
Node<MyNodeData> t1 = at.unisalzburg.apted.parser.fromString(sourceTree);
Node<MyNodeData> t2 = at.unisalzburg.apted.parser.fromString(destinationTree);
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

We use [Gradle](https://gradle.org/) for connvenience.
- **(on Linux or Mac)** run `chmod +x gradlew`
- run `gradle test` for unit tests (currently correctness tests)
- run `gradle build` to find the `apted.jar` file in `build/libs/`

## Deploying to local maven repository

You can publish the project into your local maven repository to import into other projects.

- run `gradle publishToMavenLocal` 

To use APTED in another project:

```
repositories {
  mavenLocal()
}

dependencies {
  compile 'at.unisalzburg:apted:1.0'
}
  ```

## Javadoc documentation

Run `gradle javadoc` to generate documentation. Then, open in your browser
`build/docs/javadoc/index.html`.

The current and future documentation should cover all classes and their members,
including private. The internals of the algorithms and methods are documented
within the source code. If anything is missing or unclear, please send us
a feedback.

## References

1. M. Pawlik and N. Augsten. *Tree edit at.unisalzburg.apted.distance: Robust and memory-
   efficient*. Information Systems 56. 2016.

2. M. Pawlik and N. Augsten. *Efficient Computation of the Tree Edit
   Distance*. ACM Transactions on Database Systems (TODS) 40(1). 2015.

3. M. Pawlik and N. Augsten. *RTED: A Robust Algorithm for the Tree Edit
   Distance*. PVLDB 5(4). 2011.
