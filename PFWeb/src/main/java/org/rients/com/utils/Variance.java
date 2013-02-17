package org.rients.com.utils;

import java.util.Iterator;
import java.util.List;

import org.rients.com.model.Dagkoers;


public class Variance
{
   public static float getHighest(List<Dagkoers> rates) {
       float highest = 0;
       Iterator i = rates.iterator();
       for(int j=0; i.hasNext(); j++) {
           Dagkoers dk = (Dagkoers) i.next();
           if(dk.closekoers > highest)
               highest = dk.closekoers;
       }
       return highest;
   }
   
   public static float getLowest(List<Dagkoers> rates) {
       float lowest = ((Dagkoers) rates.get(0)).closekoers;
       @SuppressWarnings("rawtypes")
    Iterator i = rates.iterator();
       for(int j=0; i.hasNext(); j++) {
           Dagkoers dk = (Dagkoers) i.next();
           if(dk.closekoers < lowest)
               lowest = dk.closekoers;
       }
       return lowest;
   }
       
    public static float variances(List<Dagkoers> rates){
    
        int aantal = rates.size();
        Iterator i = rates.iterator();
        Float[] data = new Float[aantal];
        for(int j=0; i.hasNext(); j++) {
            Dagkoers dk = (Dagkoers) i.next();
            data[j] = new Float(dk.closekoers);
        }
        return variances(data);
    }
    
    public static float variances(Float[] data)
	{
	    float sum = 0;
	    float sumsquare = 0;
		int len = data.length;
		float avr = 0;
		for (int i = 0; i < len; ++i)
		{
			sum += data[i].floatValue();
			
		}
		avr = sum /len;
		for (int i = 0; i < len; ++i)
		{
		    float temp = data[i].floatValue() - avr;
		    sumsquare +=(temp * temp);
		}
		return new Double(Math.sqrt(sumsquare / len)).floatValue(); 
	}
    
    public static float avr(Float[] data)
	{
	    float sum = 0;
		int len = data.length;
		float avr = 0;
		for (int i = 0; i < len; ++i)
		{
			sum += data[i].floatValue();
			
		}
		avr = sum /len;
		
		return avr; 
	}

	public static void main(String[] args)
	{
		//int len = 3;
		//if (args.length > 0)
		//	len = Integer.parseInt(args[0]);
		//int[]data=new int[len];
		//int[] data = { 100, 200, 50 };
		float[] data = { 103, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 };
		Float[] temp = new Float[data.length];
		for (int i=0;i<data.length;++i)
		{
			//data[i]=(int) Math.round((Math.random()*100)+0.5);
		    temp[i]=new Float(data[i]);
			
		}
		
		float  variance = variances(temp);
		System.out.println("variance " + variance);
	}
}
