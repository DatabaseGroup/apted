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

package distance;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import util.LabelDictionary;
import util.LblTree;
import node.Node;

/**
 * Stores various indices on nodes required for efficient computation of APTED.
 *
 * @param <D> type of node data.
 */
// [TODO] Rename to NodeIndex.
public class InfoTree_PLUS<D> {

  /**
   * The input tree to the algorithm that is already parsed to tree structure
   * using {@link node.Node} class.
   *
   * @see node.Node
   * @see parser.InputParser
   */
  private Node<D> inputTree;

  private static final byte LEFT = 0;
  private static final byte RIGHT = 1;
  private static final byte HEAVY = 2;
  public int sizes[];
  public int parents[];
  public int preL_to_preR[];
  public int preR_to_preL[];

  /**
   * Array of pointers to node data objects. Used for cost of edit operations.
   */
  public Node<D> preL_to_node[];

  public int preL_to_ln[];
  public int preR_to_ln[];
  public int preL_to_kr_sum[];
  public int preL_to_rev_kr_sum[];
  public int preL_to_desc_sum[];

  // Added by Victor. From the implementation of RTED for mapping computation.
  public int postL_to_lld[];
  public int postR_to_rld[];

  public int preL_to_postL[];
  public int postL_to_preL[];

  public int preL_to_postR[];
  public int postR_to_preL[];

  private LabelDictionary ld;
  public boolean nodeType_L[];
  public boolean nodeType_R[];
  public boolean nodeType_H[];
  public int children[][];
  private int sizeTmp;
  private int descSizesTmp;
  private int krSizesSumTmp;
  private int revkrSizesSumTmp;
  private int preorderTmp;
  private int currentNode;
  private boolean switched;
  private int leafCount;
  private int treeSize;

  private int depthTmp;
  public int depths[];

  public int lchl;
  public int rchl;

  public InfoTree_PLUS(Node<D> aInputTree, LabelDictionary aLd) {
    sizeTmp = 0;
    descSizesTmp = 0;
    krSizesSumTmp = 0;
    revkrSizesSumTmp = 0;
    preorderTmp = 0;
    currentNode = -1;
    switched = false;
    leafCount = 0;
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
    nodeType_H = new boolean[treeSize];
    ld = aLd;
    currentNode = 0;

    depthTmp = -1;
    depths = new int[treeSize];

    gatherInfo(inputTree, -1);
    postTraversalProcessing();
  }

  public int getSize() {
    return treeSize;
  }

  public int getLeafCount() {
    return leafCount;
  }

  public boolean ifNodeOfType(int postorder, int type) {
    switch(type)
    {
    case 0: // '\0'
        return nodeType_L[postorder];

    case 1: // '\001'
        return nodeType_R[postorder];

    case 2: // '\002'
        return nodeType_H[postorder];
    }
    return false;
  }

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

  // [TODO] labels don't exist any more
  // public int getLabels(int node)
  // {
  //     return labels[node];
  // }

  public int getPreL_to_LN(int node) {
    return preL_to_ln[node];
  }

  public int getPreR_to_LN(int node) {
    return preR_to_ln[node];
  }

  public int getPreL_to_KR_Sum(int node) {
    return preL_to_kr_sum[node];
  }

  public int getPreL_to_Rev_KR_Sum(int node) {
    return preL_to_rev_kr_sum[node];
  }

  public int getPreL_to_Desc_Sum(int node) {
    return preL_to_desc_sum[node];
  }

  public int getCurrentNode() {
    return currentNode;
  }

  public void setCurrentNode(int preorderL) {
    currentNode = preorderL;
  }

  /**
   * Indexes the nodes of the input tree. Stores information about each tree
   * node in index arrys, for example, for each node n indexed with its preorder
   * number stores the subtree size rooted at n.
   */
  private int gatherInfo(Node<D> aT, int postorder) {
  	depthTmp++;
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
    ArrayList childrenPreorders = new ArrayList();
    preorderTmp++;
    // [TODO] Loop over children of a node.
    Iterator<Node<D>> childrenIt = aT.getChildren().iterator();
    while (childrenIt.hasNext()) {
    // for(Enumeration e = aT.getChildren(); e.hasMoreElements();) {
      childrenCount++;
      currentPreorder = preorderTmp;
      parents[currentPreorder] = preorder;

      // [TODO] Execute method recursively for next child.
      // postorder = gatherInfo((LblTree)e.nextElement(), postorder);
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
      // [TODO] If there is a next child.
      if(childrenIt.hasNext()) {
      // if(e.hasMoreElements()) {
          revkrSizesSum += revkrSizesSumTmp + sizeTmp + 1;
      } else {
          revkrSizesSum += revkrSizesSumTmp;
          nodeType_R[currentPreorder] = true;
      }
    }

    postorder++;

    // [TODO] tmp data not present in Node.
    //        tmpData seems not to be used.
    // aT.setTmpData(Integer.valueOf(preorder));

    int currentDescSizes = descSizes + currentSize + 1;
    preL_to_desc_sum[preorder] = ((currentSize + 1) * (currentSize + 1 + 3)) / 2 - currentDescSizes;
    preL_to_kr_sum[preorder] = krSizesSum + currentSize + 1;
    preL_to_rev_kr_sum[preorder] = revkrSizesSum + currentSize + 1;

    // [TODO] Store string label in node data.
    // labels[preorder] = ld.store(aT.getLabel());
    preL_to_node[preorder] = aT;

    sizes[preorder] = currentSize + 1;
    preorderR = treeSize - 1 - postorder;
    preL_to_preR[preorder] = preorderR;
    preR_to_preL[preorderR] = preorder;
    if(heavyChild != -1) {
      nodeType_H[heavyChild] = true;
    }
    children[preorder] = toIntArray(childrenPreorders);
    descSizesTmp = currentDescSizes;
    sizeTmp = currentSize;
    krSizesSumTmp = krSizesSum;
    revkrSizesSumTmp = revkrSizesSum;


    postL_to_preL[postorder] = preorder;
    preL_to_postL[preorder] = postorder;

    preL_to_postR[preorder] = treeSize-1-preorder;
    postR_to_preL[treeSize-1-preorder] = preorder;
    // postR to postL : info[13][treeSize - 1 - preorder] = postorder;

    depths[preorder] = depthTmp;
    depthTmp--;
    return postorder;
  }

    public boolean isLeaf(int nodeInPreorderL)
    {
        return sizes[nodeInPreorderL] == 1;
    }

    private void postTraversalProcessing()
    {
        int currentLeaf = -1;
        for(int i = 0; i < sizes[0]; i++)
        {
            preL_to_ln[i] = currentLeaf;
            if(isLeaf(i)) {
                currentLeaf = i;
            }

            // This block stores leftmost leaf descendants for each node
            // indexed in postorder. Used for mapping computation.
            // Added by Victor.
            int postl = i; // Assume that the for loop iterates postorder.
            int preorder = postL_to_preL[i];
            if (sizes[preorder] == 1)
                postL_to_lld[postl] = postl;
            else
                postL_to_lld[postl] = postL_to_lld[preL_to_postL[children[preorder][0]]];

            // This block stores rightmost leaf descendants for each node
            // indexed in right-to-left postorder.
            // [TODO] Implement revpost2_RLD, use both instead of APTED.getLLD
            //        and APTED.gerRLD methods, remove these method.
            //        Result: faster lookup of these values.
            int postr = i; // Assume that the for loop iterates reversed postorder.
            preorder = postR_to_preL[postr];
            if (sizes[preorder] == 1)
                postR_to_rld[postr] = postr;
            else
                postR_to_rld[postr] = postR_to_rld[preL_to_postR[children[preorder][children[preorder].length-1]]];

            //lchl and rchl TODO: there are no values for parent node
            if (sizes[i] == 1) {
            	int parent = parents[i];
	            if (parent > -1) {
		            if (parent+1 == i) {
		            	lchl++;
		            } else
		            if (preL_to_preR[parent]+1 == preL_to_preR[i]) {
		            	rchl++;
		            }
	            }
            }
        }

        currentLeaf = -1;
        for(int i = 0; i < sizes[0]; i++)
        {
            preR_to_ln[i] = currentLeaf;
            if(isLeaf(preR_to_preL[i])) {
                currentLeaf = i;
            }
        }

    }

    public static int[] toIntArray(List integers)
    {
        int ints[] = new int[integers.size()];
        int i = 0;
        for(Iterator iterator = integers.iterator(); iterator.hasNext();)
        {
            Integer n = (Integer)iterator.next();
            ints[i++] = n.intValue();
        }

        return ints;
    }

    public void setSwitched(boolean value)
    {
        switched = value;
    }

    public boolean isSwitched()
    {
        return switched;
    }
}
