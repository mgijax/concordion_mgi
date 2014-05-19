package org.concordion.internal.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.Runner;
import org.concordion.api.RunnerResult;
import org.concordion.api.Unimplemented;
import org.concordion.internal.util.Stats;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

public class DefaultConcordionRunner implements Runner {

    private static Logger logger = Logger.getLogger(DefaultConcordionRunner.class.getName());
    
    public RunnerResult execute(Resource resource, String href) throws Exception {
        Class<?> concordionClass = findTestClass(resource, href);
        return runTestClass(concordionClass);            
    }

    /**
     * Finds the test class for the specification referenced by the given href, relative to the 
     * resource.  
     * 
     * @param resource the current resource
     * @param href the specification to find the test class for
     * @return test class
     * @throws ClassNotFoundException if test class not found
     */
    public Class<?> findTestClass(Resource resource, String href) throws ClassNotFoundException {
        String name = resource.getName();
        Resource hrefResource = resource.getParent().getRelativeResource(href);
        name = hrefResource.getPath().replaceFirst("/", "").replace("/", ".").replaceAll("\\.html$", "");
        Class<?> concordionClass;
        try {
            concordionClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            concordionClass = Class.forName(name + "Test");
        }
        return concordionClass;
    }

    protected RunnerResult runTestClass(Class<?> concordionClass) throws Exception {
        org.junit.runner.Result jUnitResult = runJUnitClass(concordionClass);
        return decodeJUnitResult(concordionClass, jUnitResult);
    }

    protected org.junit.runner.Result runJUnitClass(Class<?> concordionClass) {
    	Stats.initFailCount(concordionClass);
        org.junit.runner.Result jUnitResult = JUnitCore.runClasses(concordionClass);
        return jUnitResult;
    }

    protected RunnerResult decodeJUnitResult(Class<?> concordionClass, org.junit.runner.Result jUnitResult)
            throws Exception {
        if (jUnitResult.wasSuccessful()) {
            if (isOnlySuccessfulBecauseItWasExpectedToFail(concordionClass)
             || isOnlySuccessfulBecauseItIsUnimplemented(concordionClass)) {
            	return new RunnerResult(Result.IGNORED);
            }
            if (jUnitResult.getIgnoreCount() > 0) {
            	return new RunnerResult(Result.IGNORED);
            }
        } else {
            List<Failure> failures = jUnitResult.getFailures();
            for (Failure failure : failures) {
                Throwable exception = failure.getException();
                if (!(exception instanceof AssertionError)) {
                    logger.log(Level.WARNING, "", exception);
                    if (exception instanceof Exception) {
                        throw (Exception)exception;
                    } else {
                        throw new RuntimeException(exception);
                    }
                }
            }
        }
        //int failCount = jUnitResult.getFailures().size();
        int failCount = Stats.getFailCount(concordionClass);
        if(failCount == 0) failCount = jUnitResult.getFailures().size();
        int ignoreCount = jUnitResult.getIgnoreCount();
        int successCount = jUnitResult.getRunCount() - ignoreCount - failCount;
        int manFailCount = Stats.getManualFailCount(concordionClass);
        
        Stats.removeFailCount(concordionClass);
        
        List<Result> results = createMultipleResults(Result.FAILURE,failCount);
        results.addAll(createMultipleResults(Result.IGNORED,ignoreCount));
        results.addAll(createMultipleResults(Result.SUCCESS,successCount));
        results.addAll(createMultipleResults(Result.MANUAL_FAIL,manFailCount));
        return new RunnerResult(results);
    }
    
    private List<Result> createMultipleResults(Result resultType,int number)
    {
    	List<Result> results = new ArrayList<Result>();
    	if (number<=0) return results;
    	for(int i=0; i<number;i++)
    	{
    		results.add(resultType);
    	}
    	return results;
    }

    private boolean isOnlySuccessfulBecauseItWasExpectedToFail(Class<?> concordionClass) {
        return concordionClass.getAnnotation(ExpectedToFail.class) != null;
    }
    
    private boolean isOnlySuccessfulBecauseItIsUnimplemented(Class<?> concordionClass) {
        return concordionClass.getAnnotation(Unimplemented.class) != null;
    }
}
