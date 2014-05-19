package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.RunnerResult;

public class RunEvent {

    private final Element element;
    private final RunnerResult runnerResult;

    public RunEvent(Element element,RunnerResult rr) {
        this.element = element;
        this.runnerResult = rr;
    }

    public Element getElement() {
        return element;
    }
    
    public RunnerResult getRunnerResult() {
    	return runnerResult;
    }

}
