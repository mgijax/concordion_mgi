package org.concordion.internal.listener;

import org.concordion.api.Element;
import org.concordion.api.RunnerResult;
import org.concordion.api.listener.RunEvent;
import org.concordion.api.listener.RunFailureEvent;
import org.concordion.api.listener.RunIgnoreEvent;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.RunSuccessEvent;

public class RunResultRenderer extends ThrowableRenderer implements RunListener {

    public void successReported(RunSuccessEvent event) {
        event.getElement().addStyleClass("success").appendNonBreakingSpaceIfBlank();
        appendCounts(event);
    }

    public void failureReported(RunFailureEvent event) {
        event.getElement().addStyleClass("failure").appendNonBreakingSpaceIfBlank();
        appendCounts(event);
    }

    public void ignoredReported(RunIgnoreEvent event) {
        event.getElement().addStyleClass("ignored").appendNonBreakingSpaceIfBlank();
        appendCounts(event);
    }
    
    private void appendCounts(RunEvent event) {
    	RunnerResult rr = event.getRunnerResult();
    	if(rr.getFailureCount()>0)
    	{
    		String text = " ("+pluralize("failure",rr.getFailureCount());
    		if(rr.getManualFailCount()>0)
    		{
    			text += ", "+rr.getManualFailCount()+" manual";
    		}
    		text += ")";
    		event.getElement().appendChild(new Element("b").appendText(text));
    	}
    }
    
    private String pluralize(String word, int count)
    {
    	if (count == 1) return count+" "+word;
    	return count + " " + word + "s";
    }

}
