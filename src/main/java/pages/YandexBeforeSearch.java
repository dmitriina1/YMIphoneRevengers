package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import helpers.Assertions;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

/**
 * Класс {@code YandexBeforeSearch} предназначен для работы с главной страницей Яндекс.Маркета.
 * Он предоставляет методы для взаимодействия с каталогом, включая выбор категории и подкатегории.
 *
 * @author Наливайко Дмитрий
 *
 * */
public class YandexBeforeSearch extends BasePage {

    /**
     * Метод {@code openSite} выполняет открытие сайта и проверку заголовков.
     *
     * @param url ссыдка на сайт
     * @param title ожидаемый заголовок сайта
     * @param typeNextPage класс следующей вызываемой страницы
     */
    @Step("Переход на сайт: {url}")
    public <T extends BasePage> T openSite(String url, String title, Class<T> typeNextPage) {
        open(url);
        Assertions.assertTrue(Wait().withTimeout(Duration.ofSeconds(15))
                        .until(driver -> driver.getTitle().contains(title)),
                "Заголовок страницы не содержит ожидаемый текст.\n" +
                        "Ожидалось: '" + title + "'\n" +
                        "Фактический заголовок: '" + webdriver().driver().getWebDriver().getTitle() + "'");
        return typeNextPage.cast(page(typeNextPage));
    }

    /**
     * Метод {@code findCatalog} выполняет поиск и нажатие по кнопке каталога.
     */
    @Step("Нажатие на каталог")
    public YandexBeforeSearch findCatalog() {
        $x("//div[(@data-baobab-name='catalog')]")
                .shouldBe(Condition.enabled, Duration.ofSeconds(15)).click();
        return this;
    }

    /**
     * Метод {@code catalogContentMouseOver} выполняет поиск и наведение на категорию из каталога.
     *
     * @param catalogContent искомая категория
     */
    @Step("Наведение на категорию {catalogContent} в Каталоге")
    public YandexBeforeSearch catalogContentMouseOver(String catalogContent) {
        $x(String.format("//div[contains(@data-zone-name, 'catalog-content')]//span[contains(text(), '%s')]",
                catalogContent)).shouldBe(Condition.enabled, Duration.ofSeconds(15)).hover();
        return this;
    }

    /**
     * Метод {@code catalogSubItemClick} выполняет поиск и нажатие на подкатегорию из каталога.
     *
     * @param catalogSubItem искомая подкатегория
     * @param typeNextPage класс следующей вызываемой страницы
     *
     * @author Наливайко Дмитрий
     */
    @Step("Нажатие и поиск подкатегории {catalogSubItem} в Каталоге")
    public <T extends BasePage> T goToСatalogSubItem(String catalogSubItem, Class<T> typeNextPage) {
        $x(String.format(
                "//li//a[contains(text(), '%s')]",
                catalogSubItem)).shouldBe(Condition.enabled, Duration.ofSeconds(15)).click();
        return typeNextPage.cast(page(typeNextPage));
    }
}
