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
import java.util.Arrays;
import node.Node;
import node.NodeIndexer;
import costmodel.CostModel;

public class AllPossibleMappingsTED<C extends CostModel, D> {

  /**
   * Indexer of the source tree.
   *
   * @see node.NodeIndexer
   */
  private NodeIndexer it1;

  /**
   * Indexer of the destination tree.
   *
   * @see node.NodeIndexer
   */
  private NodeIndexer it2;

  /**
   * The size of the source input tree.
   */
  private int size1;

  /**
   * The size of the destination tree.
   */
  private int size2;

  /**
   * Cost model to be used for calculating costs of edit operations.
   */
  private C costModel;

  public AllPossibleMappingsTED(C costModel) {
    this.costModel = costModel;
  }

  public float computeEditDistance(Node<D> t1, Node<D> t2) {
    // Index the nodes of both input trees.
    init(t1, t2);
    ArrayList<ArrayList<int[]>> mappings = generate_all_one_to_one_mappings();
    System.out.println(mappingsToString(mappings));
    return -1;
  }

  public void init(Node<D> t1, Node<D> t2) {
    it1 = new NodeIndexer(t1, costModel);
    it2 = new NodeIndexer(t2, costModel);
    size1 = it1.getSize();
    size2 = it2.getSize();
  }

  /**
   * Generate all possible 1-1 mappings.
   *
   * <p>These mappings do not conform to TED conditions (sibling-order and
   * ancestor-descendant).
   *
   * <p>A mapping is a list of pairs (also lists) of preorder IDs (identifying
   * nodes).
   *
   * @param f1 sets of nodes in the source tree.
   * @param f2 sets of nodes in the destination tree.
   */
  private ArrayList<ArrayList<int[]>> generate_all_one_to_one_mappings() {
    // Start with an empty mapping - all nodes are deleted or inserted.
    ArrayList<ArrayList<int[]>> mappings = new ArrayList<ArrayList<int[]>>(1);
    mappings.add(new ArrayList<int[]>(size1 + size2));
    // Add all deleted nodes.
    for (int n1 = 0; n1 < size1; n1++) {
      mappings.get(0).add(new int[]{n1, -1});
    }
    // Add all inserted nodes.
    for (int n2 = 0; n2 < size2; n2++) {
      mappings.get(0).add(new int[]{-1, n2});
    }
    // mappings.get(0).add(new int[]());
    System.out.println(mappings.size());
    System.out.println(mappings.get(0).size());
    // For each node in the source tree.
    for (int n1 = 0; n1 < size1; n1++) {
      // Duplicate all mappings and store in mappings_copy.
      ArrayList<ArrayList<int[]>> mappings_copy = deepMappingsCopy(mappings);
      // For each node in the destination tree.
      for (int n2 = 0; n2 < size2; n2++) {
        // For each mapping (produced for all n1 values smaller than
        // current n1).
        for (ArrayList<int[]> m : mappings_copy) {
          // Produce new mappings with the pair (n1, n2) by adding this
          // pair to all mappings where it is valid to add.
          boolean element_add = true;
          // Verify if (n1, n2) can be added to mapping m.
          // All elements in m are checked with (n1, n2) for possible
          // violation.
          // One-to-one condition.
          for (int[] e : m) {
            // n1 is not in any of previous mappings
            if (e[0] != -1 && e[1] != -1 && e[1] == n2) {
              element_add = false;
              System.out.println("Add " + n2 + " false.");
              break;
            }
          }
          // New mappings must be produced by duplicating a previous
          // mapping and extending it by (n1, n2).
          if (element_add) {
            ArrayList<int[]> m_copy = deepMappingCopy(m);
            m_copy.add(new int[]{n1, n2});
            System.out.println("Add " + n1 + "," + n2 + " true.");
            // If a pair (n1,n2) is added, (n1,-1) and (-1,n2) must be removed.
            System.out.println(removeMappingElement(m_copy, new int[]{n1, -1}));
            System.out.println(removeMappingElement(m_copy, new int[]{-1, n2}));
            mappings.add(m_copy);
            String result = "{";
            for (int[] me : m_copy) {
              result += "[" + me[0] + "," + me[1] + "]";
            }
            result += "}\n";
            System.out.println("Added " + result);
          }
        }
      }
    }
    return mappings;
  }

  private ArrayList<int[]> deepMappingCopy(ArrayList<int[]> mapping) {
    ArrayList<int[]> mapping_copy = new ArrayList<int[]>(mapping.size());
    for (int[] me : mapping) { // for each mapping element in a mapping
      mapping_copy.add(Arrays.copyOf(me, me.length));
    }
    return mapping_copy;
  }

  private ArrayList<ArrayList<int[]>> deepMappingsCopy(ArrayList<ArrayList<int[]>> mappings) {
    ArrayList<ArrayList<int[]>> mappings_copy = new ArrayList<ArrayList<int[]>>(mappings.size());
    for (ArrayList<int[]> m : mappings) { // for each mapping in mappings
      ArrayList<int[]> m_copy = new ArrayList<int[]>(m.size());
      for (int[] me : m) { // for each mapping element in a mapping
        m_copy.add(Arrays.copyOf(me, me.length));
      }
      mappings_copy.add(m_copy);
    }
    return mappings_copy;
  }

  private String mappingsToString(ArrayList<ArrayList<int[]>> mappings) {
    String result = "Mappings:\n";
    for (ArrayList<int[]> m : mappings) {
      result += "{";
      for (int[] me : m) {
        result += "[" + me[0] + "," + me[1] + "]";
      }
      result += "}\n";
    }
    return result;
  }

  private boolean removeMappingElement(ArrayList<int[]> m, int[] e) {
    for (int[] me : m) {
      if (me[0] == e[0] && me[1] == e[1]) {
        m.remove(me);
        return true;
      }
    }
    return false;
  }
}
