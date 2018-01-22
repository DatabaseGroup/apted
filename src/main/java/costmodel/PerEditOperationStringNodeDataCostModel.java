/* MIT License
 *
 * Copyright (c) 2017 Mateusz Pawlik
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

package costmodel;

import node.Node;
import node.StringNodeData;

/**
 * This is a cost model defined on {@link node.StringNodeData} with a fixed cost
 * per edit operation.
 */
public class PerEditOperationStringNodeDataCostModel implements CostModel<StringNodeData> {

    /**
     * Stores the cost of deleting a node.
     */
    private float deleteCost;

    /**
     * Stores the cost of inserting a node.
     */
    private float insertCost;

    /**
     * Stores the cost of mapping two nodes (renaming their labels).
     */
    private float renameCost;

    /**
     * Initialises the cost model with the passed edit operation costs.
     *
     * @param deleteCost deletion cost.
     * @param insertCost insertion cost.
     * @param renameCost rename cost.
     */
    public PerEditOperationStringNodeDataCostModel(float deleteCost, float insertCost, float renameCost) {
        this.deleteCost = deleteCost;
        this.insertCost = insertCost;
        this.renameCost = renameCost;
    }

    /**
     * Calculates the cost of deleting a node.
     *
     * @param node the node considered to be deleted.
     * @return the cost of deleting node n.
     */
    public float delete(Node<StringNodeData> node) {
        return deleteCost;
    }

    /**
     * Calculates the cost of inserting a node.
     *
     * @param node the node considered to be inserted.
     * @return the cost of inserting node n.
     */
    public float insert(Node<StringNodeData> node) {
        return insertCost;
    }

    /**
     * Calculates the cost of renaming the string labels of two nodes.
     *
     * @param sourceNode the source node of rename.
     * @param targetNode the destination node of rename.
     * @return the cost of renaming node n1 to n2.
     */
    public float rename(Node<StringNodeData> sourceNode, Node<StringNodeData> targetNode) {
        String source = sourceNode.getNodeData().getLabel();
        String target = targetNode.getNodeData().getLabel();
        return (source.equals(target)) ? 0.0f : renameCost;
    }
}
