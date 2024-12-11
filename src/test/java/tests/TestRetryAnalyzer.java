package tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.RetryAnalyzer;

@Listeners(utils.RetryListener.class)
public class TestRetryAnalyzer {
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testExample() {
        Assert.assertTrue(false); // Retry this test
    }

    @Test(retryAnalyzer = utils.RetryAnalyzer.class)
    public void sampleTest() {
        System.out.println("Executing test...");
        Assert.fail("Failing test to trigger retry");
    }
}
