package helpers;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.SelenideLog;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.selenide.AllureSelenide;
import io.qameta.allure.selenide.LogType;
import io.qameta.allure.util.ResultsUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Класс {@code CustomAllureSelenide} расширяет функциональность интеграции Selenide с Allure.
 * Позволяет кастомизировать логирование шагов, скриншотов и данных страницы.
 *
 * @author Наливайко Дмитрий
 */
public class CustomAllureSelenide extends AllureSelenide {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureSelenide.class);
    private boolean saveScreenshots;
    private boolean savePageHtml;
    private boolean includeSelenideLocatorsSteps;
    private final Map<LogType, Level> logTypesToSave;
    private final AllureLifecycle lifecycle;

    /**
     * Конструктор {@code CustomAllureSelenide} с жизненным циклом Allure по умолчанию.
     */
    public CustomAllureSelenide() {
        this(Allure.getLifecycle());
    }

    /**
     * Конструктор {@code CustomAllureSelenide} с указанным жизненным циклом Allure.
     *
     * @param lifecycle объект {@link AllureLifecycle} для управления отчётом.
     */
    public CustomAllureSelenide(AllureLifecycle lifecycle) {
        this.saveScreenshots = true;
        this.savePageHtml = true;
        this.includeSelenideLocatorsSteps = true;
        this.logTypesToSave = new HashMap();
        this.lifecycle = lifecycle;
    }

    /**
     * Метод {@code getScreenshotBytes} возвращает скриншот страницы в виде массива байт.
     *
     * @return Optional с байтами скриншота или пустой, если драйвер не запущен
     */
    private static Optional<byte[]> getScreenshotBytes() {
        try {
            return WebDriverRunner.hasWebDriverStarted() ? Optional.of(((TakesScreenshot)WebDriverRunner
                    .getWebDriver()).getScreenshotAs(OutputType.BYTES)) : Optional.empty();
        } catch (WebDriverException var1) {
            LOGGER.warn("Could not get screen shot", var1);
            return Optional.empty();
        }
    }

    /**
     * Метод {@code getPageSourceBytes} возвращает исходный код страницы в виде байт.
     *
     * @return Optional с байтами HTML-страницы или пустой, если драйвер не запущен
     */
    private static Optional<byte[]> getPageSourceBytes() {
        try {
            return WebDriverRunner.hasWebDriverStarted() ? Optional.of(WebDriverRunner.getWebDriver()
                    .getPageSource().getBytes(StandardCharsets.UTF_8)) : Optional.empty();
        } catch (WebDriverException var1) {
            LOGGER.warn("Could not get page source", var1);
            return Optional.empty();
        }
    }

    /**
     * Метод {@code getBrowserLogs} возвращает логи браузера указанного типа и уровня.
     *
     * @param logType тип логов
     * @param level уровень логов
     *
     * @return строка с логами
     */
    private static String getBrowserLogs(LogType logType, Level level) {
        return String.join("\n\n", Selenide.getWebDriverLogs(logType.toString(), level));
    }

    /**
     * Метод {@code afterEvent} обрабатывает события Selenide после их выполнения.
     * Добавляет в отчёт Allure скриншоты, исходный код страницы и логи.
     *
     * @param event событие логирования
     */
    @Override
    public void afterEvent(LogEvent event) {
        this.lifecycle.getCurrentTestCaseOrStep().ifPresent((parentUuid) -> {
            if (this.saveScreenshots) {
                getScreenshotBytes().ifPresent((bytes) -> {
                    this.lifecycle.addAttachment("Screenshot", "image/png", "png", bytes);
                });
            }

            if (this.savePageHtml) {
                getPageSourceBytes().ifPresent((bytes) -> {
                    this.lifecycle.addAttachment("Page source", "text/html", "html", bytes);
                });
            }

            if (!this.logTypesToSave.isEmpty()) {
                this.logTypesToSave.forEach((logType, level) -> {
                    byte[] content = getBrowserLogs(logType, level).getBytes(StandardCharsets.UTF_8);
                    this.lifecycle.addAttachment("Logs from: " + logType,
                            "application/json", ".txt", content);
                });
            }

        });


        if (this.stepsShouldBeLogged(event)) {
            this.lifecycle.getCurrentTestCaseOrStep().ifPresent((parentUuid) -> {
                switch(event.getStatus()) {
                    case PASS:
                        this.lifecycle.updateStep((step) -> {
                            step.setStatus(Status.PASSED);
                        });
                        break;
                    case FAIL:
                        this.lifecycle.updateStep((stepResult) -> {
                            stepResult.setStatus((Status) ResultsUtils.getStatus(event.getError())
                                    .orElse(Status.BROKEN));
                            stepResult.setStatusDetails((StatusDetails)ResultsUtils.
                                    getStatusDetails(event.getError()).orElse(new StatusDetails()));
                        });
                        break;
                    default:
                        LOGGER.warn("Step finished with unsupported status {}", event.getStatus());
                }

                this.lifecycle.stopStep();
            });
        }

    }

    /**
     * Метод {@code stepsShouldBeLogged} определяет, нужно ли логировать событие.
     *
     * @param event событие логирования
     *
     * @return true, если событие должно быть записано в отчёт, иначе false
     */
    private boolean stepsShouldBeLogged(LogEvent event) {
        return this.includeSelenideLocatorsSteps || !(event instanceof SelenideLog);
    }
}
