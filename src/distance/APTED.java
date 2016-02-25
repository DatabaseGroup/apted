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

package distance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import util.*;

/**
 * Implements APTED algorithm from [2].
 *
 *  - Optimal strategy with all paths.
 *  - Single-node single path function supports currently only unit cost.
 *  - Two-node single path function not included.
 *  - \Delta^L and \Delta^R based on Zhang and Shasha's algorithm for executing
 *     left and right paths (as in [3]). If only left and right paths are used
 *     in the strategy, the memory usage is reduced by one quadratic array.
 *  - For any other path \Delta^A from [1] is used.
 * 
 *  [1] M. Pawlik and N. Augsten. Efficient Computation of the Tree Edit 
 *      Distance. ACM Transactions on Database Systems (TODS) 40(1). 2015.
 *  [2] M. Pawlik and N. Augsten. Tree edit distance: Robust and memory-
 *      efficient. Information Systems 56. 2016.
 *  [3] M. Pawlik and N. Augsten. RTED: A Robust Algorithm for the Tree Edit 
 *      Distance. PVLDB 5(4). 2011.
 *
 * @author Mateusz Pawlik
 *
 */
public class APTED
{
	private static final byte LEFT = 0;
    private static final byte RIGHT = 1;
    private static final byte INNER = 2;
    private static final byte HEAVY = 2;
    private InfoTree_PLUS it1;
    private InfoTree_PLUS it2;
    private int size1;
    private int size2;
    private LabelDictionary ld;
    private float delta[][];
    public int initSCounter;
    public int initTCounter;
    private float q[];
    int fn[];
    int ft[];
    private long counter;
    private float costDel;
    private float costIns;
    private float costMatch;
    
    public APTED(float delCost, float insCost, float matchCost)
    {
        counter = 0L;
        costDel = delCost;
        costIns = insCost;
        costMatch = matchCost;
    }
    
    public float nonNormalizedTreeDist(LblTree t1, LblTree t2)
    {    	    	        
        // PRECOMPUTATION
        init(t1, t2);
        
        // STRATEGY COMPUTATION with heuristic described in [2]
        if (it1.lchl < it1.rchl) {
        	delta = computeOptStrategyUsingAllPathsOn2_memopt_postL(it1, it2); 
        } else {
        	delta = computeOptStrategyUsingAllPathsOn2_memopt_postR(it1, it2);
        }
               
        // TED COMPUTATION
        tedInit();
        float result = computeDistUsingLRHPathsStrArray(it1, it2);
        
        return result;
    }

    public void init(LblTree t1, LblTree t2)
    {
        ld = new LabelDictionary();
        it1 = new InfoTree_PLUS(t1, ld);
        t1 = null;
        it2 = new InfoTree_PLUS(t2, ld);
        t2 = null;
        size1 = it1.getSize();
        size2 = it2.getSize();
    }

    private void tedInit()
    {    	
    	initSCounter = 0;
    	initTCounter = 0;
        counter = 0L;

        // Initialize arrays.
        int maxSize = Math.max(size1, size2) + 1;
        q = new float[maxSize];
        fn = new int[maxSize + 1];
        ft = new int[maxSize + 1];

        // Compute subtree distances without the root nodes.
        for(int x = 0; x < size1; x++) {
            int sizeX = it1.getSizes(x);
            for(int y = 0; y < size2; y++) {
                int sizeY = it2.getSizes(y);
                if(sizeX == 1 || sizeY == 1) {
                    delta[x][y] = (sizeX - 1) * costDel + (sizeY - 1) * costIns;
                }
            }
        }

    }
 
    public float[][] computeOptStrategyUsingAllPathsOn2_memopt_postL(InfoTree_PLUS it1, InfoTree_PLUS it2) {
    	
    int size1 = it1.getSize();
    int size2 = it2.getSize();
    float strategy[][] = new float[size1][size2];
    float cost1_L[][] = new float[size1][];
    float cost1_R[][] = new float[size1][];
    float cost1_I[][] = new float[size1][];
    float cost2_L[] = new float[size2];
    float cost2_R[] = new float[size2];
    float cost2_I[] = new float[size2];
    int cost2_path[] = new int[size2];
    float leafRow[] = new float[size2];
    int pathIDOffset = size1;
    float minCost = 0x7fffffffffffffffL;
    int strategyPath = -1;
    
    int[] pre2size1 = it1.sizes;
	int[] pre2size2 = it2.sizes;
	int[] pre2descSum1 = it1.preL_to_desc_sum;
	int[] pre2descSum2 = it2.preL_to_desc_sum;
	int[] pre2krSum1 = it1.preL_to_kr_sum;
	int[] pre2krSum2 = it2.preL_to_kr_sum;
	int[] pre2revkrSum1 = it1.preL_to_rev_kr_sum;
	int[] pre2revkrSum2 = it2.preL_to_rev_kr_sum;
	int[] preL_to_preR_1 = it1.preL_to_preR;
	int[] preL_to_preR_2 = it2.preL_to_preR;
	int[] preR_to_preL_1 = it1.preR_to_preL;
	int[] preR_to_preL_2 = it2.preR_to_preL;
	int[] pre2parent1 = it1.parents;
	int[] pre2parent2 = it2.parents;
	
	int[] preL_to_postL_1 = it1.preL_to_postL;
	int[] preL_to_postL_2 = it2.preL_to_postL;
	
	int[] postL_to_preL_1 = it1.postL_to_preL;
	int[] postL_to_preL_2 = it2.postL_to_preL;
	
	int size_v, parent_v_preL, parent_w_preL, parent_w_postL = -1, size_w, parent_v_postL = -1;
	int leftPath_v, rightPath_v;
	float[] cost_Lpointer_v, cost_Rpointer_v, cost_Ipointer_v;
	float[] strategypointer_v;
	float[] cost_Lpointer_parent_v = null, cost_Rpointer_parent_v = null, cost_Ipointer_parent_v = null;
	float[] strategypointer_parent_v = null;
	int krSum_v, revkrSum_v, descSum_v;
	boolean is_v_leaf;
	
	int v_in_preL;
    int w_in_preL;
    
	Stack<float[]> rowsToReuse_L = new Stack<float[]>();
	Stack<float[]> rowsToReuse_R = new Stack<float[]>();
	Stack<float[]> rowsToReuse_I = new Stack<float[]>();
			
    for(int v = 0; v < size1; v++)
    {    	
    	v_in_preL = postL_to_preL_1[v];
    	
    	is_v_leaf = it1.isLeaf(v_in_preL);
		parent_v_preL = pre2parent1[v_in_preL];
		
		if (parent_v_preL != -1) parent_v_postL = preL_to_postL_1[parent_v_preL];
		
		strategypointer_v = strategy[v_in_preL];
		
		size_v = pre2size1[v_in_preL];
		leftPath_v = -(preR_to_preL_1[preL_to_preR_1[v_in_preL] + size_v - 1] + 1);// this is the left path's ID which is the leftmost leaf node: l-r_preorder(r-l_preorder(v) + |Fv| - 1)
		rightPath_v = v_in_preL + size_v - 1 + 1; // this is the right path's ID which is the rightmost leaf node: l-r_preorder(v) + |Fv| - 1
		krSum_v = pre2krSum1[v_in_preL];
		revkrSum_v = pre2revkrSum1[v_in_preL];
		descSum_v = pre2descSum1[v_in_preL];
		
        if(is_v_leaf)
        {
            cost1_L[v] = leafRow;
            cost1_R[v] = leafRow;
            cost1_I[v] = leafRow;
            for(int i = 0; i < size2; i++)
                strategypointer_v[postL_to_preL_2[i]] = v_in_preL;

        }
        
        cost_Lpointer_v = cost1_L[v];
		cost_Rpointer_v = cost1_R[v];
		cost_Ipointer_v = cost1_I[v];
		
        if(parent_v_preL != -1 && cost1_L[parent_v_postL] == null)
        {
			if (rowsToReuse_L.isEmpty()) {
				cost1_L[parent_v_postL] = new float[size2];
				cost1_R[parent_v_postL] = new float[size2];
				cost1_I[parent_v_postL] = new float[size2];
			} else {
				cost1_L[parent_v_postL] = rowsToReuse_L.pop();
				cost1_R[parent_v_postL] = rowsToReuse_R.pop();
				cost1_I[parent_v_postL] = rowsToReuse_I.pop();
			}
            
        }
        
        if (parent_v_preL != -1) {
			cost_Lpointer_parent_v = cost1_L[parent_v_postL];
			cost_Rpointer_parent_v = cost1_R[parent_v_postL];
			cost_Ipointer_parent_v = cost1_I[parent_v_postL];
			strategypointer_parent_v = strategy[parent_v_preL];
		}
        
        Arrays.fill(cost2_L, 0L);
        Arrays.fill(cost2_R, 0L);
        Arrays.fill(cost2_I, 0L);
        Arrays.fill(cost2_path, 0);
        for(int w = 0; w < size2; w++)
        {
        	w_in_preL = postL_to_preL_2[w];
        	
        	parent_w_preL = pre2parent2[w_in_preL];
        	if (parent_w_preL != -1) parent_w_postL = preL_to_postL_2[parent_w_preL];
        	
        	size_w = pre2size2[w_in_preL];
            if(it2.isLeaf(w_in_preL))
            {
                cost2_L[w] = 0L;
                cost2_R[w] = 0L;
                cost2_I[w] = 0L;
                cost2_path[w] = w_in_preL;
            }
            minCost = 0x7fffffffffffffffL;
            strategyPath = -1;
            float tmpCost = 0x7fffffffffffffffL;
            
            if (size_v <= 1 || size_w <= 1) { // USE NEW SINGLE_PATH FUNCTIONS FOR SMALL SUBTREES
            	minCost = Math.max(size_v, size_w);
            } else {
	            tmpCost = (float) size_v * (float) pre2krSum2[w_in_preL] + cost_Lpointer_v[w];
	            if(tmpCost < minCost)
	            {
	                minCost = tmpCost;
	                strategyPath = leftPath_v;
	            }
	            tmpCost = (float) size_v * (float) pre2revkrSum2[w_in_preL] + cost_Rpointer_v[w];
	            if(tmpCost < minCost)
	            {
	                minCost = tmpCost;
	                strategyPath = rightPath_v;
	            }
	            tmpCost = (float) size_v * (float) pre2descSum2[w_in_preL] + cost_Ipointer_v[w];
	            if(tmpCost < minCost)
	            {
	                minCost = tmpCost;
	                strategyPath = (int)strategypointer_v[w_in_preL] + 1;
	            }
	            tmpCost = (float) size_w * (float) krSum_v + cost2_L[w];
	            if(tmpCost < minCost)
	            {
	                minCost = tmpCost;
	                strategyPath = -(preR_to_preL_2[preL_to_preR_2[w_in_preL] + size_w - 1] + pathIDOffset + 1);
	            }
	            tmpCost = (float) size_w * (float) revkrSum_v + cost2_R[w];
	            if(tmpCost < minCost)
	            {
	                minCost = tmpCost;
	                strategyPath = w_in_preL + size_w - 1 + pathIDOffset + 1;
	            }
	            tmpCost = (float) size_w * (float) descSum_v + cost2_I[w];
	            if(tmpCost < minCost)
	            {
	                minCost = tmpCost;
	                strategyPath = cost2_path[w] + pathIDOffset + 1;
	            }
            }
            
            if(parent_v_preL != -1)
            {
                cost_Rpointer_parent_v[w] += minCost;
                tmpCost = -minCost + cost1_I[v][w];
                if(tmpCost < cost1_I[parent_v_postL][w])
                {
                    cost_Ipointer_parent_v[w] = tmpCost;
                    strategypointer_parent_v[w_in_preL] = strategypointer_v[w_in_preL];
                }
                if(it1.ifNodeOfType(v_in_preL, 1))
                {
                    cost_Ipointer_parent_v[w] += cost_Rpointer_parent_v[w];
                    cost_Rpointer_parent_v[w] += cost_Rpointer_v[w] - minCost;
                }
                if(it1.ifNodeOfType(v_in_preL, 0))
                    cost_Lpointer_parent_v[w] += cost_Lpointer_v[w];
                else
                    cost_Lpointer_parent_v[w] += minCost;
            }
            if(parent_w_preL != -1)
            {
                cost2_R[parent_w_postL] += minCost;
                tmpCost = -minCost + cost2_I[w];
                if(tmpCost < cost2_I[parent_w_postL])
                {
                    cost2_I[parent_w_postL] = tmpCost;
                    cost2_path[parent_w_postL] = cost2_path[w];
                }
                if(it2.ifNodeOfType(w_in_preL, 1))
                {
                    cost2_I[parent_w_postL] += cost2_R[parent_w_postL];
                    cost2_R[parent_w_postL] += cost2_R[w] - minCost;
                }
                if(it2.ifNodeOfType(w_in_preL, 0))
                    cost2_L[parent_w_postL] += cost2_L[w];
                else
                    cost2_L[parent_w_postL] += minCost;
            }
            strategypointer_v[w_in_preL] = strategyPath;
        }

        if(!it1.isLeaf(v_in_preL)) {            
            Arrays.fill(cost1_L[v], 0);
			Arrays.fill(cost1_R[v], 0);
			Arrays.fill(cost1_I[v], 0);
			rowsToReuse_L.push(cost1_L[v]);
			rowsToReuse_R.push(cost1_R[v]);
			rowsToReuse_I.push(cost1_I[v]);
        }
        	
    }

    return strategy;
}

    public float[][] computeOptStrategyUsingAllPathsOn2_memopt_postR(InfoTree_PLUS it1, InfoTree_PLUS it2)
    {
        int size1 = it1.getSize();
        int size2 = it2.getSize();
        float strategy[][] = new float[size1][size2];
        float cost1_L[][] = new float[size1][];
        float cost1_R[][] = new float[size1][];
        float cost1_I[][] = new float[size1][];
        float cost2_L[] = new float[size2];
        float cost2_R[] = new float[size2];
        float cost2_I[] = new float[size2];
        int cost2_path[] = new int[size2];
        float leafRow[] = new float[size2];
        int pathIDOffset = size1;
        float minCost = 0x7fffffffffffffffL;
        int strategyPath = -1;
        
        int[] pre2size1 = it1.sizes;
		int[] pre2size2 = it2.sizes;
		int[] pre2descSum1 = it1.preL_to_desc_sum;
		int[] pre2descSum2 = it2.preL_to_desc_sum;
		int[] pre2krSum1 = it1.preL_to_kr_sum;
		int[] pre2krSum2 = it2.preL_to_kr_sum;
		int[] pre2revkrSum1 = it1.preL_to_rev_kr_sum;
		int[] pre2revkrSum2 = it2.preL_to_rev_kr_sum;
		int[] preL_to_preR_1 = it1.preL_to_preR;
		int[] preL_to_preR_2 = it2.preL_to_preR;
		int[] preR_to_preL_1 = it1.preR_to_preL;
		int[] preR_to_preL_2 = it2.preR_to_preL;
		int[] pre2parent1 = it1.parents;
		int[] pre2parent2 = it2.parents;
		
		int size_v, parent_v, parent_w, size_w;
		int leftPath_v, rightPath_v;
		float[] cost_Lpointer_v, cost_Rpointer_v, cost_Ipointer_v;
		float[] strategypointer_v;
		float[] cost_Lpointer_parent_v = null, cost_Rpointer_parent_v = null, cost_Ipointer_parent_v = null;
		float[] strategypointer_parent_v = null;
		int krSum_v, revkrSum_v, descSum_v;
		boolean is_v_leaf;
		
		Stack<float[]> rowsToReuse_L = new Stack<float[]>();
		Stack<float[]> rowsToReuse_R = new Stack<float[]>();
		Stack<float[]> rowsToReuse_I = new Stack<float[]>();
        		
        for(int v = size1 - 1; v >= 0; v--)
        {
        	is_v_leaf = it1.isLeaf(v);
			parent_v = pre2parent1[v];
			
			strategypointer_v = strategy[v];
			
			size_v = pre2size1[v];
			leftPath_v = -(preR_to_preL_1[preL_to_preR_1[v] + pre2size1[v] - 1] + 1);// this is the left path's ID which is the leftmost leaf node: l-r_preorder(r-l_preorder(v) + |Fv| - 1)
			rightPath_v = v + pre2size1[v] - 1 + 1; // this is the right path's ID which is the rightmost leaf node: l-r_preorder(v) + |Fv| - 1
			krSum_v = pre2krSum1[v];
			revkrSum_v = pre2revkrSum1[v];
			descSum_v = pre2descSum1[v];
			
            if(is_v_leaf)
            {
                cost1_L[v] = leafRow;
                cost1_R[v] = leafRow;
                cost1_I[v] = leafRow;
                for(int i = 0; i < size2; i++)
                    strategypointer_v[i] = v;

            }
            
            cost_Lpointer_v = cost1_L[v];
			cost_Rpointer_v = cost1_R[v];
			cost_Ipointer_v = cost1_I[v];
			
            if(parent_v != -1 && cost1_L[parent_v] == null)
            {
                if (rowsToReuse_L.isEmpty()) {
    				cost1_L[parent_v] = new float[size2];
    				cost1_R[parent_v] = new float[size2];
    				cost1_I[parent_v] = new float[size2];
    			} else {
    				cost1_L[parent_v] = rowsToReuse_L.pop();
    				cost1_R[parent_v] = rowsToReuse_R.pop();
    				cost1_I[parent_v] = rowsToReuse_I.pop();
    			}
                
            }
            
            if (parent_v != -1) {
				cost_Lpointer_parent_v = cost1_L[parent_v];
				cost_Rpointer_parent_v = cost1_R[parent_v];
				cost_Ipointer_parent_v = cost1_I[parent_v];
				strategypointer_parent_v = strategy[parent_v];
			}
            
            Arrays.fill(cost2_L, 0L);
            Arrays.fill(cost2_R, 0L);
            Arrays.fill(cost2_I, 0L);
            Arrays.fill(cost2_path, 0);
            for(int w = size2 - 1; w >= 0; w--)
            {
            	size_w = pre2size2[w];
                if(it2.isLeaf(w))
                {
                    cost2_L[w] = 0L;
                    cost2_R[w] = 0L;
                    cost2_I[w] = 0L;
                    cost2_path[w] = w;
                }
                minCost = 0x7fffffffffffffffL;
                strategyPath = -1;
                float tmpCost = 0x7fffffffffffffffL;
                
                if (size_v <= 2 || size_w <= 2) { // USE NEW SINGLE_PATH FUNCTIONS FOR SMALL SUBTREES
                	minCost = Math.max(size_v, size_w);
                } else {
	                tmpCost = (float) size_v * (float) pre2krSum2[w] + cost_Lpointer_v[w];
	                if(tmpCost < minCost)
	                {
	                    minCost = tmpCost;
	                    strategyPath = leftPath_v;
	                }
	                tmpCost = (float) size_v * (float) pre2revkrSum2[w] + cost_Rpointer_v[w];
	                if(tmpCost < minCost)
	                {
	                    minCost = tmpCost;
	                    strategyPath = rightPath_v;
	                }
	                tmpCost = (float) size_v * (float) pre2descSum2[w] + cost_Ipointer_v[w];
	                if(tmpCost < minCost)
	                {
	                    minCost = tmpCost;
	                    strategyPath = (int)strategypointer_v[w] + 1;
	                }
	                tmpCost = (float) size_w * (float) krSum_v + cost2_L[w];
	                if(tmpCost < minCost)
	                {
	                    minCost = tmpCost;
	                    strategyPath = -(preR_to_preL_2[preL_to_preR_2[w] + size_w - 1] + pathIDOffset + 1);
	                }
	                tmpCost = (float) size_w * (float) revkrSum_v + cost2_R[w];
	                if(tmpCost < minCost)
	                {
	                    minCost = tmpCost;
	                    strategyPath = w + size_w - 1 + pathIDOffset + 1;
	                }
	                tmpCost = (float) size_w * (float) descSum_v + cost2_I[w];
	                if(tmpCost < minCost)
	                {
	                    minCost = tmpCost;
	                    strategyPath = cost2_path[w] + pathIDOffset + 1;
	                }
                }
                
                if(parent_v != -1)
                {
                    cost_Lpointer_parent_v[w] += minCost;
                    tmpCost = -minCost + cost1_I[v][w];
                    if(tmpCost < cost1_I[parent_v][w])
                    {
                        cost_Ipointer_parent_v[w] = tmpCost;
                        strategypointer_parent_v[w] = strategypointer_v[w];
                    }
                    if(it1.ifNodeOfType(v, 0))
                    {
                        cost_Ipointer_parent_v[w] += cost_Lpointer_parent_v[w];
                        cost_Lpointer_parent_v[w] += cost_Lpointer_v[w] - minCost;
                    }
                    if(it1.ifNodeOfType(v, 1))
                        cost_Rpointer_parent_v[w] += cost_Rpointer_v[w];
                    else
                        cost_Rpointer_parent_v[w] += minCost;
                }
                parent_w = pre2parent2[w];
                if(parent_w != -1)
                {
                    cost2_L[parent_w] += minCost;
                    tmpCost = -minCost + cost2_I[w];
                    if(tmpCost < cost2_I[parent_w])
                    {
                        cost2_I[parent_w] = tmpCost;
                        cost2_path[parent_w] = cost2_path[w];
                    }
                    if(it2.ifNodeOfType(w, 0))
                    {
                        cost2_I[parent_w] += cost2_L[parent_w];
                        cost2_L[parent_w] += cost2_L[w] - minCost;
                    }
                    if(it2.ifNodeOfType(w, 1))
                        cost2_R[parent_w] += cost2_R[w];
                    else
                        cost2_R[parent_w] += minCost;
                }
                strategypointer_v[w] = strategyPath;
            }

            if(!it1.isLeaf(v)) {            	
                Arrays.fill(cost1_L[v], 0);
    			Arrays.fill(cost1_R[v], 0);
    			Arrays.fill(cost1_I[v], 0);
    			rowsToReuse_L.push(cost1_L[v]);
    			rowsToReuse_R.push(cost1_R[v]);
    			rowsToReuse_I.push(cost1_I[v]);
        	}
        }

        return strategy;
    }
    
    private float computeDistUsingLRHPathsStrArray(InfoTree_PLUS it1, InfoTree_PLUS it2)
    {
    	int currentSubtree1 = it1.getCurrentNode();
        int currentSubtree2 = it2.getCurrentNode();
        int subtreeSize1 = it1.getSizes(currentSubtree1);
        int subtreeSize2 = it2.getSizes(currentSubtree2);
    	
        // SINGLE-NODE SUBTREE
		if ((subtreeSize1 == 1 || subtreeSize2 == 1)) {

			if (it1.getCurrentNode() > 0 || it2.getCurrentNode() > 0) {
				return -1;
			} else {
				float result = Math.max(it1.getSizes(currentSubtree1), it2.getSizes(currentSubtree2));
				boolean matchFound = false;
				
				for (int i = currentSubtree1; i < currentSubtree1 + it1.sizes[currentSubtree1]; i++) {
					for (int j = currentSubtree2; j < currentSubtree2 + it2.sizes[currentSubtree2]; j++) {
						if (!matchFound)
							matchFound = it1.labels[i] == it2.labels[j];
						counter++;
					}
				}
                // TODO: modify to custom costs
                // unit cost only
				return result += (matchFound ? -1.0D : 0.0D); 
			}
		}
		// END
		    	
        int strategyPathID = (int)delta[currentSubtree1][currentSubtree2];
                
        byte strategyPathType = -1;
        int currentPathNode = Math.abs(strategyPathID) - 1;
        int pathIDOffset = it1.getSize();
                
        int parent = -1;
        if(currentPathNode < pathIDOffset)
        {            
            strategyPathType = getStrategyPathType(strategyPathID, pathIDOffset, it1, currentSubtree1, subtreeSize1);
            while((parent = it1.getParents(currentPathNode)) >= currentSubtree1) 
            {
                int ai[];
                int k = (ai = it1.getChildren(parent)).length;
                for(int i = 0; i < k; i++)
                {
                    int child = ai[i];
                    if(child != currentPathNode)
                    {
                        it1.setCurrentNode(child);
                        computeDistUsingLRHPathsStrArray(it1, it2);
                    }
                }

                currentPathNode = parent;
            }
            it1.setCurrentNode(currentSubtree1);
            it1.setSwitched(false);
            it2.setSwitched(false);
                        
            if (strategyPathType == 0) {
            	return spfL(it1, it2);
            }
            if (strategyPathType == 1) {
            	return spfR(it1, it2);
            }
            return spfWithPathID_opt_mem(it1, it2, Math.abs(strategyPathID) - 1, strategyPathType);
        }
                    
        currentPathNode -= pathIDOffset;
        strategyPathType = getStrategyPathType(strategyPathID, pathIDOffset, it2, currentSubtree2, subtreeSize2);
        while((parent = it2.getParents(currentPathNode)) >= currentSubtree2) 
        {        	
        	int ai1[];
            int l = (ai1 = it2.getChildren(parent)).length;
            for(int j = 0; j < l; j++)
            {
                int child = ai1[j];
                if(child != currentPathNode)
                {
                    it2.setCurrentNode(child);
                    computeDistUsingLRHPathsStrArray(it1, it2);
                }
            }

            currentPathNode = parent;
        }
        it2.setCurrentNode(currentSubtree2);
        it1.setSwitched(true);
        it2.setSwitched(true);
              
        if (strategyPathType == 0) {
        	return spfL(it2, it1);
        }
        if (strategyPathType == 1) {
        	return spfR(it2, it1);
        }
        return spfWithPathID_opt_mem(it2, it1, Math.abs(strategyPathID) - pathIDOffset - 1, strategyPathType);
    }

    
    private float spfWithPathID_opt_mem(InfoTree_PLUS it1, InfoTree_PLUS it2, int pathID, byte pathType)
    {    	
    	boolean treesSwitched = it1.isSwitched();
    	    	
    	int[] it2labels = it2.labels;
    	int[] it2sizes = it2.sizes;
    	
        int currentSubtreePreL1 = it1.getCurrentNode();
        int currentSubtreePreL2 = it2.getCurrentNode();
                
        int currentForestSize1 = 0;
        int currentForestSize2 = 0;
        int tmpForestSize1 = 0;
        int subtreeSize2 = it2.getSizes(currentSubtreePreL2);
        int subtreeSize1 = it1.getSizes(currentSubtreePreL1);
        
        float[][] t = new float[subtreeSize2+1][subtreeSize2+1];
        float[][] s = new float[subtreeSize1+1][subtreeSize2+1];

        float minCost = -1;
        float sp1 = 0;
        float sp2 = 0;
        float sp3 = 0;
        int startPathNode = -1;
        int endPathNode = pathID;
        int it1PreLoff = endPathNode;
        int it2PreLoff = currentSubtreePreL2;
        int it1PreRoff = it1.getPreL_to_PreR(endPathNode);
        int it2PreRoff = it2.getPreL_to_PreR(it2PreLoff);
        
        // variable declarations which were inside the loops
        int rFlast,lFlast,endPathNode_in_preR,startPathNode_in_preR,parent_of_endPathNode,parent_of_endPathNode_in_preR,
        lFfirst,rFfirst,rGlast,rGfirst,lGfirst,rG_in_preL,rGminus1_in_preL,parent_of_rG_in_preL,lGlast,lF_in_preR,lFSubtreeSize,lFLabel,
        lGminus1_in_preR,parent_of_lG,parent_of_lG_in_preR,rF_in_preL,rFSubtreeSize,
        rGfirst_in_preL;
        boolean leftPart,rightPart,fForestIsTree,lFIsConsecutiveNodeOfCurrentPathNode,lFIsLeftSiblingOfCurrentPathNode,
        rFIsConsecutiveNodeOfCurrentPathNode,rFIsRightSiblingOfCurrentPathNode;
        float[] sp1spointer,sp2spointer,sp3spointer,sp3deltapointer,swritepointer,sp1tpointer,sp3tpointer;
        byte sp1source,sp3source;
        
        for(; endPathNode >= currentSubtreePreL1; endPathNode = it1.getParents(endPathNode))
        {
        	it1PreLoff = endPathNode;
            it1PreRoff = it1.getPreL_to_PreR(endPathNode);
            
            rFlast = -1;
            lFlast = -1;
            endPathNode_in_preR = it1.getPreL_to_PreR(endPathNode);
            startPathNode_in_preR = startPathNode == -1 ? 0x7fffffff : it1.getPreL_to_PreR(startPathNode);
            parent_of_endPathNode = it1.getParents(endPathNode);
            parent_of_endPathNode_in_preR = parent_of_endPathNode == -1 ? 0x7fffffff : it1.getPreL_to_PreR(parent_of_endPathNode);
            
            
            if(startPathNode - endPathNode > 1) {
                leftPart = true;
            } else {
            	leftPart = false;
            }
            if(startPathNode >= 0 && startPathNode_in_preR - endPathNode_in_preR > 1) {
                rightPart = true;
            } else {
            	rightPart = false;
            }
            if(pathType == 1 || pathType == 2 && leftPart)
            {
                if(startPathNode == -1)
                {
                    rFfirst = endPathNode_in_preR;
                    lFfirst = endPathNode;
                } else
                {
                    rFfirst = startPathNode_in_preR;
                    lFfirst = startPathNode - 1;
                }
                if(!rightPart)
                    rFlast = endPathNode_in_preR;
                rGlast = it2.getPreL_to_PreR(currentSubtreePreL2);
                rGfirst = (rGlast + subtreeSize2) - 1;
                lFlast = rightPart ? endPathNode + 1 : endPathNode;
                fn[fn.length - 1] = -1;
                for(int i = currentSubtreePreL2; i < currentSubtreePreL2 + subtreeSize2; i++)
                {
                    fn[i] = -1;
                    ft[i] = -1;
                }

                tmpForestSize1 = currentForestSize1;
                for(int rG = rGfirst; rG >= rGlast; rG--)
                {
                    lGfirst = it2.getPreR_to_PreL(rG);
                    rG_in_preL = it2.getPreR_to_PreL(rG);
                    rGminus1_in_preL = rG <= it2.getPreL_to_PreR(currentSubtreePreL2) ? 0x7fffffff : it2.getPreR_to_PreL(rG - 1);
                    parent_of_rG_in_preL = it2.getParents(rG_in_preL);
                    if(pathType == 1)
                    {
                        if (lGfirst == currentSubtreePreL2 || rGminus1_in_preL != parent_of_rG_in_preL) lGlast = lGfirst;
                        else lGlast = it2.getParents(lGfirst)+1;
                    } else
                    {
                    	lGlast = lGfirst == currentSubtreePreL2 ? lGfirst : currentSubtreePreL2+1;
                    }
                    updateFnArray(it2.getPreL_to_LN(lGfirst), lGfirst, currentSubtreePreL2);
                    updateFtArray(it2.getPreL_to_LN(lGfirst), lGfirst);
                    int rF = rFfirst;
                    currentForestSize1 = tmpForestSize1;
                    
                    for(int lF = lFfirst; lF >= lFlast; lF--)
                    {
                        if(lF == lFlast && !rightPart) rF = rFlast;
                        currentForestSize1++;
                        currentForestSize2 = it2.getSizes(lGfirst) - 1;
                        lF_in_preR = it1.getPreL_to_PreR(lF);
                        fForestIsTree = lF_in_preR == rF;
                        lFSubtreeSize = it1.getSizes(lF);
                        lFLabel = it1.getLabels(lF);
                        lFIsConsecutiveNodeOfCurrentPathNode = startPathNode - lF == 1;
                        lFIsLeftSiblingOfCurrentPathNode = lF + lFSubtreeSize == startPathNode;
                        
                        sp1spointer = s[(lF + 1) - it1PreLoff];
                        sp2spointer = s[lF - it1PreLoff];
                        sp3spointer = s[0];
                        sp3deltapointer = treesSwitched ? null : delta[lF];
                        swritepointer = s[lF - it1PreLoff];
                                               
                        sp1source = 1;
                        sp3source = 1;
                        if(fForestIsTree) {
                            if(lFSubtreeSize == 1) sp1source = 3;
                            else if(lFIsConsecutiveNodeOfCurrentPathNode) sp1source = 2;
                            sp3 = 0;
                            sp3source = 2;
                        } else {
                            if(lFIsConsecutiveNodeOfCurrentPathNode) sp1source = 2;
                            sp3 = currentForestSize1 - lFSubtreeSize * costDel;
                            if(lFIsLeftSiblingOfCurrentPathNode) sp3source = 3;
                        }
                        
                        if (sp3source == 1) sp3spointer = s[(lF + lFSubtreeSize) - it1PreLoff];
                        
                        if(currentForestSize2 + 1 == 1) sp2 = currentForestSize1 * costDel;
                        else sp2 = q[lF];
                        int lG = lGfirst;
                        currentForestSize2++;
                        
                        switch(sp1source) {
                        	case 1: sp1 = sp1spointer[lG - it2PreLoff]; break;
                        	case 2: sp1 = t[lG - it2PreLoff][rG - it2PreRoff]; break;
                        	case 3: sp1 = currentForestSize2 * costIns; break;
                        }
                        sp1 += costDel;
                        minCost = sp1;
                        sp2 += costIns;
                        if(sp2 < minCost) minCost = sp2;
                        
                        if (sp3 < minCost) {
                        	sp3 += treesSwitched ? delta[lG][lF] : sp3deltapointer[lG];
                        	if (sp3 < minCost) {
                        		if (lFLabel != it2labels[lG]) sp3 += costMatch;
                                if(sp3 < minCost) minCost = sp3;
                        	}
                        }
                        
                        swritepointer[lG - it2PreLoff] = minCost;
                        lG = ft[lG];
                        counter++;
                                                               
                        while (lG >= lGlast) {
                            currentForestSize2++;
                            switch(sp1source) {
                            	case 1: sp1 = sp1spointer[lG - it2PreLoff] + costDel; break;
                            	case 2: sp1 = t[lG - it2PreLoff][rG - it2PreRoff] + costDel; break;
                            	case 3: sp1 = currentForestSize2 * costIns + costDel; break;
                            }
                            sp2 = sp2spointer[fn[lG] - it2PreLoff] + costDel;
                            
                            minCost = sp1;
                            if(sp2 < minCost) minCost = sp2;
                                
                            sp3 = treesSwitched ? delta[lG][lF] : sp3deltapointer[lG];
                            if (sp3 < minCost) {
                            	switch(sp3source) {
                                	case 1: sp3 += sp3spointer[fn[(lG + it2sizes[lG]) - 1] - it2PreLoff]; break;
                                	case 2: sp3 += (currentForestSize2 - it2sizes[lG]) * costIns; break;
                                	case 3: sp3 += t[fn[(lG + it2sizes[lG]) - 1] - it2PreLoff][rG - it2PreRoff]; break;
                                }
                            	if (sp3 < minCost) {
                            		if (lFLabel != it2labels[lG]) sp3 += costMatch;
                            		if(sp3 < minCost) minCost = sp3;
                            	}
                            }                       
                            swritepointer[lG - it2PreLoff] = minCost;
                            lG = ft[lG];
                            counter++;
                        }

                    }
                    
                    if(rGminus1_in_preL == parent_of_rG_in_preL)
                    {
                        if(!rightPart)
                        {
                            if(leftPart) {
                                if(treesSwitched) {
                                    delta[parent_of_rG_in_preL][endPathNode] = s[(lFlast + 1) - it1PreLoff][(rGminus1_in_preL + 1) - it2PreLoff];
                                } else {
                                    delta[endPathNode][parent_of_rG_in_preL] = s[(lFlast + 1) - it1PreLoff][(rGminus1_in_preL + 1) - it2PreLoff];
                                }
                            }
                            if(endPathNode > 0 && endPathNode == parent_of_endPathNode + 1 && endPathNode_in_preR == parent_of_endPathNode_in_preR + 1) {
                                if(treesSwitched) {
                                    delta[parent_of_rG_in_preL][parent_of_endPathNode] = s[lFlast - it1PreLoff][(rGminus1_in_preL + 1) - it2PreLoff];
                                } else {
                                    delta[parent_of_endPathNode][parent_of_rG_in_preL] = s[lFlast - it1PreLoff][(rGminus1_in_preL + 1) - it2PreLoff];
                                }
                            }
                        }
                        for(int lF = lFfirst; lF >= lFlast; lF--) {
                            q[lF] = s[lF - it1PreLoff][(parent_of_rG_in_preL + 1) - it2PreLoff];
                        }
                    }
                    // TODO: first pointers can be precomputed
                    for(int lG = lGfirst; lG >= lGlast; lG = ft[lG]) {
                        t[lG - it2PreLoff][rG - it2PreRoff] = s[lFlast - it1PreLoff][lG - it2PreLoff];
                    }
                }
            }
            if(pathType == 0 || pathType == 2 && rightPart || pathType == 2 && !leftPart && !rightPart)
            {
                if(startPathNode == -1)
                {
                    lFfirst = endPathNode;
                    rFfirst = it1.getPreL_to_PreR(endPathNode);
                } else
                {
                    rFfirst = it1.getPreL_to_PreR(startPathNode) - 1;
                    lFfirst = endPathNode + 1;
                }
                lFlast = endPathNode;
                lGlast = currentSubtreePreL2;
                lGfirst = (lGlast + subtreeSize2) - 1;
                rFlast = it1.getPreL_to_PreR(endPathNode);
                fn[fn.length - 1] = -1;
                for(int i = currentSubtreePreL2; i < currentSubtreePreL2 + subtreeSize2; i++)
                {
                    fn[i] = -1;
                    ft[i] = -1;
                }

                tmpForestSize1 = currentForestSize1;
                for(int lG = lGfirst; lG >= lGlast; lG--)
                {
                    rGfirst = it2.getPreL_to_PreR(lG);
                    updateFnArray(it2.getPreR_to_LN(rGfirst), rGfirst, it2.getPreL_to_PreR(currentSubtreePreL2));
                    updateFtArray(it2.getPreR_to_LN(rGfirst), rGfirst);
                    int lF = lFfirst;
                    lGminus1_in_preR = lG <= currentSubtreePreL2 ? 0x7fffffff : it2.getPreL_to_PreR(lG - 1);
                    parent_of_lG = it2.getParents(lG);
                    parent_of_lG_in_preR = parent_of_lG == -1 ? -1 : it2.getPreL_to_PreR(parent_of_lG);
                    currentForestSize1 = tmpForestSize1;
                    if(pathType == 0) {
                        if (lG == currentSubtreePreL2) rGlast = rGfirst;
                        else if(it2.getChildren(parent_of_lG)[0] != lG) rGlast = rGfirst;
                        else rGlast = it2.getPreL_to_PreR(parent_of_lG)+1;
                    } else {
                        rGlast = rGfirst == it2.getPreL_to_PreR(currentSubtreePreL2) ? rGfirst : it2.getPreL_to_PreR(currentSubtreePreL2);
                    }
                    
                    for(int rF = rFfirst; rF >= rFlast; rF--)
                    {
                        if(rF == rFlast)
                            lF = lFlast;
                        currentForestSize1++;
                        currentForestSize2 = it2.getSizes(lG) - 1;
                        rF_in_preL = it1.getPreR_to_PreL(rF);
                        rFSubtreeSize = it1.getSizes(rF_in_preL);
                        if(startPathNode > 0) {
                            rFIsConsecutiveNodeOfCurrentPathNode = startPathNode_in_preR - rF == 1;
                            rFIsRightSiblingOfCurrentPathNode = rF + rFSubtreeSize == startPathNode_in_preR;
                        } else {
                            rFIsConsecutiveNodeOfCurrentPathNode = false;
                            rFIsRightSiblingOfCurrentPathNode = false;
                        }
                        fForestIsTree = rF_in_preL == lF;
                        int rFLabel = it1.getLabels(rF_in_preL);
                        
                        sp1spointer = s[(rF + 1) - it1PreRoff];
                        sp2spointer = s[rF - it1PreRoff];
                        sp3spointer = s[0];
                        sp3deltapointer = treesSwitched ? null : delta[rF_in_preL];
                        swritepointer = s[rF - it1PreRoff];
                        sp1tpointer = t[lG - it2PreLoff];
                        sp3tpointer = t[lG - it2PreLoff];
                        
                        sp1source = 1;
                        sp3source = 1;
                        if(fForestIsTree) {
                            if (rFSubtreeSize == 1) sp1source = 3;
                            else if (rFIsConsecutiveNodeOfCurrentPathNode) sp1source = 2;
                            sp3 = 0;
                            sp3source = 2;
                        } else {
                            if (rFIsConsecutiveNodeOfCurrentPathNode) sp1source = 2;
                            sp3 = currentForestSize1 - rFSubtreeSize * costDel;
                            if (rFIsRightSiblingOfCurrentPathNode) sp3source = 3;
                        }
                        
                        if (sp3source == 1) sp3spointer = s[(rF + rFSubtreeSize) - it1PreRoff];
                        
                        if (currentForestSize2 + 1 == 1) sp2 = currentForestSize1 * costDel;
                        else sp2 = q[rF];
                        
                        int rG = rGfirst;
                        rGfirst_in_preL = it2.getPreR_to_PreL(rGfirst);
                        
                        currentForestSize2++;
                        
                        switch(sp1source) {
                        	case 1: sp1 = sp1spointer[rG - it2PreRoff]; break;
                        	case 2: sp1 = sp1tpointer[rG - it2PreRoff]; break;
                        	case 3: sp1 = currentForestSize2 * costIns; break;
                        }
                        sp1 += costDel;
                        minCost = sp1;
                        sp2 += costIns;
                        if(sp2 < minCost) minCost = sp2;
                        
                        if (sp3 < minCost) {
                        	sp3 += treesSwitched ? delta[rGfirst_in_preL][rF_in_preL] : sp3deltapointer[rGfirst_in_preL];
                        	if (sp3 < minCost) {
                        		if (rFLabel != it2labels[rGfirst_in_preL]) sp3 += costMatch;
                                if (sp3 < minCost) minCost = sp3;
                        	}
                        }
                        
                        swritepointer[rG - it2PreRoff] = minCost;
                        rG = ft[rG];
                        counter++;
                                                                        
                        while (rG >= rGlast) {
                            currentForestSize2++;
                            rG_in_preL = it2.getPreR_to_PreL(rG);
                            switch(sp1source) {
                            	case 1: sp1 = sp1spointer[rG - it2PreRoff] + costDel; break;
                            	case 2: sp1 = sp1tpointer[rG - it2PreRoff] + costDel; break;
                            	case 3: sp1 = currentForestSize2 * costIns + costDel; break;
                            }
                            sp2 = sp2spointer[fn[rG] - it2PreRoff] + costIns;
                            
                            minCost = sp1;
                            if(sp2 < minCost) minCost = sp2;
                             
                            sp3 = treesSwitched ? delta[rG_in_preL][rF_in_preL] : sp3deltapointer[rG_in_preL];
                            if (sp3 < minCost) {
                            	switch(sp3source) {
                                	case 1: sp3 += sp3spointer[fn[(rG + it2sizes[rG_in_preL]) - 1] - it2PreRoff]; break;
                                	case 2: sp3 += (currentForestSize2 - it2sizes[rG_in_preL]) * costIns; break;
                                	case 3: sp3 += sp3tpointer[fn[(rG + it2sizes[rG_in_preL]) - 1] - it2PreRoff]; break;
                                }
                            	
                            	if (sp3 < minCost) {
                            		if (rFLabel != it2labels[rG_in_preL]) sp3 += costMatch;
    	                            if(sp3 < minCost) minCost = sp3;
                            	}
                            }
                                                        
                            swritepointer[rG - it2PreRoff] = minCost;
                            rG = ft[rG];
                            counter++;
                        }

                    }
                    
                    if(lG > currentSubtreePreL2 && lG - 1 == parent_of_lG)
                    {
                        if(rightPart) {
                            if(treesSwitched) { 
                                delta[parent_of_lG][endPathNode] = s[(rFlast + 1) - it1PreRoff][(lGminus1_in_preR + 1) - it2PreRoff];
                            } else {
                                delta[endPathNode][parent_of_lG] = s[(rFlast + 1) - it1PreRoff][(lGminus1_in_preR + 1) - it2PreRoff];
                            }
                        }
                        if(endPathNode > 0 && endPathNode == parent_of_endPathNode + 1 && endPathNode_in_preR == parent_of_endPathNode_in_preR + 1)
                            if(treesSwitched) {
                                delta[parent_of_lG][parent_of_endPathNode] = s[rFlast - it1PreRoff][(lGminus1_in_preR + 1) - it2PreRoff];
                            } else {
                                delta[parent_of_endPathNode][parent_of_lG] = s[rFlast - it1PreRoff][(lGminus1_in_preR + 1) - it2PreRoff];
                            }
                        for (int rF = rFfirst; rF >= rFlast; rF--) {
                            q[rF] = s[rF - it1PreRoff][(parent_of_lG_in_preR + 1) - it2PreRoff];
                        }
                    }
                 // TODO: first pointers can be precomputed
                    for (int rG = rGfirst; rG >= rGlast; rG = ft[rG]) {
                        t[lG - it2PreLoff][rG - it2PreRoff] = s[rFlast - it1PreRoff][rG - it2PreRoff];
                    }
                }
            }
            startPathNode = endPathNode;
        }
        
        return minCost;
    }
    
    // ===================== BEGIN spfL
    private float spfL(InfoTree_PLUS it1, InfoTree_PLUS it2) {
    	    	    	
    	int[] keyRoots = new int[it2.sizes[it2.getCurrentNode()]];
    	
    	Arrays.fill(keyRoots, -1);
    	
    	int pathID = it2.preR_to_preL[it2.preL_to_preR[it2.getCurrentNode()] + it2.sizes[it2.getCurrentNode()] - 1];
    	int firstKeyRoot = computeKeyRoots(it2, it2.getCurrentNode(), pathID, keyRoots, 0);
    	   
    	float[][] forestdist = new float[it1.sizes[it1.getCurrentNode()]+1][it2.sizes[it2.getCurrentNode()]+1];
    	 	
    	for (int i = firstKeyRoot-1; i >= 0; i--) {
    		treeEditDist(it1, it2, it1.getCurrentNode(), keyRoots[i], forestdist);
    	}
    	
    	return forestdist[it1.sizes[it1.getCurrentNode()]][it2.sizes[it2.getCurrentNode()]];
    }
    private int computeKeyRoots(InfoTree_PLUS it2, int subtreeRootNode, int pathID, int[] keyRoots, int index) {
    	
    	keyRoots[index] = subtreeRootNode;
    	index++;
    	    
    	int pathNode = pathID;
    	while (pathNode > subtreeRootNode) {
    		int parent = it2.parents[pathNode];
    		for (int child : it2.getChildren(parent)) {
    			if (child != pathNode) index = computeKeyRoots(it2, child, it2.preR_to_preL[it2.preL_to_preR[child]+it2.sizes[child]-1], keyRoots, index);
    		}
    		pathNode = parent;
    	}
    	
    	return index;
    } 
    private int getLLD(InfoTree_PLUS it, int postorder) {
    	int preL = it.postL_to_preL[postorder];
    	return it.preL_to_postL[it.preR_to_preL[it.preL_to_preR[preL] + it.sizes[preL] - 1]];
    }
    private void treeEditDist(InfoTree_PLUS it1, InfoTree_PLUS it2, int it1subtree, int it2subtree, float[][] forestdist) {
    	
    	// i,j have to be in postorder
    	int i = it1.preL_to_postL[it1subtree];
    	int j = it2.preL_to_postL[it2subtree];
    	
    	int ioff = getLLD(it1, i) - 1;
        int joff = getLLD(it2, j) - 1;
        float da = 0;
        float db = 0;
        float dc = 0;
        boolean switched = it1.isSwitched();
        forestdist[0][0] = 0;
        for(int i1 = 1; i1 <= i - ioff; i1++)
            forestdist[i1][0] = forestdist[i1 - 1][0] + 1;

        for(int j1 = 1; j1 <= j - joff; j1++)
            forestdist[0][j1] = forestdist[0][j1 - 1] + 1;

        for(int i1 = 1; i1 <= i - ioff; i1++)
        {
            for(int j1 = 1; j1 <= j - joff; j1++)
            {
                counter++;
                float u = 0;
                if(it1.labels[it1.postL_to_preL[i1 + ioff]] != it2.labels[it2.postL_to_preL[j1 + joff]]) u = costMatch;
                
                if(getLLD(it1,i1 + ioff) == getLLD(it1,i) && getLLD(it2,j1 + joff) == getLLD(it2,j))
                {
                    da = forestdist[i1 - 1][j1] + costDel;
                    db = forestdist[i1][j1 - 1] + costIns;
                    dc = forestdist[i1 - 1][j1 - 1] + u;
                    forestdist[i1][j1] = da >= db ? db >= dc ? dc : db : da >= dc ? dc : da;
                    if (switched) {
                    	delta[it2.postL_to_preL[j1+joff]][it1.postL_to_preL[i1+ioff]] = forestdist[i1 - 1][j1 - 1];
                    } else {
                    	delta[it1.postL_to_preL[i1+ioff]][it2.postL_to_preL[j1+joff]] = forestdist[i1 - 1][j1 - 1];
                    }
                } else
                {
                    da = forestdist[i1 - 1][j1] + costDel;
                    db = forestdist[i1][j1 - 1] + costIns;
                    dc = forestdist[getLLD(it1,i1 + ioff) - 1 - ioff][getLLD(it2,j1 + joff) - 1 - joff] + 
                    		(switched ? delta[it2.postL_to_preL[j1 + joff]][it1.postL_to_preL[i1 + ioff]] : delta[it1.postL_to_preL[i1 + ioff]][it2.postL_to_preL[j1 + joff]]) + u;
                    forestdist[i1][j1] = da >= db ? db >= dc ? dc : db : da >= dc ? dc : da;
                }
            }

        }
    }
 	// ===================== END spfL
    
    // ===================== BEGIN spfR
    private float spfR(InfoTree_PLUS it1, InfoTree_PLUS it2) {
    	    	    	
    	int[] revKeyRoots = new int[it2.sizes[it2.getCurrentNode()]];
    	    	
    	Arrays.fill(revKeyRoots, -1);
    	
    	int pathID = it2.getCurrentNode() + it2.sizes[it2.getCurrentNode()] - 1; // in r-l preorder
    	int firstKeyRoot = computeRevKeyRoots(it2, it2.getCurrentNode(), pathID, revKeyRoots, 0);
    	   
    	float[][] forestdist = new float[it1.sizes[it1.getCurrentNode()]+1][it2.sizes[it2.getCurrentNode()]+1];
    	
    	for (int i = firstKeyRoot-1; i >= 0; i--) {
    		revTreeEditDist(it1, it2, it1.getCurrentNode(), revKeyRoots[i], forestdist);
    	}
    	
    	return forestdist[it1.sizes[it1.getCurrentNode()]][it2.sizes[it2.getCurrentNode()]];
    }
    private int computeRevKeyRoots(InfoTree_PLUS it2, int subtreeRootNode, int pathID, int[] revKeyRoots, int index) {
    	
    	revKeyRoots[index] = subtreeRootNode;
    	index++;
    	    
    	int pathNode = pathID;
    	while (pathNode > subtreeRootNode) {
    		int parent = it2.parents[pathNode];
    		for (int child : it2.getChildren(parent)) {
    			if (child != pathNode) index = computeRevKeyRoots(it2, child, child+it2.sizes[child]-1, revKeyRoots, index);
    		}
    		pathNode = parent;
    	}
    	
    	return index;
    }
    private int getRLD(InfoTree_PLUS it, int revPostorder) {
    	int preL = it.postR_to_preL[revPostorder];
    	return it.preL_to_postR[preL + it.sizes[preL] - 1];
    }
    private void revTreeEditDist(InfoTree_PLUS it1, InfoTree_PLUS it2, int it1subtree, int it2subtree, float[][] forestdist) {
    	
    	// i,j have to be in r-l postorder
    	int i = it1.preL_to_postR[it1subtree];
    	int j = it2.preL_to_postR[it2subtree];
    	
    	int ioff = getRLD(it1, i) - 1;
        int joff = getRLD(it2, j) - 1;
        float da = 0;
        float db = 0;
        float dc = 0;
        boolean switched = it1.isSwitched();
        forestdist[0][0] = 0;
        for(int i1 = 1; i1 <= i - ioff; i1++)
            forestdist[i1][0] = forestdist[i1 - 1][0] + 1;

        for(int j1 = 1; j1 <= j - joff; j1++)
            forestdist[0][j1] = forestdist[0][j1 - 1] + 1;

        for(int i1 = 1; i1 <= i - ioff; i1++)
        {
            for(int j1 = 1; j1 <= j - joff; j1++)
            {
                counter++;
                float u = 0;
                if(it1.labels[it1.postR_to_preL[i1 + ioff]] != it2.labels[it2.postR_to_preL[j1 + joff]]) u = costMatch;
                
                if(getRLD(it1,i1 + ioff) == getRLD(it1,i) && getRLD(it2,j1 + joff) == getRLD(it2,j))
                {
                    da = forestdist[i1 - 1][j1] + costDel;
                    db = forestdist[i1][j1 - 1] + costIns;
                    dc = forestdist[i1 - 1][j1 - 1] + u;
                    forestdist[i1][j1] = da >= db ? db >= dc ? dc : db : da >= dc ? dc : da;
                    if (switched) {
                    	delta[it2.postR_to_preL[j1+joff]][it1.postR_to_preL[i1+ioff]] = forestdist[i1 - 1][j1 - 1];
                    } else {
                    	delta[it1.postR_to_preL[i1+ioff]][it2.postR_to_preL[j1+joff]] = forestdist[i1 - 1][j1 - 1];
                    }
                } else
                {
                    da = forestdist[i1 - 1][j1] + costDel;
                    db = forestdist[i1][j1 - 1] + costIns;
                    dc = forestdist[getRLD(it1,i1 + ioff) - 1 - ioff][getRLD(it2,j1 + joff) - 1 - joff] + 
                    		(switched ? delta[it2.postR_to_preL[j1 + joff]][it1.postR_to_preL[i1 + ioff]] : delta[it1.postR_to_preL[i1 + ioff]][it2.postR_to_preL[j1 + joff]]) + u;
                    forestdist[i1][j1] = da >= db ? db >= dc ? dc : db : da >= dc ? dc : da;
                }
            }

        }
    }
    // ===================== END spfR
    
    
    private byte getStrategyPathType(int pathIDWithPathIDOffset, int pathIDOffset, InfoTree_PLUS it, int currentRootNodePreL, int currentSubtreeSize)
    {
    	if (Integer.signum(pathIDWithPathIDOffset) == -1) return LEFT;
    	
        int pathID = Math.abs(pathIDWithPathIDOffset) - 1;
        if(pathID >= pathIDOffset)
            pathID = pathID - pathIDOffset;
        if(pathID == (currentRootNodePreL + currentSubtreeSize) - 1)
        	return RIGHT;
        
        return INNER;
    }

    private void updateFnArray(int lnForNode, int node, int currentSubtreePreL)
    {
        if(lnForNode >= currentSubtreePreL)
        {
            fn[node] = fn[lnForNode];
            fn[lnForNode] = node;
        } else
        {
            fn[node] = fn[fn.length - 1];
            fn[fn.length - 1] = node;
        }
    }

    private void updateFtArray(int lnForNode, int node)
    {
        ft[node] = lnForNode;
        if(fn[node] > -1)
            ft[fn[node]] = node;
    }

    public void setCustomCosts(float costDel, float costIns, float costMatch)
    {
        this.costDel = costDel;
        this.costIns = costIns;
        this.costMatch = costMatch;
    }

    public InfoTree_PLUS getIt1()
    {
        return it1;
    }

    public InfoTree_PLUS getIt2()
    {
        return it2;
    }

    
}