package listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.SeleniumBaseTest;
import utils.ScreenshotUtil;
import com.aventstack.extentreports.MediaEntityBuilder;

public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        attachScreenshot(result, "Pass screenshot");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachScreenshot(result, "Failure screenshot");
    }

    private void attachScreenshot(ITestResult result, String label) {
        Object instance = result.getInstance();
        if (!(instance instanceof SeleniumBaseTest base)) return; // not a Selenium test, skip

        WebDriver driver = base.getDriver();
        if (driver == null) return;

        String path = ScreenshotUtil.capture(driver, result.getName());
        if (path == null) return;

        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                base.test.fail(label,
                        MediaEntityBuilder.createScreenCaptureFromPath(path).build());
            } else {
                base.test.pass(label,
                        MediaEntityBuilder.createScreenCaptureFromPath(path).build());
            }
        } catch (Exception e) {
            System.err.println("Could not attach screenshot: " + e.getMessage());
        }
    }
}