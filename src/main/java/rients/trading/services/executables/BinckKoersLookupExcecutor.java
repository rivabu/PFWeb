package rients.trading.services.executables;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.Wait.WaitTimedOutException;

public class BinckKoersLookupExcecutor {

    private static final String WAIT_TIME = "500";

    public String getBaseurl() {
        return "https://login.binck.nl";
    }

    protected WebDriver driver;
    protected WebDriverBackedSelenium selenium;

    /**
     * The firefox driver.
     * 
     * @return driver The firefox driver.
     */
    private WebDriver firefoxDriver() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("javascript.enabled", true);
        return new FirefoxDriver(profile);
    }

    public BinckKoersLookupExcecutor() {
        driver = firefoxDriver();
        selenium = new WebDriverBackedSelenium(driver, getBaseurl());
    }

    @Test
    public void process() {

        selenium.open("/klanten/Login.aspx?language=");
        selenium.waitForPageToLoad("500");
        selenium.type("id=ctl00_Content_Gebruikersnaam", "252351789");
        selenium.type("id=ctl00_Content_Wachtwoord", "RientsSuzan");
        selenium.click("css=#ctl00_Content_LoginButton > span");
        boolean multiple = true;
        String biedPrijs = "";
        while (true) {
            try {
                biedPrijs = handleLookup(multiple, "Brent Crude Oil Future May RBS TS 127.43", "541292");
                if (Double.parseDouble(biedPrijs) < 9) {
                    sell("Brent Crude Oil Future May RBS TS 127.43", "541292", 400, 9);
                }
                biedPrijs = handleLookup(multiple, "Goud RBS Turbo Short 1693", "553810");
                if (Double.parseDouble(biedPrijs) < 5) {
                    sell("Goud RBS Turbo Short 1693", "553810", 300, 5);
                }
          
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            } finally {
                sleep();
            }
        }
    }
    
    private void sleep() {
        try {
        Thread.sleep(60000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sell(String name, String fundCode, int aantal, double prijs) {
        System.out.println("about to sell: " + name + " prijs: " + prijs + " aantal: " + aantal);
        selenium.open("/klanten/Fondsinformatie/Overzicht.aspx?fondsId=" + fundCode);
        selenium.waitForPageToLoad(WAIT_TIME);
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String biedPrijs = selenium.getText("//*[@id=\"bied$" + fundCode + "\"]");
        biedPrijs = biedPrijs.replace(",", ".");
        if (Double.parseDouble(biedPrijs) < prijs) {
            selenium.click("css=#ctl00_ctl00_ctl00_Content_Content_FondsHeader_VerkopenButton > span");
            selenium.waitForPageToLoad("30000");
            selenium.type("id=ctl00_ctl00_Content_Content_Aantal", aantal + "");
            selenium.select("id=ctl00_ctl00_Content_Content_Prijsconditie", "label=Bestens");
            selenium.click("css=#ctl00_ctl00_Content_Content_ctl11_ControleerOrder > span");
            selenium.waitForPageToLoad("30000");
            selenium.click("css=#ctl00_ctl00_Content_Content_Akkoord > span");
            selenium.waitForPageToLoad("30000");
            System.out.println("sold: " + name + " prijs: " + biedPrijs + " aantal: " + aantal);
        } else {
            System.out.println("not sold: " + name + " prijs: " + prijs + " aantal: " + aantal);
        }
    }

    private String handleLookup(boolean lookupURL, String name, String fundCode) {
        String biedPrijs = "";
        try {
            if (lookupURL) {
                selenium.open("/klanten/Fondsinformatie/Overzicht.aspx?fondsId=" + fundCode);
                selenium.waitForPageToLoad(WAIT_TIME);
            }
            biedPrijs = selenium.getText("//*[@id=\"bied$" + fundCode + "\"]");
            String biedtijd = selenium.getText("//*[@id=\"biedtijd$" + fundCode + "\"]");
            System.out.println(name + " " + biedtijd + " " + biedPrijs);
        } catch (SeleniumException se) {
            se.printStackTrace();
        } catch (WaitTimedOutException wto) {
            wto.printStackTrace();
        }
        biedPrijs = biedPrijs.replace(",", ".");
        return biedPrijs;

    }

    public static void main(String[] args) {
        BinckKoersLookupExcecutor executor = new BinckKoersLookupExcecutor();
        executor.process();
    }
}
