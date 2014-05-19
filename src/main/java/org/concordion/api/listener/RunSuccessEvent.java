package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.RunnerResult;

public class RunSuccessEvent extends RunEvent {
    public RunSuccessEvent(Element element,RunnerResult rr) {
        super(element,rr);
    }
}
