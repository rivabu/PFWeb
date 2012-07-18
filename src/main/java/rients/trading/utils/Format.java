package rients.trading.utils;

/**
Deze class is corrupt, de functies print missen. Gaarne deze class van de cd halen "java advanced 1.1
*/

public class Format
{

    private int width;
    private int precision;
    private String pre;
    private String post;
    private boolean leading_zeroes;
    private boolean show_plus;
    private boolean alternate;
    private boolean show_space;
    private boolean left_align;
    private char fmt;
    public static double atof(String s)
    {
        int i = 0;
        int j = 1;
        double d = 0.0D;
        double d1 = 1.0D;
        boolean flag = false;
        for(; i < s.length() && Character.isWhitespace(s.charAt(i)); i++);
        if(i < s.length() && s.charAt(i) == '-')
        {
            j = -1;
            i++;
        } else
        if(i < s.length() && s.charAt(i) == '+')
            i++;
        for(; i < s.length(); i++)
        {
            int k = s.charAt(i);
            if(48 <= k && k <= 57)
            {
                if(!flag)
                    d = (d * 10D + (double)k) - 48D;
                else
                if(flag)
                {
                    d1 /= 10D;
                    d += d1 * (double)(k - 48);
                }
            } else
            if(k == 46)
            {
                if(!flag)
                    flag = true;
                else
                    return (double)j * d;
            } else
            if(k == 101 || k == 69)
            {
                long l = (int)parseLong(s.substring(i + 1), 10);
                return (double)j * d * Math.pow(10D, l);
            } else
            {
                return (double)j * d;
            }
        }

        return (double)j * d;
    }
    public static int atoi(String s)
    {
        return (int)atol(s);
    }
    public static long atol(String s)
    {
        int i;
        for(i = 0; i < s.length() && Character.isWhitespace(s.charAt(i)); i++);
        if(i < s.length() && s.charAt(i) == '0')
        {
            if(i + 1 < s.length() && (s.charAt(i + 1) == 'x' || s.charAt(i + 1) == 'X'))
                return parseLong(s.substring(i + 2), 16);
            else
                return parseLong(s, 8);
        } else
        {
            return parseLong(s, 10);
        }
    }
    private static String convert(long l, int i, int j, String s)
    {
        if(l == 0L)
            return "0";
        String s1 = "";
        for(; l != 0L; l >>>= i)
            s1 = s.charAt((int)(l & (long)j)) + s1;

        return s1;
    }
    private String exp_format(double d)
    {
        String s = "";
        int i = 0;
        double d1 = d;
        double d2 = 1.0D;
        if(d != 0.0D)
        {
            for(; d1 > 10D; d1 /= 10D)
            {
                i++;
                d2 /= 10D;
            }

            for(; d1 < 1.0D; d1 *= 10D)
            {
                i--;
                d2 *= 10D;
            }

        }
        if((fmt == 'g' || fmt == 'G') && i >= -4 && i < precision)
            return fixed_format(d);
        d *= d2;
        s = s + fixed_format(d);
        if(fmt == 'e' || fmt == 'g')
            s = s + "e";
        else
            s = s + "E";
        String s1 = "000";
        if(i >= 0)
        {
            s = s + "+";
            s1 = s1 + i;
        } else
        {
            s = s + "-";
            s1 = s1 + -i;
        }
        return s + s1.substring(s1.length() - 3, s1.length());
    }
    private String fixed_format(double d)
    {
        boolean flag = (fmt == 'G' || fmt == 'g') && !alternate;
        if(d > 9.2233720368547758E+018D)
            return exp_format(d);
        if(precision == 0)
            return (long)(d + 0.5D) + (flag ? "" : ".");
        long l = (long)d;
        double d1 = d - (double)l;
        if(d1 >= 1.0D || d1 < 0.0D)
            return exp_format(d);
        double d2 = 1.0D;
        String s = "";
        for(int i = 1; i <= precision && d2 <= 9.2233720368547758E+018D; i++)
        {
            d2 *= 10D;
            s = s + "0";
        }

        long l1 = (long)(d2 * d1 + 0.5D);
        if((double)l1 >= d2)
        {
            l1 = 0L;
            l++;
        }
        String s1 = s + l1;
        s1 = "." + s1.substring(s1.length() - precision, s1.length());
        if(flag)
        {
            int j;
            for(j = s1.length() - 1; j >= 0 && s1.charAt(j) == '0'; j--);
            if(j >= 0 && s1.charAt(j) == '.')
                j--;
            s1 = s1.substring(0, j + 1);
        }
        return l + s1;
    }
    public String form(char c)
    {
        if(fmt != 'c')
        {
            throw new IllegalArgumentException();
        } else
        {
            String s = "" + c;
            return pad(s);
        }
    }
    public String form(double d)
    {
        if(precision < 0)
            precision = 6;
        byte byte0 = 1;
        if(d < 0.0D)
        {
            d = -d;
            byte0 = -1;
        }
        String s;
        if(fmt == 'f')
            s = fixed_format(d);
        else
        if(fmt == 'e' || fmt == 'E' || fmt == 'g' || fmt == 'G')
            s = exp_format(d);
        else
            throw new IllegalArgumentException();
        return pad(sign(byte0, s));
    }
    public String form(long l)
    {
        byte byte0 = 0;
        String s;
        if(fmt == 'd' || fmt == 'i')
        {
            if(l < 0L)
            {
                s = ("" + l).substring(1);
                byte0 = -1;
            } else
            {
                s = "" + l;
                byte0 = 1;
            }
        } else
        if(fmt == 'o')
            s = convert(l, 3, 7, "01234567");
        else
        if(fmt == 'x')
            s = convert(l, 4, 15, "0123456789abcdef");
        else
        if(fmt == 'X')
            s = convert(l, 4, 15, "0123456789ABCDEF");
        else
            throw new IllegalArgumentException();
        return pad(sign(byte0, s));
    }
    public String form(String s)
    {
        if(fmt != 's')
            throw new IllegalArgumentException();
        if(precision >= 0)
            s = s.substring(0, precision);
        return pad(s);
    }
    public static void main(String args[])
    {
        double d = 1.2345678901199999D;
        double d1 = 123D;
        double d2 = 1.2345E+030D;
        double d3 = 1.02D;
        double d4 = 1.234E-005D;
        int i = 51966;
        /*
        print(System.out, "x = |%f|\n", d);
        print(System.out, "u = |%20f|\n", d4);
        print(System.out, "x = |% .5f|\n", d);
        print(System.out, "w = |%20.5f|\n", d3);
        print(System.out, "x = |%020.5f|\n", d);
        print(System.out, "x = |%+20.5f|\n", d);
        print(System.out, "x = |%+020.5f|\n", d);
        print(System.out, "x = |% 020.5f|\n", d);
        print(System.out, "y = |%#+20.5f|\n", d1);
        print(System.out, "y = |%-+20.5f|\n", d1);
        print(System.out, "z = |%20.5f|\n", d2);
        print(System.out, "x = |%e|\n", d);
        print(System.out, "u = |%20e|\n", d4);
        print(System.out, "x = |% .5e|\n", d);
        print(System.out, "w = |%20.5e|\n", d3);
        print(System.out, "x = |%020.5e|\n", d);
        print(System.out, "x = |%+20.5e|\n", d);
        print(System.out, "x = |%+020.5e|\n", d);
        print(System.out, "x = |% 020.5e|\n", d);
        print(System.out, "y = |%#+20.5e|\n", d1);
        print(System.out, "y = |%-+20.5e|\n", d1);
        print(System.out, "x = |%g|\n", d);
        print(System.out, "z = |%g|\n", d2);
        print(System.out, "w = |%g|\n", d3);
        print(System.out, "u = |%g|\n", d4);
        print(System.out, "y = |%.2g|\n", d1);
        print(System.out, "y = |%#.2g|\n", d1);
        print(System.out, "d = |%d|\n", i);
        print(System.out, "d = |%20d|\n", i);
        print(System.out, "d = |%020d|\n", i);
        print(System.out, "d = |%+20d|\n", i);
        print(System.out, "d = |% 020d|\n", i);
        print(System.out, "d = |%-20d|\n", i);
        print(System.out, "d = |%20.8d|\n", i);
        print(System.out, "d = |%x|\n", i);
        print(System.out, "d = |%20X|\n", i);
        print(System.out, "d = |%#20x|\n", i);
        print(System.out, "d = |%020X|\n", i);
        print(System.out, "d = |%20.8x|\n", i);
        print(System.out, "d = |%o|\n", i);
        print(System.out, "d = |%020o|\n", i);
        print(System.out, "d = |%#20o|\n", i);
        print(System.out, "d = |%#020o|\n", i);
        print(System.out, "d = |%20.12o|\n", i);
        print(System.out, "s = |%-20s|\n", "Hello");
        print(System.out, "s = |%-20c|\n", '!');
        print(System.out, "|%i|\n", 0x8000000000000000L);
        print(System.out, "|%6.2e|\n", 0.0D);
        print(System.out, "|%6.2g|\n", 0.0D);
        print(System.out, "|%6.2f|\n", 9.9900000000000002D);
        print(System.out, "|%6.2f|\n", 9.9990000000000006D);
        print(System.out, "|%6.0f|\n", 9.9990000000000006D);
        */
    }
    private String pad(String s)
    {
        String s1 = repeat(' ', width - s.length());
        if(left_align)
            return pre + s + s1 + post;
        else
            return pre + s1 + s + post;
    }
    private static long parseLong(String s, int i)
    {
        int j = 0;
        int k = 1;
        long l = 0L;
        for(; j < s.length() && Character.isWhitespace(s.charAt(j)); j++);
        if(j < s.length() && s.charAt(j) == '-')
        {
            k = -1;
            j++;
        } else
        if(j < s.length() && s.charAt(j) == '+')
            j++;
        for(; j < s.length(); j++)
        {
            int i1 = s.charAt(j);
            if(48 <= i1 && i1 < 48 + i)
                l = (l * (long)i + (long)i1) - 48L;
            else
            if(65 <= i1 && i1 < (65 + i) - 10)
                l = ((l * (long)i + (long)i1) - 65L) + 10L;
            else
            if(97 <= i1 && i1 < (97 + i) - 10)
                l = ((l * (long)i + (long)i1) - 97L) + 10L;
            else
                return l * (long)k;
        }

        return l * (long)k;
    }
    private static String repeat(char c, int i)
    {
        if(i <= 0)
            return "";
        StringBuffer stringbuffer = new StringBuffer(i);
        for(int j = 0; j < i; j++)
            stringbuffer.append(c);

        return stringbuffer.toString();
    }
    private String sign(int i, String s)
    {
        String s1 = "";
        if(i < 0)
            s1 = "-";
        else
        if(i > 0)
        {
            if(show_plus)
                s1 = "+";
            else
            if(show_space)
                s1 = " ";
        } else
        if(fmt == 'o' && alternate && s.length() > 0 && s.charAt(0) != '0')
            s1 = "0";
        else
        if(fmt == 'x' && alternate)
            s1 = "0x";
        else
        if(fmt == 'X' && alternate)
            s1 = "0X";
        int j = 0;
        if(leading_zeroes)
            j = width;
        else
        if((fmt == 'd' || fmt == 'i' || fmt == 'x' || fmt == 'X' || fmt == 'o') && precision > 0)
            j = precision;
        return s1 + repeat('0', j - s1.length() - s.length()) + s;
    }
}
