import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

public class PhilosophieWebdriver {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    private Pattern bracketPattern = Pattern.compile("(\\(([^)]*)\\)|\\[([^)]*)\\])");
    private String xpathQuery = "//a[ancestor::div[@class='mw-content-ltr'] and not(ancestor::table) and text() != '' and not(ancestor::div[@class='thumb tright']) and not(ancestor::i) and @href[starts-with(., '/wiki/')]]";
    List<String> breadcrumbs;
    private int maxLinkCount;
    private int maxIterations;

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.firefox.bin", "D:\\Programme\\Mozilla Firefox\\firefox.exe");
        driver = new FirefoxDriver();
    //    baseUrl = "http://de.wikipedia.org/wiki/Tee";
      //  baseUrl = "http://de.wikipedia.org/wiki/Anton_Schwob";
        baseUrl = "http://de.wikipedia.org/wiki/Spezial:Zuf%C3%A4llige_Seite";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();
        maxLinkCount = 100;
        maxIterations = 100;
    }

    @Test
    public void testPhilosophie() throws Exception {

        int iterations = 1;
        do {

            driver.navigate().to(baseUrl);
            breadcrumbs = new ArrayList<>();

            boolean aborted = false;
            int followedLinks = 0;
            while (!driver.getTitle().startsWith("Philosophie") && !aborted && followedLinks < maxLinkCount) {
                if (PageManager.getInstance().pageIsDeadEnd(driver.getCurrentUrl())) {
                    aborted = true;
                    System.out.println("Search aborted (previous search came to a dead end here)");
                } else {
                    System.out.println(driver.getTitle());
                    String linkText = findLinkText();
                    if (linkText != null) {
                        breadcrumbs.add(0, driver.getCurrentUrl());
                        driver.findElement(By.linkText(linkText)).click();
                        followedLinks++;
                    } else {
                        aborted = true;
                        System.out.println("Search aborted (Loop detected).");
                    }
                }
            }

            System.out.println(followedLinks);

            if (!aborted && followedLinks < 15) {
                breadcrumbs.add(0, driver.getCurrentUrl());
                updatePagesSuccessful(breadcrumbs);
            } else {
                updatePagesUnsuccessful(breadcrumbs);
            }

            PageManager.getInstance().persistPages();

            iterations++;
        } while (iterations < maxIterations);

        for (Page page : PageManager.getInstance().getAllPages()) {
            System.out.println(String.format("%s -> %s (%s)", page.getUrl(), page.getLinksToDestinationCount(),
                    page.getDestinationReacher()));
        }

    }

    private void updatePagesUnsuccessful(List<String> breadcrumbs) {
        for (int i = 0; i < breadcrumbs.size(); i++) {
            PageManager.getInstance().updatePage(breadcrumbs.get(i), DestinationReacher.IMPOSSIBLE);
        }
    }

    private void updatePagesSuccessful(List<String> breadcrumbs) {
        for (int i = 0; i < breadcrumbs.size(); i++) {
            PageManager.getInstance().updatePage(breadcrumbs.get(i), DestinationReacher.POSSIBLE, i);
        }
    }

    private String findLinkText() {
        String linkText = null;
        if (!breadcrumbs.contains(driver.getCurrentUrl())) {
            Page page = new Page(driver.getCurrentUrl());
            PageManager.getInstance().addPage(page);
            boolean found = false;
            for (WebElement element : driver.findElements(By.xpath(xpathQuery))) {
                if (!found) {
                    linkText = element.getText();
                    if (!isInBrackets(element.findElement(By.xpath("..")).getText(), linkText)) {
                        found = true;
                    } else {
                        linkText = null;
                    }
                }
            }
        }
        return linkText;
    }

    private boolean isInBrackets(String paragraph, String text) {
        boolean inBrackets = false;
        Matcher matcher = bracketPattern.matcher(paragraph);
        if (matcher.find()) {
            String textInBrackets = "";
            int groupCount = matcher.groupCount();
            for (int i = 0; i < groupCount; i++) {
                if (!inBrackets) {
                    textInBrackets = matcher.group(i);
                    if (textInBrackets != null && textInBrackets.contains(text)) {
                        inBrackets = true;
                    }
                }
            }
        }
        return inBrackets;
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}
