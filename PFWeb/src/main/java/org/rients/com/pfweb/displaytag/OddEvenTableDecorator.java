package org.rients.com.pfweb.displaytag;

import org.displaytag.decorator.TableDecorator;
import org.rients.com.model.Transaction;

public class OddEvenTableDecorator extends TableDecorator {
    public String addRowClass()
    {
    	String returnValue = "even";
    	Transaction t = (Transaction) getCurrentRowObject();
    	if (t.getScoreAbs() >= 0) {
    		returnValue = "odd";
    	} else {
    		returnValue = "even";
    	}
    	return returnValue;
    }
    
}
