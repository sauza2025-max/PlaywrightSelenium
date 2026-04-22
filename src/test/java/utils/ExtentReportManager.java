package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.nio.file.Paths;

/**
 * ExtentReportManager
 * -------------------
 * Singleton that initializes and manages the Extent HTML report.
 * Both Playwright and Selenium tests share the same report instance.
 *
 * Usage:
 *   ExtentReportManager.getInstance()          -> get the ExtentReports object
 *   ExtentReportManager.createTest("name")     -> create a new test node
 *   ExtentReportManager.flush()                -> write the report to disk
 */
public class ExtentReportManager {

    private static ExtentReports extent;

    // Use absolute path based on working directory -- avoids directory-not-found errors
    private static final String REPORT_DIR  = System.getProperty("user.dir") + File.separator + "test-output";
    private static final String REPORT_PATH = REPORT_DIR + File.separator + "ExtentReport.html";

    // Thread-safe test storage (important for parallel test runs)
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    private ExtentReportManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            // Ensure output directory exists before ExtentSparkReporter tries to write
            new File(REPORT_DIR).mkdirs();
            new File(REPORT_DIR + File.separator + "screenshots").mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
            spark.config().setDocumentTitle("Test Automation Report");
            spark.config().setReportName("Playwright + Selenium Learning Suite");
            spark.config().setTheme(Theme.DARK);
            spark.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Author",      "QA Team");
            extent.setSystemInfo("Environment", "Learning");
            extent.setSystemInfo("Java",        System.getProperty("java.version"));
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        currentTest.set(test);
        return test;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        currentTest.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return currentTest.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
