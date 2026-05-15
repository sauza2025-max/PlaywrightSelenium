package listeners;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.DriverManager;
import utils.PageManager;
import org.testng.ISuite;

import java.io.*;
import java.util.Properties;

public class AllureListener implements ITestListener, ISuiteListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        attachScreenshot("Screenshot - PASSED - " + result.getName());
        cleanup();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachScreenshot("Screenshot - FAILED - " + result.getName());
        cleanup();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        attachScreenshot("Screenshot - SKIPPED - " + result.getName());
        cleanup();
    }

    @Override
    public void onFinish(ISuite suite) {
        try {
            Properties props = new Properties();
            props.setProperty("Browser", "Chrome");
            // ✅ Read the stored version, fallback to Unknown if not set
            props.setProperty("Browser.Version", System.getProperty("browser.version", "Unknown"));
            props.setProperty("Environment", "QA");
            props.setProperty("Base.URL", "https://your-app.com");
            props.setProperty("OS", System.getProperty("os.name"));

                // ✅ Changed to target/allure-results
            File allureResultsDir = new File(System.getProperty("user.dir") + "/target/allure-results");
            if (!allureResultsDir.exists()) allureResultsDir.mkdirs();

            File envFile = new File(allureResultsDir, "environment.properties");
            try (FileOutputStream fos = new FileOutputStream(envFile)) {
                props.store(fos, "Allure Environment");
            }
            System.out.println(">>> Allure env file created: " + envFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void attachScreenshot(String name) {
        // ✅ Try Playwright first
        Page page = PageManager.getPage();
        if (page != null) {
            try {
                byte[] screenshot = page.screenshot(
                        new Page.ScreenshotOptions().setFullPage(true)
                );
                Allure.addAttachment(name, "image/png",
                        new ByteArrayInputStream(screenshot), "png");
                return; // done, skip Selenium
            } catch (Exception e) {
                System.err.println("Playwright screenshot failed: " + e.getMessage());
            }
        }

        // ✅ Fall back to Selenium
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, "image/png",
                        new ByteArrayInputStream(screenshot), "png");
            } catch (Exception e) {
                System.err.println("Selenium screenshot failed: " + e.getMessage());
            }
        }
    }

    private void cleanup() {
        // Close Playwright context if open
        Page page = PageManager.getPage();
        if (page != null) {
            try {
                page.context().close();
            } catch (Exception ignored) {}
            PageManager.removePage();
        }

        // Quit Selenium driver if open
        DriverManager.quitDriver();
    }
}