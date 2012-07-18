// Decompiled by Jad v1.5.7d. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLUtilities.java

package rients.trading.utils;


// Referenced classes of package modelutilities:
//            Format

public final class URLUtilities
{

    public URLUtilities()
    {
    }

    public static String urlDecode(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        char c;
        for(int i = 0; i < s.length(); stringbuffer.append(c))
        {
            c = s.charAt(i);
            i++;
            if(c == '+')
                c = ' ';
            else
            if(c == '%')
            {
                c = (char)Format.atoi("0x" + s.substring(i, i + 2));
                i++;
            }
        }

        return new String(stringbuffer);
    }
}
