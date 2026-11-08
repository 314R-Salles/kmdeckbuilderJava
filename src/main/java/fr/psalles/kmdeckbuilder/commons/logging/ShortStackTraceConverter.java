package fr.psalles.kmdeckbuilder.commons.logging;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;

// Garde les premières lignes de chaque exception, puis uniquement celles de notre code.
// Doit étendre ThrowableHandlingConverter : sinon Logback ajoute aussi la trace complète à la suite.
public class ShortStackTraceConverter extends ThrowableHandlingConverter {

    private static final String APP_PACKAGE = "fr.psalles.kmdeckbuilder";
    private static final int FIRST_LINES = 3;

    @Override
    public String convert(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (IThrowableProxy t = event.getThrowableProxy(); t != null; t = t.getCause()) {
            sb.append(prefix).append(t.getClassName()).append(": ").append(t.getMessage())
                    .append(CoreConstants.LINE_SEPARATOR);
            appendFrames(sb, t.getStackTraceElementProxyArray());
            prefix = "Caused by: ";
        }
        return sb.toString();
    }

    private void appendFrames(StringBuilder sb, StackTraceElementProxy[] frames) {
        int hidden = 0;
        for (int i = 0; i < frames.length; i++) {
            if (i < FIRST_LINES || isAppFrame(frames[i])) {
                appendHidden(sb, hidden);
                hidden = 0;
                sb.append("    ").append(frames[i]).append(CoreConstants.LINE_SEPARATOR);
            } else {
                hidden++;
            }
        }
        appendHidden(sb, hidden);
    }

    private boolean isAppFrame(StackTraceElementProxy frame) {
        String className = frame.getStackTraceElement().getClassName();
        // "$$" : classes générées par Spring (proxys), sans intérêt pour le débogage
        return className.startsWith(APP_PACKAGE) && !className.contains("$$");
    }

    private void appendHidden(StringBuilder sb, int hidden) {
        if (hidden > 0) {
            sb.append("    ... ").append(hidden).append(" lignes masquées")
                    .append(CoreConstants.LINE_SEPARATOR);
        }
    }
}
