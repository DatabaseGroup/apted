/* MIT License
 *
 * Copyright (c) 2017 Mateusz Pawlik, Nikolaus Augsten
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

package parser;

import node.Node;
import node.StringNodeData;
import util.FormatUtilities;

import java.util.Vector;

// [TODO] Make this parser independent from FormatUtilities - move here relevant elements.

/**
 * Parser for the input trees in the bracket notation with a single string-value
 * label of type {@link StringNodeData}.
 * <p>
 * <p>Bracket notation encodes the trees with nested parentheses, for example,
 * in tree {A{B{X}{Y}{F}}{C}} the root node has label A and two children with
 * labels B and C. Node with label B has three children with labels X, Y, F.
 *
 * @see Node
 * @see StringNodeData
 */
public class BracketStringInputParser implements InputParser<StringNodeData> {

    /**
     * Parses the input tree as a string and converts it to our tree
     * representation using the {@link Node} class.
     *
     * @param s input tree as string in bracket notation.
     * @return tree representation of the bracket notation input.
     * @see Node
     */
    public Node<StringNodeData> fromString(String s) {
        s = s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1);
        Node<StringNodeData> node = new Node<StringNodeData>(new StringNodeData(FormatUtilities.getRoot(s)));
        Vector c = FormatUtilities.getChildren(s);
        for (int i = 0; i < c.size(); i++)
            node.addChild(fromString((String) c.elementAt(i)));
        return node;
    }
}
