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