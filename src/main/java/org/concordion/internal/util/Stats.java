package org.concordion.internal.util;

import java.util.HashMap;
import java.util.Map;

/*
 * A regrettable singleton for tracking counts of failures and manual tests
 */
public class Stats 
{
	private static Map<Class<?>,Integer> failCountMap = new HashMap<Class<?>,Integer>();
	private static Map<Class<?>,Integer> manFailCountMap = new HashMap<Class<?>,Integer>();
	 
    public static void initFailCount(Class<?> clazz)
    {
    	failCountMap.put(clazz,0);
    	manFailCountMap.put(clazz,0);
    }
    
    public static void addFail()
    {
    	for(Class<?> clazz : failCountMap.keySet())
    	{
    		addFail(clazz);
    	}
    }
    
    public static void addFail(Class<?> clazz)
    {
    	Integer count = 0;
    	if(failCountMap.containsKey(clazz))
    	{
    		count = failCountMap.get(clazz);
    	}
    	failCountMap.put(clazz,count+1);
    }
    
    public static void minusFail()
    {
    	for(Class<?> clazz : failCountMap.keySet())
    	{
    		minusFail(clazz);
    	}
    }
    
    public static void minusFail(Class<?> clazz)
    {
    	Integer count = 0;
    	if(failCountMap.containsKey(clazz))
    	{
    		count = failCountMap.get(clazz);
    	}
    	failCountMap.put(clazz,count-1);
    }
    
    public static void addManualFail()
    {
    	for(Class<?> clazz : manFailCountMap.keySet())
    	{
    		addManualFail(clazz);
    	}
    }
    
    public static void addManualFail(Class<?> clazz)
    {
    	Integer count = 0;
    	if(manFailCountMap.containsKey(clazz))
    	{
    		count = manFailCountMap.get(clazz);
    	}
    	manFailCountMap.put(clazz,count+1);
    }
    
    public static int getFailCount(Class<?> clazz)
    {
    	if(failCountMap.containsKey(clazz))
    	{
    		return failCountMap.get(clazz);
    	}
    	return 0;
    }
    
    public static int getManualFailCount(Class<?> clazz)
    {
    	if(manFailCountMap.containsKey(clazz))
    	{
    		return manFailCountMap.get(clazz);
    	}
    	return 0;
    }
    
    public static void removeFailCount(Class<?> clazz)
    {
    	if(failCountMap.containsKey(clazz))
    	{
    		failCountMap.remove(clazz);
    		manFailCountMap.remove(clazz);
    	}
    }
}
