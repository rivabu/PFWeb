package org.rients.com.pfweb.controllers;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.time.FastDateFormat;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

public class HREFFormatter implements DisplaytagColumnDecorator
       {
       
           /**
            * FastDateFormat used to format the date object.
            */
           private FastDateFormat dateFormat = FastDateFormat.getInstance("MM/dd/yyyy HH:mm:ss"); //$NON-NLS-1$
       
           /**
            * transform the given object into a String representation. The object is supposed to be a date.
            * @see org.displaytag.decorator.DisplaytagColumnDecorator#decorate(Object, PageContext, MediaTypeEnum)
            */
           public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media) throws DecoratorException
           {
               return "<a href=\"http://127.0.0.1:8060/PFWeb/overlay.jsp\" class=\"lightbox\">Klik mij</a>";
           }

}


