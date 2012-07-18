package rients.trading.context;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import java.util.Hashtable;
import java.util.Map;


public class TradingContext {
    private static Map<String, Object> contextObjects = null;
    private static final Logger LOGGER;

    static {
        contextObjects = new Hashtable<String, Object>();
        LOGGER = Logger.getLogger(rients.trading.context.TradingContext.class);
    }

    /**
     * Empty constructor
     */
    TradingContext() {
    }

    public void emptyTradingContect() {
        if ((contextObjects != null) && !contextObjects.isEmpty()) {
            contextObjects = new Hashtable<String, Object>();
        }

        LOGGER.debug("contextObjects cleared");
    }

    public void putObject(String name, Object object) {
        contextObjects.put(name, object);
        LOGGER.debug("contextObjects putObject = " + name);
    }

    public Object getObject(String name) {
        Object returnObject = null;

        if (StringUtils.isNotBlank(name) && contextObjects.containsKey(name)) {
            returnObject = contextObjects.get(name);
        } else {
            returnObject = Integer.valueOf(-1);
            LOGGER.error("problems retrieving object from tradingContext: name = " + name);
        }

        return returnObject;
    }

    public boolean containsObject(String name) {
        boolean returnValue = false;

        if (StringUtils.isNotBlank(name) && contextObjects.containsKey(name)) {
            returnValue = true;
        }

        return returnValue;
    }

    public void deleteObject(String name) {
        if (StringUtils.isNotBlank(name) && contextObjects.containsKey(name)) {
            contextObjects.remove(name);
        } else {
            LOGGER.error("problems deleting object from tradingContext: name = " + name);
        }
    }
}
