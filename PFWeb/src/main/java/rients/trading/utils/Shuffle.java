package rients.trading.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Shuffle {
    public static void main(String args[]) {
        List l = Arrays.asList(args);
        Collections.shuffle(l);
        System.out.println(l);
    }
    
	public static List shuffle(String args[]) {
			List l = Arrays.asList(args);
			Collections.shuffle(l);
			return l;
		}
}

