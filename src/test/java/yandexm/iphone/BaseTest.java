package yandexm.iphone;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.CustomAllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import static helpers.Properties.testsProperties;

/**
 * Класс {@code BaseTest} содержит базовые настройки для тестов,
 * включая конфигурацию браузера и обработку логов.
 *
 * @author Наливайко Дмитрий
 */
public class BaseTest {

    /**
     * Метод {@code setup} настраивает сохранение скриншотов и исходного кода страницы.
     */
    @BeforeAll
    public static void setup() {
        SelenideLogger.addListener("AllureSelenide",
                new CustomAllureSelenide().screenshots(true).savePageSource(true));
    }

    /**
     * Метод {@code option} настраивает параметры браузера перед каждым тестом:
     * - Таймаут ожидания: 30 секунд
     * - Браузер: Chrome
     * - Отключение расширений
     * - Стратегия загрузки страницы: "none"
     */
    @BeforeEach
    public void option() {
        Configuration.timeout = testsProperties.defaultTimeout();
        Configuration.browser = testsProperties.defaultBrowser();
        Configuration.browserSize = null;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-extensions", "--start-maximized");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "none");
        Configuration.browserCapabilities = capabilities;
    }

    /**
     * Метод {@code close} закрывает веб-драйвер после каждого теста.
     */
    @AfterEach
    public void close() {
        com.codeborne.selenide.Selenide.closeWebDriver();
    }
}
