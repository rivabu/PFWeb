package org.rients.com.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TransactionComparator implements Comparator<Object>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8430634587512807970L;

	public int compare(Object o1, Object o2) {
        int date1 = ((Transaction) o1).getStartDate();
        int date2 = ((Transaction) o2).getStartDate();

        if (date1 > date2) {
            return 1;
        } else {
            return -1;
        }
    }

    public static List<Transaction> orderTransactions(List<Transaction> lst) {
        Collections.sort(lst, new TransactionComparator());

        return lst;
    }
}
