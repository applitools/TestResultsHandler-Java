import ApplitoolsTestResultHandler.ApplitoolsTestResultsHandler;
import ApplitoolsTestResultHandler.ResultStatus;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.Eyes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DownloadDiffExample {

    public static void main(String[] args) throws Exception {

        WebDriver driver = new ChromeDriver();
        Eyes eyes = new Eyes();

        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
       

        try {
            //Turn on Eyes logs
            eyes.setLogHandler(new StdoutLogHandler(true));

            eyes.open(driver, "App Name", "Test Name", new RectangleSize(800, 600));

            // Navigate the browser to the "hello world!" web-site.
            driver.get("https://applitools.com/helloworld");

            // Visual checkpoint #1.
            eyes.checkWindow("Hello World");

            driver.get("https://applitools.com/helloworld?diff1");

            eyes.checkWindow("Diff 1");

            // End visual testing. Validate visual correctness.
            TestResults testResult = eyes.close(false);

            //Link to batch result.
            System.out.println("This is the link for the Batch Result: " + testResult.getUrl());

            //Constructor to use without proxy configuration
            ApplitoolsTestResultsHandler testResultHandler = new ApplitoolsTestResultsHandler(testResult,
                    System.getenv("APPLITOOLS_VIEW_KEY"), System.getenv("APPLITOOLS_API_KEY"), System.getenv("APPLITOOLS_WRITE_KEY"));

            //Constructor to use if using a proxy with a server URL and a Port.
//            ApplitoolsTestResultsHandler testResultHandler = new ApplitoolsTestResultsHandler(testResult,
//            System.getenv("APPLITOOLS_VIEW_KEY"),"ProxyServerURL","ProxyServerPort", System.getenv("APPLITOOLS_API_KEY"), System.getenv("APPLITOOLS_WRITE_KEY"));

            //Constructor to use if using a proxy with a server URL and a Port, and username and password
//            ApplitoolsTestResultsHandler testResultHandler = new ApplitoolsTestResultsHandler(testResult,
//            System.getenv("APPLITOOLS_VIEW_KEY"),"ProxyServerURL","ProxyServerPort","ProxyServerUserName","ProxyServerPassword", System.getenv("APPLITOOLS_API_KEY"), System.getenv("APPLITOOLS_WRITE_KEY"));


            List<BufferedImage>  base = testResultHandler.getBaselineBufferedImages();  // get Baseline Images as BufferedImage
            List<BufferedImage>  curr = testResultHandler.getCurrentBufferedImages();   // get Current Images as BufferedImage
            List<BufferedImage> diff = testResultHandler.getDiffsBufferedImages();      // get Diff Images as BufferedImage


            // Optional Setting this prefix will determine the structure of the repository for the downloaded images.
//            testResultHandler.SetPathPrefixStructure("TestName/AppName/viewport/hostingOS/hostingApp");

            //Link to test/step result
            System.out.println("This is the url to the first step " +testResultHandler.getLinkToStep(1));

            testResultHandler.downloadImages(System.getenv("PathToDownloadImages"));                // Download both the Baseline and the Current images to the folder specified in Path.
            testResultHandler.downloadBaselineImages(System.getenv("PathToDownloadImages"));      // Download only the Baseline images to the folder specified in Path.
            testResultHandler.downloadCurrentImages(System.getenv("PathToDownloadImages"));       // Download only the Current images to the folder specified in Path.
            testResultHandler.downloadDiffs(System.getenv("PathToDownloadImages"));                 // Download Diffs to the folder specified in Path.
            testResultHandler.downloadAnimateGiff(System.getenv("PathToDownloadImages"));           // Download Animated GIf to the folder specified in Path.

            //Create a list of desired statuses to be automatically saved
            List<ResultStatus> desiredStatuses = new ArrayList<>();
            desiredStatuses.add(ResultStatus.NEW);
            desiredStatuses.add(ResultStatus.PASSED);

            //Accepting changes to steps with the desired statuses
            testResultHandler.acceptChanges(desiredStatuses);

            //Get Steps Names
            String[] names = testResultHandler.getStepsNames();

            //Get the status of each step (Pass / Unresolved / New / Missing).
            ResultStatus[] results = testResultHandler.calculateStepResults();
            for (int i=0 ; i< results.length; i++){
                System.out.println("The result of step "+(i+1)+" is "+ results[i]);
            }

        }

        finally {
            // Abort Session in case of an unexpected error.
            eyes.abortIfNotClosed();
            driver.close();
        }
    }
}
