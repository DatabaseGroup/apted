// The MIT License (MIT)
// Copyright (c) 2016 Mateusz Pawlik and Nikolaus Augsten
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy 
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights 
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
// copies of the Software, and to permit persons to whom the Software is 
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

// package test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import util.LblTree;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.gson.Gson;

import static org.junit.Assert.assertEquals;
import distance.APTED;

/**
 * Correctness unit tests of distance and mapping computation.
 * In case of mapping, only mapping cost is verified against correct distance.
 * Only unit-cost tests.
 *
 * @author Mateusz Pawlik
 *
 */
@RunWith(Parameterized.class)
public class CorrectnessTest {

  // Path to JSON file with test cases.
  private static final String CORRECTNESS_TESTS_PATH = "correctness_test_cases.json";
  
  // APTED algorithm initialized for each test case.
  private APTED apted = new APTED((float)1.0, (float)1.0, (float)1.0);

  // Test case parameter field holding all test case parameters.
  // Could be also deserialized here but without much benefit.
  private TestCase t;

  // This class represents a single test case from the JSON file.
  // [TODO] Verify if this is the best placement for this class.
  private static class TestCase {
    private int testID;
    private String t1;
    private String t2;
    private int d;

    // Used in printing the test case details on failure with '(name = "{0}")'.
    public String toString() {
      return "testID:" + testID + ",t1:" + t1 + ",t2:" + t2 + ",d:" + d;
    }

    public int getTestID() {
      return testID;
    }
    public String getT1() {
      return t1;
    }
    public String getT2() {
      return t2;
    }
    public int getD() {
      return d;
    }
  }

  // Constructor for parameterized tests.
  public CorrectnessTest(TestCase t) {
    this.t = t;
  }

  // This method returns a list of test cases read from external JSON file.
  // Uses google.gson for reading JSON document.
  @Parameters(name = "{0}")
  public static Collection data() throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(CORRECTNESS_TESTS_PATH));
    Gson gson = new Gson();
    TestCase[] testCases = new Gson().fromJson(br, TestCase[].class);   
    return Arrays.asList(testCases);
  }

  // [TODO] Bring back all tests - each test should be a separate method:
  //        - d(T1, T2)
  //        - d(T2, T1)
  //        - mapping_cost(T1, T2)
  //        - mapping_cost(T2, T1)
  @Test
  public void correctDistanceTest() {
    LblTree t1, t2;
    int result;
    // LinkedList<int[]> mapping;
    t1 = LblTree.fromString(this.t.getT1());
    t2 = LblTree.fromString(this.t.getT2());
    // Distance tests.
    // This cast is safe due to unit cost.
    result = (int)apted.nonNormalizedTreeDist(t1, t2);
    // result = 9; // USED TO TEST FAILURE OUTPUT
    assertEquals(this.t.getD(), result);
    // assertEquals(expected, result, 0.000000001);
  }

  // // Mapping tests.
  //   mapping = ted.computeEditMapping();
  //   result = ted.mappingCost(mapping);
  //   assertEquals(expected, result, 0.000000001);
  //   // Distance test.
  //   result = ted.nonNormalizedTreeDist(t2, t1);
    // assertEquals(expected, result, 0.000000001);   
  //   // Mapping tests.
  //   mapping = ted.computeEditMapping();
  //   result = ted.mappingCost(mapping);
  //   assertEquals(expected, result, 0.000000001);

}