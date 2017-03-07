/* MIT License
 *
 * Copyright (c) 2017 Database Research Group Salzburg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package node;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import node.Node;

/**
 * Indexes nodes of an input tree and stores various indices on nodes required
 * for efficient computation of APTED [1,2]. Additionally, it stores
 * single-value properties of the tree.
 *
 * <p>For indexing we use four tree traversals that assign ids to the nodes:
 * <ul>
 * <li>left-to-right preorder [1],
 * <li>right-to-left preorder [1],
 * <li>left-to-right postorder [2],
 * <li>right-to-left postorder [2].
 * </ul>
 *
 * <p>See the source code for more algorithm-related comments.
 *
 * <p>References:
 * <ul>
 * <li>[1] M. Pawlik and N. Augsten. Efficient Computation of the Tree Edit
 *      Distance. ACM Transactions on Database Systems (TODS) 40(1). 2015.
 * <li>[2] M. Pawlik and N. Augsten. Tree edit distance: Robust and memory-
 *      efficient. Information Systems 56. 2016.
 * </ul>
 *
 * @param <D> type of node data.
 */
public class NodeIndexer<D> {

  /**
   * The input tree to the algorithm that is already parsed to tree structure
   * using {@link node.Node} class.
   *
   * @see node.Node
   * @see parser.InputParser
   */
  private Node<D> inputTree;

  // [TODO] Be consistent in naming indices: <FROM>_to_<TO>.

  // Structure indices.

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to Node
   * object corresponding to n. Used for cost of edit operations.
   *
   * @see node.Node
   */
  public Node<D> preL_to_node[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * size of n's subtree (node n and all its descendants).
   */
  public int sizes[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * left-to-right preorder id of n's parent.
   */
  public int parents[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * array of n's children. Size of children array at node n equals the number
   * of n's children.
   */
  public int children[][];

  /**
   * Index from left-to-right postorder id of node n (starting with 0) to the
   * left-to-right postorder id of n's leftmost leaf descendant.
   */
  public int postL_to_lld[];

  /**
   * Index from right-to-left postorder id of node n (starting with 0) to the
   * right-to-left postorder id of n's rightmost leaf descendant.
   */
  public int postR_to_rld[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * left-to-right preorder id of the first leaf node to the left of n. If
   * there is no leaf node to the left of n, it is represented with the value
   * '-1' [1, Section 8.4].
   */
  public int preL_to_ln[];

  /**
   * Index from right-to-left preorder id of node n (starting with 0) to the
   * right-to-left preorder id of the first leaf node to the right of n. If
   * there is no leaf node to the right of n, it is represented with the value
   * '-1' [1, Section 8.4].
   */
  public int preR_to_ln[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to
   * a boolean value that states if node n lies on the leftmost path starting
   * at n's parent [2, Algorithm 1, Lines 26,36].
   */
  public boolean nodeType_L[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to
   * a boolean value that states if node n lies on the rightmost path starting
   * at n's parent input tree [2, Section 5.3, Algorithm 1, Lines 26,36].
   */
  public boolean nodeType_R[];

  // Traversal translation indices.

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * right-to-left preorder id of n.
   */
  public int preL_to_preR[];

  /**
   * Index from right-to-left preorder id of node n (starting with 0) to the
   * left-to-right preorder id of n.
   */
  public int preR_to_preL[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * left-to-right postorder id of n.
   */
  public int preL_to_postL[];

  /**
   * Index from left-to-right postorder id of node n (starting with 0) to the
   * left-to-right preorder id of n.
   */
  public int postL_to_preL[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * right-to-left postorder id of n.
   */
  public int preL_to_postR[];

  /**
   * Index from right-to-left postorder id of node n (starting with 0) to the
   * left-to-right preorder id of n.
   */
  public int postR_to_preL[];

  // Cost indices.

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * cost of spf_L (single path function using the leftmost path) for the
   * subtree rooted at n [1, Section 5.2].
   */
  public int preL_to_kr_sum[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * cost of spf_R (single path function using the rightmost path) for the
   * subtree rooted at n [1, Section 5.2].
   */
  public int preL_to_rev_kr_sum[];

  /**
   * Index from left-to-right preorder id of node n (starting with 0) to the
   * cost of spf_A (single path function using an inner path) for the subtree
   * rooted at n [1, Section 5.2].
   */
  public int preL_to_desc_sum[];

  // Variables holding values modified at runtime while the algorithm executes.
  private int currentNode;
  private boolean switched;

  // Structure single-value variables.

  /**
   * [TODO] Document it.
   */
  private int treeSize;

  /**
   * [TODO] Document it.
   */
  public int lchl;

  /**
   * [TODO] Document it.
   */
  public int rchl;

  // Variables used temporarily while indexing.

  /**
   * Temporary variable used in indexing for storing subtree size.
   */
  private int sizeTmp;

  /**
   * Temporary variable used in indexing for storing sum of subtree sizes
   * rooted at descendant nodes.
   */
  private int descSizesTmp;

  /**
   * Temporary variable used in indexing for storing sum of keyroot node sizes.
   */
  private int krSizesSumTmp;

  /**
   * Temporary variable used in indexing for storing sum of right-to-left
   * keyroot node sizes.
   */
  private int revkrSizesSumTmp;

  /**
   * Temporary variable used in indexing for storing preorder index of a node.
   */
  private int preorderTmp;

  /**
   * Indexes the nodes of input trees and stores the indices for quick access
   * from APTED algorithm.
   *
   * @param aInputTree an input tree to APTED. Its nodes will be indexed.
   * @param <D> type on node data.
   */
  public NodeIndexer(Node<D> aInputTree) {
    sizeTmp = 0;
    descSizesTmp = 0;
    krSizesSumTmp = 0;
    revkrSizesSumTmp = 0;
    preorderTmp = 0;
    currentNode = -1;
    switched = false;
    treeSize = 0;

    inputTree = aInputTree;

    treeSize = inputTree.getNodeCount();
    sizes = new int[treeSize];
    parents = new int[treeSize];
    preL_to_preR = new int[treeSize];
    preR_to_preL = new int[treeSize];

    preL_to_postL = new int[treeSize];
    postL_to_preL = new int[treeSize];

    preL_to_postR = new int[treeSize];
    postR_to_preL = new int[treeSize];

    postL_to_lld = new int[treeSize]; // Added by Viktor. For mapping computation.
    postR_to_rld = new int[treeSize];

    // Store a pointer to node data.
    preL_to_node = new Node[treeSize];

    preL_to_ln = new int[treeSize];
    preR_to_ln = new int[treeSize];
    preL_to_kr_sum = new int[treeSize];
    preL_to_rev_kr_sum = new int[treeSize];
    preL_to_desc_sum = new int[treeSize];
    Arrays.fill(parents, -1);
    children = new int[treeSize][];
    nodeType_L = new boolean[treeSize];
    nodeType_R = new boolean[treeSize];

    currentNode = 0;

    gatherInfo(inputTree, -1);
    postTraversalProcessing();
  }

  /**
   * Indexes the nodes of the input tree. Stores information about each tree
   * node in index arrays, for example, for each node n indexed with its preorder
   * number stores the subtree size rooted at n.
   */
  // [TODO] Change name to indexNodes and document parameters.
  private int gatherInfo(Node<D> aT, int postorder) {
    int currentSize = 0;
    int childrenCount = 0;
    int descSizes = 0;
    int krSizesSum = 0;
    int revkrSizesSum = 0;
    int preorder = preorderTmp;
    int preorderR = 0;
    int heavyChild = -1;
    int weight = -1;
    int maxWeight = -1;
    int currentPreorder = -1;
    ArrayList<Integer> childrenPreorders = new ArrayList<>();
    preorderTmp++;
    // Loop over children of a node.
    Iterator<Node<D>> childrenIt = aT.getChildren().iterator();
    while (childrenIt.hasNext()) {
      childrenCount++;
      currentPreorder = preorderTmp;
      parents[currentPreorder] = preorder;

      // Execute method recursively for next child.
      postorder = gatherInfo(childrenIt.next(), postorder);

      childrenPreorders.add(Integer.valueOf(currentPreorder));
      weight = sizeTmp + 1;
      if(weight >= maxWeight) {
          maxWeight = weight;
          heavyChild = currentPreorder;
      }
      currentSize += 1 + sizeTmp;
      descSizes += descSizesTmp;
      if(childrenCount > 1) {
          krSizesSum += krSizesSumTmp + sizeTmp + 1;
      } else {
          krSizesSum += krSizesSumTmp;
          nodeType_L[currentPreorder] = true;
      }
      if(childrenIt.hasNext()) {
          revkrSizesSum += revkrSizesSumTmp + sizeTmp + 1;
      } else {
          revkrSizesSum += revkrSizesSumTmp;
          nodeType_R[currentPreorder] = true;
      }
    }

    postorder++;

    int currentDescSizes = descSizes + currentSize + 1;
    preL_to_desc_sum[preorder] = ((currentSize + 1) * (currentSize + 1 + 3)) / 2 - currentDescSizes;
    preL_to_kr_sum[preorder] = krSizesSum + currentSize + 1;
    preL_to_rev_kr_sum[preorder] = revkrSizesSum + currentSize + 1;

    // Store pointer to a node object corresponding to preorder.
    preL_to_node[preorder] = aT;

    sizes[preorder] = currentSize + 1;
    preorderR = treeSize - 1 - postorder;
    preL_to_preR[preorder] = preorderR;
    preR_to_preL[preorderR] = preorder;

    children[preorder] = toIntArray(childrenPreorders);
    descSizesTmp = currentDescSizes;
    sizeTmp = currentSize;
    krSizesSumTmp = krSizesSum;
    revkrSizesSumTmp = revkrSizesSum;

    postL_to_preL[postorder] = preorder;
    preL_to_postL[preorder] = postorder;

    preL_to_postR[preorder] = treeSize-1-preorder;
    postR_to_preL[treeSize-1-preorder] = preorder;

    return postorder;
  }

  /**
   * [TODO] Document it.
   */
  private void postTraversalProcessing() {
    int currentLeaf = -1;
    for(int i = 0; i < sizes[0]; i++) {
      preL_to_ln[i] = currentLeaf;
      if(isLeaf(i)) {
          currentLeaf = i;
      }

      // This block stores leftmost leaf descendants for each node
      // indexed in postorder. Used for mapping computation.
      // Added by Victor.
      int postl = i; // Assume that the for loop iterates postorder.
      int preorder = postL_to_preL[i];
      if (sizes[preorder] == 1) {
        postL_to_lld[postl] = postl;
      } else {
        postL_to_lld[postl] = postL_to_lld[preL_to_postL[children[preorder][0]]];
      }
      // This block stores rightmost leaf descendants for each node
      // indexed in right-to-left postorder.
      // [TODO] Use postL_to_lld and postR_to_rld instead of APTED.getLLD
      //        and APTED.gerRLD methods, remove these method.
      //        Result: faster lookup of these values.
      int postr = i; // Assume that the for loop iterates reversed postorder.
      preorder = postR_to_preL[postr];
      if (sizes[preorder] == 1) {
        postR_to_rld[postr] = postr;
      } else {
        postR_to_rld[postr] = postR_to_rld[preL_to_postR[children[preorder][children[preorder].length-1]]];
      }
      //lchl and rchl TODO: there are no values for parent node
      if (sizes[i] == 1) {
      	int parent = parents[i];
        if (parent > -1) {
          if (parent+1 == i) {
          	lchl++;
          } else if (preL_to_preR[parent]+1 == preL_to_preR[i]) {
          	rchl++;
          }
        }
      }
    }

    currentLeaf = -1;
    for(int i = 0; i < sizes[0]; i++) {
      preR_to_ln[i] = currentLeaf;
      if(isLeaf(preR_to_preL[i])) {
        currentLeaf = i;
      }
    }

  }

  /**
   * [TODO] Document it.
   */
  public int getSize() {
    return treeSize;
  }

  /**
   * [TODO] Document it.
   */
  public boolean isLeaf(int nodeInPreorderL) {
    return sizes[nodeInPreorderL] == 1;
  }

  /**
   * [TODO] Document it.
   */
  public static int[] toIntArray(ArrayList<Integer> integers) {
    int ints[] = new int[integers.size()];
    int i = 0;
    for (Integer n : integers) {
      ints[i++] = n.intValue();
    }
    return ints;
  }

  /**
   * [TODO] Document it.
   */
  public void setSwitched(boolean value) {
    switched = value;
  }

  /**
   * [TODO] Document it.
   */
  public boolean isSwitched() {
    return switched;
  }

  /**
   * [TODO] Document it.
   */
  public int getCurrentNode() {
    return currentNode;
  }

  /**
   * [TODO] Document it.
   */
  public void setCurrentNode(int preorderL) {
    currentNode = preorderL;
  }

  // [TODO] All methods below are used in APTED. Since we use direct access to
  //        public fields of this class, these methods should be substituted
  //        with a direct access (get the pointers to these methods before used
  //        in APTED).

  public int[] getChildren(int node) {
    return children[node];
  }

  public int getSizes(int node) {
    return sizes[node];
  }

  public int getParents(int node) {
    return parents[node];
  }

  public int getPreL_to_PreR(int node) {
    return preL_to_preR[node];
  }

  public int getPreR_to_PreL(int node) {
    return preR_to_preL[node];
  }

  public int getPreL_to_LN(int node) {
    return preL_to_ln[node];
  }

  public int getPreR_to_LN(int node) {
    return preR_to_ln[node];
  }

}
