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

import java.util.Collection;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.google.gson.Gson;
import static org.junit.Assert.assertEquals;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.distance.AllPossibleMappingsTED;
import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;
import at.unisalzburg.dbresearch.apted.node.Node;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import at.unisalzburg.dbresearch.apted.costmodel.PerEditOperationStringNodeDataCostModel;

/**
 * Correctness unit tests of distance computation for node labels with a single
 * string value and per-edit-operation cost model.
 *
 * @see StringNodeData
 * @see PerEditOperationStringNodeDataCostModel
 */
@RunWith(Parameterized.class)
public class PerEditOperationCorrectnessTest {

  /**
   * Test case object holding parameters of a single test case.
   *
   * <p>Could be also deserialized here but without much benefit.
   */
  private TestCase testCase;

  /**
   * This class represents a single test case from the JSON file. JSON keys
   * are mapped to fiels of this class.
   */
  // [TODO] Verify if this is the best placement for this class.
  private static class TestCase {

    /**
     * Test identifier to quickly find failed test case in JSON file.
     */
    private int testID;

    /**
     * Source tree as string.
     */
    private String t1;

    /**
     * Destination tree as string.
     */
    private String t2;

    /**
     * Correct distance value between source and destination trees.
     */
    private int d;

    /**
     * Used in printing the test case details on failure with '(name = "{0}")'.
     *
     * @return test case details.
     * @see CorrectnessTest#data()
     */
    public String toString() {
      return "testID:" + testID + ",t1:" + t1 + ",t2:" + t2 + ",d:" + d;
    }

    /**
     * Returns identifier of this test case.
     *
     * @return test case identifier.
     */
    public int getTestID() {
      return testID;
    }

    /**
     * Returns source tree of this test case.
     *
     * @return source tree.
     */
    public String getT1() {
      return t1;
    }

    /**
     * Returns destination tree of this test case.
     *
     * @return destination tree.
     */
    public String getT2() {
      return t2;
    }

    /**
     * Returns correct distance value between source and destination trees
     * of this test case.
     *
     * @return correct distance.
     */
    public int getD() {
      return d;
    }

  }

  /**
   * Constructs a single test for a single test case. Used for parameterised
   * tests.
   *
   * @param testCase single test case.
   */
  public PerEditOperationCorrectnessTest(TestCase testCase) {
    this.testCase = testCase;
  }

  /**
   * Returns a list of test cases read from external JSON file.
   *
   * <p>Uses google.gson for reading JSON document.
   *
   * <p>In case of a failure, the parameter values from {@link TestCase} object
   * are printed '(name = "{0}")'.
   *
   * @return list of all test cases read from JSON file.
   * @throws IOException in case of failure of reading the JSON file.
   */
  @Parameters(name = "{0}")
  public static Collection data() throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(CorrectnessTest.class.getResource("/mini.json").getPath()));
    Gson gson = new Gson();
    TestCase[] testCases = new Gson().fromJson(br, TestCase[].class);
    return Arrays.asList(testCases);
  }

  /**
   * Compute TED for a single test case and compare to the correct value. Uses
   * node labels with a single string value and per-edit-operation cost model.
   *
   * <p>The correct value is calculated using AllPossibleMappingsTED algorithm.
   * <p>The costs of edit operations are set to some example values different
   * than in the unit cost model.
   *
   * @see StringNodeData
   * @see PerEditOperationStringNodeDataCostModel
   * @see AllPossibleMappingsTED
   */
  @Test
  public void distancePerEditOperationStringNodeDataCostModel() {
    // Parse the input.
    BracketStringInputParser parser = new BracketStringInputParser();
    Node<StringNodeData> t1 = parser.fromString(testCase.getT1());
    Node<StringNodeData> t2 = parser.fromString(testCase.getT2());
    // Initialise algorithms.
    APTED<PerEditOperationStringNodeDataCostModel, StringNodeData> apted = new APTED<>(new PerEditOperationStringNodeDataCostModel(0.4f, 0.4f, 0.6f));
    AllPossibleMappingsTED<PerEditOperationStringNodeDataCostModel, StringNodeData> apmted = new AllPossibleMappingsTED<>(new PerEditOperationStringNodeDataCostModel(0.4f, 0.4f, 0.6f));
    // Calculate distances using both algorithms.
    float result = apted.computeEditDistance(t1, t2);
    float correctResult = apmted.computeEditDistance(t1, t2);
    assertEquals(correctResult, result, 0.0001);
  }

}
