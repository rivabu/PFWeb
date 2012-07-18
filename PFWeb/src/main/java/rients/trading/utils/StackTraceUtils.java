package rients.trading.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public final class StackTraceUtils {
    private StackTraceUtils() {
    }

    public static String getStackTrace(final Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);

        return result.toString();
    }
}
