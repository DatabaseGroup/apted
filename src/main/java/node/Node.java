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

import java.util.Vector;

// BracketStringInputParser parser = new BracketStringInputParser();
// Node<StringNodeData> t1 = parser.fromString(ts1);
// int size1 = Node.getNodeCount(); // in Node: static int getNodeCount(){}

/**
 * This is a recursive representation of an ordered tree. Each node stores a
 * vector of pointers to its children. The order of children is significant and
 * must be observed while implmeneting a custom input parser.
 *
 * @param <D> the type of node data.
 * @see InputParser
 */
public class Node<D> {
  private D nodeData;
  private Vector<Node<D>> children;

  public Node(D nodeData) {
    this.children = new Vector<Node<D>>();
    setNodeData(nodeData);
  }

  public D getNodeData() {
    return nodeData;
  }

  public void setNodeData(D nodeData) {
    this.nodeData = nodeData;
  }

  public void addChild(Node c) {
    this.children.add(c);
  }
}
