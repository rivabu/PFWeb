
package rients.trading.download.model;



public class Levels
{

    private float values[];


    public void createExpLevelArray(float stepSize, float startValue)
    {
        int j=1;
        try
        {
                float startvalueOld = 0F;
                float af[] = new float[30000];
                af[0] = startValue;
               
                    
                stepSize = 1+ (stepSize / 100); //1.01, 1.02, 1.03, 1.04
				while(startValue < 30000)
				{
					startvalueOld = startValue;
					startValue = startValue * stepSize;
					// nu naar boven afronden op twee waarden achter de komma
                    double temp = startValue * 100F;
                    temp = Math.ceil(temp);
                    temp  =  temp / 100;
					startValue = Float.valueOf(""+temp).floatValue();				
                    af[j] = startValue;
                    j++;
				}
				values = new float[j];
				values = af;
        }
        catch(ArrayIndexOutOfBoundsException _ex)
        {
            System.out.println("in creeerNivosExponential(), array nivos te klein" + j);
        }
    }

    public String lookupRate(int counter)
    {
        // TODO: create exception here
        return new Float(values[counter]).toString();
    }

    
    public int LookupOccurenceNumber(float rate)
    {
        for(int j = 0; j < values.length; j++)
            if(values[j] >= rate)
                return j;

        return 0;
    }

}
