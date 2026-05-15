package utils;

import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

// src/main/java/listeners/ScreenshotListener.java
public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        WebDriver driver = ((SeleniumBaseTest) instance).getDriver(); // adjust to your base class

        String path = ScreenshotUtil.capture(driver, result.getName());
        System.out.println("Screenshot saved: " + path);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // optional: capture on pass too (see Step 3)
    }
}
