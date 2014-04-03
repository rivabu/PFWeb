package org.rients.com.pfweb.displaytag;

import org.displaytag.decorator.TableDecorator;
import org.rients.com.model.Transaction;

public class OddEvenTableDecorator extends TableDecorator {
    public String addRowClass()
    {
        return ((Transaction) getCurrentRowObject()).getScoreAbs() >= 0 ? "odd" : "even";
    }
    
}
