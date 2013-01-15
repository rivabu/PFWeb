
package rients.trading.download.model;

import java.util.HashMap;
import java.util.Map;



public class Levels
{

    private static Levels instance = null;
    protected Levels() {
       // Exists only to defeat instantiation.
    }
    public static Levels getInstance() {
       if(instance == null) {
          instance = new Levels();
       }
       return instance;
    }
    
    private Map<Float, float[]> map = new HashMap<Float, float[]>();
    


    public void createExpLevelArray(final float stepSize, float startValue)
    {
        if (! map.containsKey(new Float(stepSize))) {
            float values[] = new float[30000];;
            int j=1;
            try
            {
                values[0] = startValue;
                   
                        
                    float stepSizeTemp = 1+ (stepSize / 100); //1.01, 1.02, 1.03, 1.04
    				while(startValue < 30000)
    				{
    					startValue = startValue * stepSizeTemp;
    					// nu naar boven afronden op twee waarden achter de komma
                        double temp = startValue * 100F;
                        temp = Math.ceil(temp);
                        temp  =  temp / 100;
    					startValue = Float.valueOf(""+temp).floatValue();				
    					values[j] = startValue;
                        j++;
    				}
            }
            catch(ArrayIndexOutOfBoundsException _ex)
            {
                System.out.println("in creeerNivosExponential(), array nivos te klein" + j);
            }
            map.put(new Float(stepSize), values);
        }
    }

    public String lookupRate(float stepSize, int counter)
    {
        float values[]  = map.get(new Float(stepSize));
        return String.format("%.2f", values[counter]).replaceAll(",", ".") ;
    }

    
    public int LookupOccurenceNumber(float stepSize, float rate)
    {
        float values[]  = map.get(new Float(stepSize));
        for(int j = 0; j < values.length; j++)
            if(values[j] >= rate)
                return j;

        return 0;
    }

}
