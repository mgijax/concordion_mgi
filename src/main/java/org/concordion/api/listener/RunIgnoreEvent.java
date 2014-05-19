package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.RunnerResult;

public class RunIgnoreEvent extends RunEvent {
    public RunIgnoreEvent(Element element,RunnerResult rr) {
        super(element,rr);
    }
}
