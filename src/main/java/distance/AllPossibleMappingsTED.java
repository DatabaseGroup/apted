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
import java.util.Iterator;
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

  /**
   * TODO: Document it.
   */
  public AllPossibleMappingsTED(C costModel) {
    this.costModel = costModel;
  }

  /**
   * TODO: Document it.
   */
  public float computeEditDistance(Node<D> t1, Node<D> t2) {
    // Index the nodes of both input trees.
    init(t1, t2);
    ArrayList<ArrayList<int[]>> mappings = generate_all_one_to_one_mappings();
    System.out.println(mappingsToString(mappings));
    removeNonTEDMappings(mappings);
    System.out.println(mappingsToString(mappings));
    return get_min_cost(mappings);
  }

  /**
   * TODO: Document it.
   */
  public void init(Node<D> t1, Node<D> t2) {
    it1 = new NodeIndexer(t1, costModel);
    it2 = new NodeIndexer(t2, costModel);
    size1 = it1.getSize();
    size2 = it2.getSize();
  }

  // TODO: Document it.
  // TODO: Rename all names to Java formatting guidelines.

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

  /**
   * Given all 1-1 mappings, discard these that violate TED conditions.
   *
   * it1, it2 -- info tree objects
   * mappings -- all 1-1 mappings
   */
  private void removeNonTEDMappings(ArrayList<ArrayList<int[]>> mappings) {
    // Validate each mapping separately.
    // Iterator safely removes mappings while iterating.
    for (Iterator<ArrayList<int[]>> mit = mappings.iterator(); mit.hasNext();) {
      ArrayList<int[]> m = mit.next();
      if (!isTEDMapping(m)) {
        mit.remove();
      }
    }
  }

  /**
   * Test if a 1-1 mapping is a TED mapping.
   *
   * it1, it2 -- info tree objects
   * m        -- 1-1 mapping
   */
  boolean isTEDMapping(ArrayList<int[]> m) {
    // Validate each pair of pairs of mapped nodes in the mapping.
    for (int[] e1 : m) {
      // Use only pairs of mapped nodes for validation.
      if (e1[0] == -1 || e1[1] == -1) {
        continue;
      }
      for (int[] e2 : m) {
        // Use only pairs of mapped nodes for validation.
        if (e2[0] == -1 || e2[1] == -1) {
          continue;
        }
        // If any of the conditions below doesn't hold, discard m.
        // Validate ancestor-descendant condition.
        boolean a = e1[0] < e2[0] && it1.preL_to_preR[e1[0]] < it1.preL_to_preR[e2[0]];
        boolean b = e1[1] < e2[1] && it2.preL_to_preR[e1[1]] < it2.preL_to_preR[e2[1]];
        if ((a && !b) || (!a && b)) {
          // Discard the mapping.
          // If this condition doesn't hold, the next condition
          // doesn't have to be verified any more and any other
          // pair (e1, e2) doesn't have to be verified any more.
          return false;
        }
        // Validate sibling-order condition.
        a = e1[0] < e2[0] && it1.preL_to_preR[e1[0]] > it1.preL_to_preR[e2[0]];
        b = e1[1] < e2[1] && it2.preL_to_preR[e1[1]] > it2.preL_to_preR[e2[1]];
        if ((a && !b) || (!a && b)) {
          // Discard the mapping.
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Given list of all TED mappings, calculate the minimal cost.
   *
   * it1, it2         -- info tree objects
   * ted_mappings     -- set of all ted_mappings between some two forests
   * f1_size, f2_size -- sizes of two forests for which ted_mappings are given
   */
  float get_min_cost(ArrayList<ArrayList<int[]>> tedMappings) {
    // Initialize min_cost to the upper bound.
    float min_cost = size1 + size2;
    // Verify cost of each mapping.
    for (ArrayList<int[]> m : tedMappings) {
      float m_cost = 0;
      // Sum up edit costs for all elements in the mapping m.
      for (int[] e : m) {
        // Add edit operation cost.
        if (e[0] > -1 && e[1] > -1) {
          m_cost += costModel.ren(it1.preL_to_node[e[0]], it2.preL_to_node[e[1]]); // USE COST MODEL - rename e[0] to e[1].
        } else if (e[0] > -1) {
          m_cost += costModel.del(it1.preL_to_node[e[0]]); // USE COST MODEL - insert e[1].
        } else {
          m_cost += costModel.ins(it2.preL_to_node[e[1]]); // USE COST MODEL - delete e[0].
        }
        // Break as soon as the current min_cost is exceeded.
        // Only for early loop break.
        if (m_cost >= min_cost) {
          break;
        }
      }
      // Store the minimal cost - compare m_cost and min_cost
      min_cost = min_cost < m_cost ? min_cost : m_cost;
    }
    return min_cost;
  }

  /**
   * TODO: Document it.
   */
  private ArrayList<int[]> deepMappingCopy(ArrayList<int[]> mapping) {
    ArrayList<int[]> mapping_copy = new ArrayList<int[]>(mapping.size());
    for (int[] me : mapping) { // for each mapping element in a mapping
      mapping_copy.add(Arrays.copyOf(me, me.length));
    }
    return mapping_copy;
  }

  /**
   * TODO: Document it.
   */
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

  /**
   * TODO: Document it.
   */
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

  /**
   * TODO: Document it.
   */
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
