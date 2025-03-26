package yandexm.iphone;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.CustomAllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Selenide.webdriver;

public class BaseTest {

    @BeforeAll
    public static void setup(){
        SelenideLogger.addListener("AllureSelenide",
                new CustomAllureSelenide().screenshots(true).savePageSource(true));
    }

    @BeforeEach
    public void option(){
        Configuration.timeout =30000;
        Configuration.browser = "chrome";
        Configuration.browserSize = null;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-extensions", "--start-maximized");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "none");
        Configuration.browserCapabilities = capabilities;
    }

    @AfterEach
    public void close() {
        com.codeborne.selenide.Selenide.closeWebDriver();
    }
}
