package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.RunnerResult;


public class RunFailureEvent extends RunEvent {
    public RunFailureEvent(Element element,RunnerResult rr) {
        super(element,rr);
    }
}
