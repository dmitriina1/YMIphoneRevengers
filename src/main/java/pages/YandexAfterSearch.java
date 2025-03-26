package pages;

import com.codeborne.selenide.*;
import helpers.Assertions;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class YandexAfterSearch extends BasePage {

    @Step("Проверка что перешел в раздел {catalogSubItem}")
    public YandexAfterSearch checkingCatalogSubItemTitle(String catalogSubItem) {
        $x("//div[contains(@data-zone-name, 'searchTitle')]//h1").shouldBe(Condition.enabled, Duration.ofSeconds(15)).shouldHave(Condition.text(catalogSubItem));
        return this;
    }


    private final String brandSearchLocator = "//div[contains(@data-zone-data, 'Бренд')]//input";
    private final String brandShownLocator = "//div[contains(@data-zone-data, 'Бренд')]" +
            "//div[contains(@data-zone-name, 'FilterValue')]";
    private final String showMoreButtonLocator = "//div[contains(@data-baobab-name, 'showMoreFilters')]";
    private final String productLocator = "//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]";

    @Step("Выбираем производителей {brands} в фильтре")
    public YandexAfterSearch inputBrands(List<String> brands) {
        List<String> tempBrands = new ArrayList<>(brands);

        for (String brand : brands) {
            if ($x(brandShownLocator + "//span[contains(text(), '" + brand + "')]").exists()) {
                $x(brandShownLocator + "//span[contains(text(), '" + brand + "')]").click();
                tempBrands.remove(brand);
            }
        }
        if (!tempBrands.isEmpty() && $(By.xpath(showMoreButtonLocator)).exists()) {
            $(By.xpath(showMoreButtonLocator)).click();

            for (String brand : tempBrands) {
                $x(brandSearchLocator).setValue(brand);
                $x(brandShownLocator + "//span[contains(text(), '" + brand + "')]")
                        .shouldBe(visible)
                        .click();
            }
        }
        return this;
    }

    public YandexAfterSearch waitElementsLoad() {
        $x(productLocator).shouldBe(enabled);
        return this;
    }

    public YandexAfterSearch checkAllProductsContainBrand(List<String> brands) {
        boolean hasNextPage = true;
        while (hasNextPage) {
            hasNextPage = navigateToNextPage();
        }
        String way = productLocator + "//div[contains(@data-baobab-name, 'title')]";
        List<String> elements = $$x(way).texts();
        List<String> fooElements = checkFilter(elements, brands);
        List<String> elementsForCheckInCards = new ArrayList<>(fooElements);
        if(!fooElements.isEmpty()){
            System.out.println(fooElements);
            for(String foo: fooElements){
            $x(way+"[.//text()='"+foo+"']").scrollIntoView(false).click();
                switchTo().window(1);
                String brandInCard = $x("//div[contains(@data-zone-name, 'fullSpecs')]" +
                        "//div[contains(@aria-label, 'Характеристики')]//span[text()='Бренд']" +
                        "/../../following-sibling::div[1]//span").shouldBe(Condition.enabled, Duration.ofSeconds(15)).getText();
                if (brands.stream().anyMatch(brand -> brand.equalsIgnoreCase(brandInCard))) {
                    elementsForCheckInCards.remove(foo);
                }
                closeWindow();
                switchTo().window(0);
            }
        }
        return this;
    }
    //div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]//div[contains(@data-baobab-name, 'title')]
//    private List<String> checkFilter(List<String> pageElements, List<String> brands) {
//        System.out.println(pageElements);
//        return pageElements.stream()
//                .filter(element -> brands.stream().noneMatch(element::contains)) // Фильтруем строки, не содержащие ни одного бренда
//                .collect(Collectors.toList()); // Собираем результат
//    }

    private List<String> checkFilter(List<String> pageElements, List<String> brands) {
        System.out.println(pageElements);
        return pageElements.stream()
                .filter(element -> brands.stream()
                        .map(String::toLowerCase)
                        .noneMatch(brand -> element.toLowerCase().contains(brand))
                )
                .collect(Collectors.toList());
    }
   // String title = product.$x(titleLocator).getText().trim();
    //
//                    if (brands.stream().noneMatch(title::contains))

    private void checkPage(List<String> brands) {
        String way = productLocator + "//div[contains(@data-baobab-name, 'title')]";
        ElementsCollection elements = $$x(way);
        for (SelenideElement el : elements) {
            System.out.println(el.getText()); // Выведет каждый заголовок
        }
    }

    @Step("Проверяем результаты поиска на наличие товара: {product}")
    public void checkAllProductsContain(List<String> compareBrands) {
        boolean hasNextPage = true;
        while (hasNextPage) {
            checkPageConditions(compareBrands);
            //hasNextPage = navigateToNextPage();
        }
    }

    /**
     * Метод {@code checkPageConditions} проверяет текущую страницу на наличие товаров, соответствующих
     * указанным брендам.
     *
     * @param compareBrands список брендов для сравнения с товарами на странице
     */

    @Step("Проверяем текущую страницу на наличие товара: {product}")
    private void checkPageConditions(List<String> compareBrands) {
        ElementsCollection productElementsTitle = $$x(productLocator);
        for (SelenideElement productElement : productElementsTitle) {
            String title = productElement.$x(".//div[contains(@data-baobab-name, 'title')]//span").getText().trim();
            boolean containsBrand = compareBrands.stream().anyMatch(title::contains);

            if (!containsBrand) {
                System.out.println(title);
            }
//            Assertions.assertTrue(containsBrand,
//                    "На странице " + currentPage + " название производителя не соответствует " +
//                            "ни одному из ожидаемых брендов: " + compareBrands);

        }
        //System.out.println("На странице " + currentPage + " товар соответствует параметрам");
    }

    /**
     * Метод {@code navigateToNextPage} переходит на следующую страницу, если она доступна.
     *
     * @return true, если переход на следующую страницу был успешным, иначе false
     */
    @Step("Переходиv на следующую страницу, если она доступна")
    private boolean navigateToNextPage() {
//        String nextPageButtonLocator = "//div[contains(@data-baobab-name, 'pager')]//div[contains(@data-baobab-name, 'next')]";
//        $x(nextPageButtonLocator)
//                .should(exist, Duration.ofSeconds(10))
//                .shouldBe(visible, Duration.ofSeconds(10))
//                .shouldBe(enabled, Duration.ofSeconds(10));
//        ElementsCollection nextButtons = $$x(nextPageButtonLocator);
//        if (!nextButtons.isEmpty()) {
//            SelenideElement nextButton = nextButtons.first();
//            if (nextButton.isDisplayed()) {
//                nextButton.shouldBe(visible, Duration.ofSeconds(10))
//                        .shouldBe(enabled, Duration.ofSeconds(10))
//                        .scrollIntoView(false)
//                        .click();
//                currentPage++;
//                return true;
//            }
//        }
//        sleep(10000);
//        return false;
        Actions actions = new Actions(webdriver().driver().getWebDriver());
        while (!isLastPage()) {
            actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();

        }
        if(isLastPage()){
            return false;
        }

        return true;
    }

    private boolean isLastPage() {
        return $$x("//div[contains(@data-baobab-name, 'pager')]" +
                "//div[contains(@data-baobab-name, 'next')]").isEmpty();
    }

//    public YandexAfterSearch checkAllProductsContainBrand(List<String> brands) {
//        String titleLocator = ".//div[contains(@data-baobab-name, 'title')]//span"; // Локатор заголовка товара
//        String nextPageButton = "//div[contains(@data-baobab-name, 'pager')]//div[contains(@data-baobab-name, 'next')]";
//        List<SelenideElement> checkedTitles = new ArrayList<>(); // Чтобы не проверять дубли
//boolean isComleted = true;
//        SelenideElement nextPage;
//        while (isComleted) {
//            nextPage = $x(nextPageButton);
//            if (nextPage.exists() && nextPage.isDisplayed()) {
//                nextPage.scrollIntoView(false).click();
//            } else {
//                ElementsCollection products = $$x(productLocator);
//
//                for (SelenideElement product : products) {
//                    String title = product.$x(titleLocator).getText().trim();
//
//                    if (brands.stream().noneMatch(title::contains)) { // Проверяем только новые товары
//                        checkedTitles.add(product);
//                        System.out.println("LOSE");
//                        System.out.println("Товар '" + title + "' не содержит бренд из списка " + brands);
//                    }
//                }
//                if ($$x(nextPageButton).isEmpty())
//                    isComleted = false;
//                break;
//            }
//
//        }
//        return this;
//    }
}

//        while (true) {
//            ElementsCollection products = $$x(productLocator);
//
//            for (SelenideElement product : products) {
//                String title = product.$x(titleLocator).getText().trim();
//
//                if (!checkedTitles.contains(title)) { // Проверяем только новые товары
//                    checkedTitles.add(title);
//
//                    boolean containsBrand = brands.stream().anyMatch(title::contains);
//                    if (!containsBrand) {
//                        System.out.println("Товар '" + title + "' не содержит бренд из списка " + brands);
//                       // throw new AssertionError("Товар '" + title + "' не содержит бренд из списка " + brands);
//                    }
//                }
//            }
//
//            // Проверяем, есть ли кнопка "Далее"
//            SelenideElement nextPage = $x(nextPageButton);
//            if (nextPage.exists() && nextPage.isDisplayed()) {
//                nextPage.scrollIntoView(true).click();
//
//                // Ждём лоадер, если он есть
//                SelenideElement loader = $x(loaderLocator);
//                if (loader.exists()) {
//                    loader.should(disappear);
//                }
//
//                // Ждём, пока появятся новые элементы
//                products.last().shouldBe(visible);
//            } else {
//                break; // Если кнопки нет — значит, это последняя страница
//            }
//        }
//        return this;
//  }

//}
