package org.concordion.api;

import java.io.PrintStream;

public interface ResultSummary {
    
    void assertIsSatisfied(Object fixture);

    boolean hasExceptions();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();

    long getIgnoredCount();
    
    void print(PrintStream out, Object fixture);

}
