package org.rients.com.pfweb.controllers;

import org.displaytag.decorator.TableDecorator;
import org.rients.com.model.Transaction;

public class RientsWrapper extends TableDecorator {
    public String addRowClass()
    {
        return ((Transaction) getCurrentRowObject()).getScoreAbs() > 0 ? "odd" : "even";
    }
    
}
