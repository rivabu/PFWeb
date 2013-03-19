import junit.framework.Test;
import junit.framework.TestSuite;

import com.thoughtworks.selenium.SeleneseTestCase;

public class GoogleSearch extends SeleneseTestCase {
    public void setUp() throws Exception {
        setUp("http://www.google.com.au/", "*firefox");

    }

    public void testSimplesearch() throws Exception {
        selenium.setTimeout("1000000");
        // this is used when you get the error message
        // thoughtworks.selenium.SeleniumException: Timed out after 30000ms

        selenium.open("/");
        selenium.type("sf", "Selenium IDE");
        selenium.click("btnG");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Selenium IDE"));
    }

    public static Test suite() {
        // method added
        return new TestSuite(GoogleSearch.class);
    }

    public void tearDown() {
        // Added . Will be called when the test will complete
        selenium.stop();
    }

    public static void main(String args[]) {
        // Added. Execution will started from here.
        junit.textui.TestRunner.run(suite());
    }
}
