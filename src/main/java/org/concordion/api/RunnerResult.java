package org.concordion.api;

import java.util.ArrayList;
import java.util.List;

public class RunnerResult {

    private final List<Result> results;
    
    int failureCount = 0;
    int ignoreCount = 0;
    int successCount = 0;
    int manFailCount = 0;

    public RunnerResult(Result result) {
    	this.results = new ArrayList<Result>();
        this.results.add(result);
        setCounts();
    }
    public RunnerResult(List<Result> results) {
    	this.results = results;
    	setCounts();
    }
    
    private void setCounts()
    {
    	for(Result result : results)
    	{
    		if(result==Result.SUCCESS) successCount+=1;
    		else if(result==Result.IGNORED) ignoreCount+=1;
    		else if(result==Result.FAILURE) failureCount+=1;
    		else if(result==Result.MANUAL_FAIL) manFailCount+=1;
    	}
    }
    
    /*
     * Return an aggregated result
     */
    public Result getResult() {
    	if(results.size()<1) return Result.SUCCESS;
    	int successCount=0;
    	for(Result result : results)
    	{
    		if(result==Result.MANUAL_FAIL)
    			continue; // only for statistic purposes
    		
    		if(isResultNotSuccess(result))
    		{
    			// must be fail or exception
    			return result;
    		}
    		else if(result==Result.SUCCESS)
    		{
    			successCount+=1;
    		}
    	}
    	if(successCount<1) return Result.IGNORED;
        return Result.SUCCESS;
    }
    
    public List<Result> getResults()
    {
    	return results;
    }
    
    public int getFailureCount()
    {
    	return failureCount;
    }
    public int getIgnoreCount()
    {
    	return ignoreCount;
    }
    public int getSuccessCount()
    {
    	return successCount;
    }
    public int getTotalResultCount()
    {
    	return results.size();
    }
    
    public int getManualFailCount()
    {
    	return manFailCount;
    }
    
    public boolean isResultNotSuccess(Result result)
    {
    	return !isResultSuccess(result);
    }
    public boolean isResultSuccess(Result result)
    {
    	return result==Result.SUCCESS || result==Result.IGNORED;
    }
}
