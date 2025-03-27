package pages;

import com.codeborne.selenide.*;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Класс {@code YandexAfterSearch} предназначен для обработки результатов поиска YandexMarket.
 * Он предоставляет методы для получения элементов, их обработки и поиска на сайте.
 *
 * @author Наливайко Дмитрий
 */
public class YandexAfterSearch extends BasePage {

    /**
     * xPath для поля ввода бренда
     */
    private final String BRAND_SEARCH_LOCATOR = "//div[contains(@data-zone-data, 'Бренд')]//input";

    /**
     * xPath для видимых элементов фильтра брендов
     */
    private final String BRAND_SHOWN_LOCATOR = "//div[contains(@data-zone-data, 'Бренд')]" +
            "//div[contains(@data-zone-name, 'FilterValue')]";

    /**
     * xPath для кнопки Показать всё
     */
    private final String SHOW_MORE_LOCATOR = "//div[contains(@data-baobab-name, 'showMoreFilters')]";

    /**
     * xPath для элементов поиска
     */
    private final String ELEMENTS_LOCATOR = "//div[contains(@data-auto, 'SerpList')]" +
            "//div[contains(@data-auto-themename, 'listDetailed')]";

    /**
     * Метод {@code checkingCatalogSubItemTitle} ожидает появления названия подкатегории на странице.
     * Получает ее название и сравнивает с {@code catalogSubItem}.
     *
     * @param catalogSubItem ожидаемое название подкатегории
     */
    @Step("Проверка что перешел в раздел {catalogSubItem}")
    public YandexAfterSearch checkingCatalogSubItemTitle(String catalogSubItem) {
        $x("//div[contains(@data-zone-name, 'searchTitle')]//h1")
                .shouldBe(Condition.enabled, Duration.ofSeconds(15))
                .shouldHave(Condition.text(catalogSubItem));
        return this;
    }

    /**
     * Метод {@code inputBrands} выбирает подходящих производителей в фильтре на сайте.
     * Если подходящих производителей не видно - нажимает на кнопку Показать всё
     * и вводит поочередно искомых производителей
     *
     * @param brands список необходимых производителей
     */
    @Step("Выбираем производителей {brands} в фильтре")
    public YandexAfterSearch inputBrands(List<String> brands) {
        List<String> tempBrands = new ArrayList<>(brands);

        for (String brand : brands) {
            String brandLocator = BRAND_SHOWN_LOCATOR + "//span[contains(text(), '" + brand + "')]";
            if ($x(brandLocator).exists()) {
                while (!"true".equals($x(brandLocator + "/../..").getAttribute("aria-checked"))) {
                    $x(brandLocator).click();
                }
                tempBrands.remove(brand);

            }
        }
        if (!tempBrands.isEmpty() && $(By.xpath(SHOW_MORE_LOCATOR)).exists()) {
            $(By.xpath(SHOW_MORE_LOCATOR)).click();

            for (String brand : tempBrands) {
                $x(BRAND_SEARCH_LOCATOR).clear();
                $x(BRAND_SEARCH_LOCATOR).setValue(brand);
                $x(BRAND_SHOWN_LOCATOR + "//span[contains(text(), '" + brand + "')]")
                        .shouldBe(visible, Duration.ofSeconds(15))
                        .shouldBe(enabled, Duration.ofSeconds(15))
                        .click();
            }
        }
        return this;
    }

    /**
     * Метод {@code waitElementsLoad} ожидает загрузки элементов страницы после применения фильтров.
     */
    @Step("Ожидание результатов поиска")
    public YandexAfterSearch waitElementsLoad() {
        $x(ELEMENTS_LOCATOR).shouldBe(visible, Duration.ofSeconds(15))
                .shouldBe(enabled, Duration.ofSeconds(15));
        return this;
    }

    /**
     * Метод {@code checkFilters} получает список элементов найденных на всей странице.
     * Проверяет, что все элементы удовлетворяют фильтру произовдителя,
     * путем сравнения названия элементов с искомыми. Если некоторые элементы не подходят под фильтры, то
     * происходит переход в карточку товара и проверяется производитель указанный в ней.
     *
     * @param brands список искомых производителей
     */
    @Step("Проверка предложений по соответсвию фильтру, производитель {brands}")
    public YandexAfterSearch checkFiltersWork(List<String> brands) {
        boolean hasNextPage = true;
        while (hasNextPage) {
            hasNextPage = navigateToNextPage();
        }
        String way = ELEMENTS_LOCATOR + "//div[contains(@data-baobab-name, 'title')]";
        List<String> elements = $$x(way).texts();
        List<String> fooElements = checkFilter(elements, brands);
        List<String> elementsForCheckInCards = new ArrayList<>(fooElements);
        if (!fooElements.isEmpty()) {
            for (String foo : fooElements) {
                $x(way + "[.//text()='" + foo + "']").scrollIntoView(false).click();
                switchTo().window(1);
                String brandInCard = $x("//div[contains(@data-zone-name, 'fullSpecs')]" +
                        "//div[contains(@aria-label, 'Характеристики')]//span[text()='Бренд']" +
                        "/../../following-sibling::div[1]//span")
                        .shouldBe(Condition.enabled, Duration.ofSeconds(15)).getText();
                if (brands.stream().anyMatch(brand -> brand.equalsIgnoreCase(brandInCard))) {
                    elementsForCheckInCards.remove(foo);
                }
                closeWindow();
                switchTo().window(0);
            }
        }
        return this;
    }

    /**
     * Метод {@code navigateToNextPage} листает вниз, пока кнопка перехода на следующую страницу не исчезнет.
     * Кнопка исчезает потому что страница автоматически продлевается.
     *
     * @return true, если кнопка перехода на следующую страницу видна, иначе false
     */
    @Step("Перехожv на следующую страницу, если она доступна")
    private boolean navigateToNextPage() {
        Actions actions = new Actions(webdriver().driver().getWebDriver());
        while (!isLastPage()) {
            actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();

        }
        if (isLastPage()) {
            return false;
        }

        return true;
    }

    /**
     * Метод {@code checkFilter} проверяет содержатся ли в заголовках элементов названия брендов.
     *
     * @return коллекция заголовков в которых ни один из брендов не был найден.
     */
    private List<String> checkFilter(List<String> pageElements, List<String> brands) {
        return pageElements.stream()
                .filter(element -> brands.stream()
                        .map(String::toLowerCase)
                        .noneMatch(brand -> element.toLowerCase().contains(brand))
                )
                .collect(Collectors.toList());
    }

    /**
     Метод {@code isLastPage} определяет, является ли текущая страница последней,
     проверяя видимость кнопки перехода на следующую страницу.
     *
     * @return true, если кнопка не видна, false наоборот.
     */
    private boolean isLastPage() {
        return $$x("//div[contains(@data-baobab-name, 'pager')]" +
                "//div[contains(@data-baobab-name, 'next')]").isEmpty();
    }
}
