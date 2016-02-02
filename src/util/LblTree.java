package util;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

// Referenced classes of package util:
//            FormatUtilities

public class LblTree extends DefaultMutableTreeNode
    implements Comparable
{

    public LblTree(String label, int treeID)
    {
        this.treeID = -1;
        this.label = null;
        tmpData = null;
        nodeID = -1;
        this.treeID = treeID;
        this.label = label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public int getTreeID()
    {
        if(isRoot())
            return treeID;
        else
            return ((LblTree)getRoot()).getTreeID();
    }

    public void setTreeID(int treeID)
    {
        if(isRoot())
            this.treeID = treeID;
        else
            ((LblTree)getRoot()).setTreeID(treeID);
    }

    public void setTmpData(Object tmpData)
    {
        this.tmpData = tmpData;
    }

    public Object getTmpData()
    {
        return tmpData;
    }

    public void prettyPrint()
    {
        prettyPrint(false);
    }

    public void prettyPrint(boolean printTmpData)
    {
        for(int i = 0; i < getLevel(); i++)
            System.out.print("    ");

        if(!isRoot())
        {
            System.out.print("+---+");
        } else
        {
            if(getTreeID() != -1)
                System.out.println((new StringBuilder("treeID: ")).append(getTreeID()).toString());
            System.out.print("*---+");
        }
        System.out.print((new StringBuilder(" '")).append(getLabel()).append("' ").toString());
        if(printTmpData)
            System.out.println(getTmpData());
        else
            System.out.println();
        for(Enumeration e = children(); e.hasMoreElements(); ((LblTree)e.nextElement()).prettyPrint(printTmpData));
    }

    public int getNodeCount()
    {
        int sum = 1;
        for(Enumeration e = children(); e.hasMoreElements();)
            sum += ((LblTree)e.nextElement()).getNodeCount();

        return sum;
    }
    
    // don't try to delete root node (postorder = size of t)
    public void deleteNode(int nodePostorder) {
		int i = 0;
		for (Enumeration e = depthFirstEnumeration(); e.hasMoreElements();) {
			i++;
			LblTree s = (LblTree) e.nextElement();
			if (i == nodePostorder) {
				int sIndex = s.getParent().getIndex(s);
				while (s.getChildCount() > 0) {
					LblTree ch = (LblTree) s.getFirstChild();
					((MutableTreeNode) s.getParent()).insert(ch, sIndex);
					sIndex++;				
				}
				s.removeFromParent();
				break;
			}
		}
	}
    
    public void renameNode(int nodePostorder, String label) {
		int i = 0;
		for (Enumeration e = depthFirstEnumeration(); e.hasMoreElements();) {
			i++;
			LblTree s = (LblTree) e.nextElement();
			if (i == nodePostorder) {
				s.setLabel(label);
				break;
			}
		}
	}

    public static LblTree fromString(String s)
    {
        int treeID = FormatUtilities.getTreeID(s);
        s = s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1);
        LblTree node = new LblTree(FormatUtilities.getRoot(s), treeID);
        Vector c = FormatUtilities.getChildren(s);
        for(int i = 0; i < c.size(); i++)
            node.add(fromString((String)c.elementAt(i)));

        return node;
    }

    public String toString()
    {
        String res = (new StringBuilder("{")).append(getLabel()).toString();
        if(getTreeID() >= 0 && isRoot())
            res = (new StringBuilder(String.valueOf(getTreeID()))).append(":").append(res).toString();
        for(Enumeration e = children(); e.hasMoreElements();)
            res = (new StringBuilder(String.valueOf(res))).append(((LblTree)e.nextElement()).toString()).toString();

        res = (new StringBuilder(String.valueOf(res))).append("}").toString();
        return res;
    }

    public int compareTo(Object o)
    {
        return getLabel().compareTo(((LblTree)o).getLabel());
    }

    public void clearTmpData()
    {
        for(Enumeration e = breadthFirstEnumeration(); e.hasMoreElements(); ((LblTree)e.nextElement()).setTmpData(null));
    }

    public static final String TAB_STRING = "    ";
    public static final String ROOT_STRING = "*---+";
    public static final String BRANCH_STRING = "+---+";
    public static final String OPEN_BRACKET = "{";
    public static final String CLOSE_BRACKET = "}";
    public static final String ID_SEPARATOR = ":";
    public static final int HIDE_NOTHING = 0;
    public static final int HIDE_ROOT_LABEL = 1;
    public static final int RENAME_LABELS_TO_LEVEL = 2;
    public static final int HIDE_ALL_LABELS = 3;
    public static final int RANDOM_ROOT_LABEL = 4;
    public final int NO_NODE = -1;
    public final int NO_TREE_ID = -1;
    int treeID;
    String label;
    Object tmpData;
    int nodeID;
}