package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class YandexBeforeSearch extends BasePage {

    private Actions actions;

    @Step("Переходим на сайт: {url}")
    public <T extends BasePage> T openSite(String url, String title, Class<T> typeNextPage) {
        open(url);
        Wait().withTimeout(Duration.ofSeconds(15)).until(driver -> driver.getTitle().contains(title));
        return typeNextPage.cast(page(typeNextPage));
    }

    @Step("Нажимаем на каталог")
    public YandexBeforeSearch findCatalog() {
        $x("//div[(@data-baobab-name='catalog')]")
                .shouldBe(Condition.enabled, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Наведение на категорию {catalogContent} в Каталоге")
    public YandexBeforeSearch catalogContentMouseOver(String catalogContent) {
        $x(String.format("//div[contains(@data-zone-name, 'catalog-content')]//span[contains(text(), '%s')]",
                catalogContent)).shouldBe(Condition.enabled, Duration.ofSeconds(15)).hover();
        return this;
    }

    @Step("Нажатие и поиск подкатегории {catalogSubItem} в Каталоге")
    public <T extends BasePage> T goToСatalogSubItem(String catalogSubItem, Class<T> typeNextPage){
        $x(String.format(
                "//li//a[contains(text(), '%s')]",
                catalogSubItem)).shouldBe(Condition.enabled, Duration.ofSeconds(15)).click();
        return typeNextPage.cast(page(typeNextPage));
    }
}
