package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// src/main/java/utils/ScreenshotUtil.java
public class ScreenshotUtil {

    public static String capture(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String dir = "target/screenshots/";

        new File(dir).mkdirs();

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destPath = dir + fileName;
        try {
            FileUtils.copyFile(src, new File(destPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destPath; // return path so reporters can embed it
    }
}
