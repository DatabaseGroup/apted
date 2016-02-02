package util;

import java.util.Hashtable;
import java.util.Map;

public class LabelDictionary
{

    public LabelDictionary()
    {
        newLabelsAllowed = true;
        count = 0;
        StrInt = new Hashtable();
        IntStr = new Hashtable();
    }

    public int store(String label)
    {
        if(StrInt.containsKey(label))
            return ((Integer)StrInt.get(label)).intValue();
        if(!newLabelsAllowed)
        {
            return -1;
        } else
        {
            Integer intKey = new Integer(count++);
            StrInt.put(label, intKey);
            IntStr.put(intKey, label);
            return intKey.intValue();
        }
    }

    public String read(int labelID)
    {
        return (String)IntStr.get(new Integer(labelID));
    }

    public boolean isNewLabelsAllowed()
    {
        return newLabelsAllowed;
    }

    public void setNewLabelsAllowed(boolean newLabelsAllowed)
    {
        this.newLabelsAllowed = newLabelsAllowed;
    }

    public static final int KEY_DUMMY_LABEL = -1;
    private int count;
    private Map StrInt;
    private Map IntStr;
    private boolean newLabelsAllowed;
}