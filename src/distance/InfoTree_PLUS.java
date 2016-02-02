package distance;

import java.util.*;
import util.LabelDictionary;
import util.LblTree;

public class InfoTree_PLUS
{

    public InfoTree_PLUS(LblTree aInputTree, LabelDictionary aLd)
    {
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
        
        labels = new int[treeSize];
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

    public int getSize()
    {
        return treeSize;
    }

    public int getLeafCount()
    {
        return leafCount;
    }

    public boolean ifNodeOfType(int postorder, int type)
    {
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

    public int[] getChildren(int node)
    {
        return children[node];
    }

    public int getSizes(int node)
    {
        return sizes[node];
    }

    public int getParents(int node)
    {
        return parents[node];
    }

    public int getPreL_to_PreR(int node)
    {
        return preL_to_preR[node];
    }

    public int getPreR_to_PreL(int node)
    {
        return preR_to_preL[node];
    }

    public int getLabels(int node)
    {
        return labels[node];
    }

    public int getPreL_to_LN(int node)
    {
        return preL_to_ln[node];
    }

    public int getPreR_to_LN(int node)
    {
        return preR_to_ln[node];
    }

    public int getPreL_to_KR_Sum(int node)
    {
        return preL_to_kr_sum[node];
    }

    public int getPreL_to_Rev_KR_Sum(int node)
    {
        return preL_to_rev_kr_sum[node];
    }

    public int getPreL_to_Desc_Sum(int node)
    {
        return preL_to_desc_sum[node];
    }

    public int getCurrentNode()
    {
        return currentNode;
    }

    public void setCurrentNode(int preorderL)
    {
        currentNode = preorderL;
    }

    private int gatherInfo(LblTree aT, int postorder)
    {
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
        for(Enumeration e = aT.children(); e.hasMoreElements();)
        {
            childrenCount++;
            currentPreorder = preorderTmp;
            parents[currentPreorder] = preorder;
            postorder = gatherInfo((LblTree)e.nextElement(), postorder);
            childrenPreorders.add(Integer.valueOf(currentPreorder));
            weight = sizeTmp + 1;
            if(weight >= maxWeight)
            {
                maxWeight = weight;
                heavyChild = currentPreorder;
            }
            currentSize += 1 + sizeTmp;
            descSizes += descSizesTmp;
            if(childrenCount > 1)
            {
                krSizesSum += krSizesSumTmp + sizeTmp + 1;
            } else
            {
                krSizesSum += krSizesSumTmp;
                nodeType_L[currentPreorder] = true;
            }
            if(e.hasMoreElements())
            {
                revkrSizesSum += revkrSizesSumTmp + sizeTmp + 1;
            } else
            {
                revkrSizesSum += revkrSizesSumTmp;
                nodeType_R[currentPreorder] = true;
            }
        }

        postorder++;
        aT.setTmpData(Integer.valueOf(preorder));
        int currentDescSizes = descSizes + currentSize + 1;
        preL_to_desc_sum[preorder] = ((currentSize + 1) * (currentSize + 1 + 3)) / 2 - currentDescSizes;
        preL_to_kr_sum[preorder] = krSizesSum + currentSize + 1;
        preL_to_rev_kr_sum[preorder] = revkrSizesSum + currentSize + 1;
        labels[preorder] = ld.store(aT.getLabel());
        sizes[preorder] = currentSize + 1;
        preorderR = treeSize - 1 - postorder;
        preL_to_preR[preorder] = preorderR;
        preR_to_preL[preorderR] = preorder;        
        if(heavyChild != -1)
            nodeType_H[heavyChild] = true;
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

    private LblTree inputTree;
    private static final byte LEFT = 0;
    private static final byte RIGHT = 1;
    private static final byte HEAVY = 2;
    public int sizes[];
    public int parents[];
    public int preL_to_preR[];
    public int preR_to_preL[];
    public int labels[];
    public int preL_to_ln[];
    public int preR_to_ln[];
    public int preL_to_kr_sum[];
    public int preL_to_rev_kr_sum[];
    public int preL_to_desc_sum[];
    
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
}