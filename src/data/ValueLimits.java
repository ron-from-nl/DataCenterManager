package data;

import java.io.Serializable;

public class ValueLimits implements Serializable, Cloneable
{
    private Double min = 0D;
    private Double max = 0D;
    
    public ValueLimits() { }
    public ValueLimits(Double minParam, Double maxParam)
    {
        min = minParam;
        max = maxParam;
    }
    
    public Double getMin() { return min; }
    public Double getMax() { return max; }
    public void setMin(Double minParam) { min = minParam; }
    public void setMax(Double maxParam) { max = maxParam; }
    public void addMin(Double minParam) { min += minParam; }
    public void addMax(Double maxParam) { max += maxParam; }
    public void subMin(Double minParam) { min -= minParam; }
    public void subMax(Double maxParam) { max -= maxParam; }

    @Override
    public String toString()
    {
        String output = "";
        output = Double.toString(getMin()) +" ";
        output += Double.toString(getMin()) +" ";
        return output;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
