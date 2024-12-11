package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
//    Goal: Automatically retry flaky tests
    private int retryCount = 0;
    private static final int maxRetryCount = 3;

    @Override
    public boolean retry(ITestResult result) {
        System.out.println("Retrying test: " + result.getName() + ", Attempt: " + (retryCount + 1));
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }

}