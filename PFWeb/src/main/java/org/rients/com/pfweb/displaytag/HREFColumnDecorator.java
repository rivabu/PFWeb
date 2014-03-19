package org.rients.com.pfweb.displaytag;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

public class HREFColumnDecorator implements DisplaytagColumnDecorator {

    public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media) throws DecoratorException {
        return "<a href=\"/PFWeb/TransactionDetails?id=" + columnValue.toString()
                + "\" class=\"lightbox\">Klik mij</a>";
    }

}
