package org.rients.com.transactionOverview;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TransactionComparator implements Comparator, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8430634587512807970L;

	public int compare(Object o1, Object o2) {
        int date1 = ((Transaction) o1).getKoopKoers().getDatum();
        int date2 = ((Transaction) o2).getKoopKoers().getDatum();

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
