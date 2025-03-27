package yandexm.iphone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.YandexAfterSearch;
import pages.YandexBeforeSearch;

import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static helpers.Properties.testsProperties;

/**
 * Класс {@code Tests} расширяет {@code BaseTest} содержит тесты для проверки функциональности Яндекс Маркета.
 *
 * @author Наливайко Дмитрий
 */
public class Tests extends BaseTest {

    /**
     * Метод {@code testYandexMarket} проверяет работу поиска и фильтрации товаров на Яндекс.Маркете.
     *
     * @param siteTitle      Ожидаемый заголовок страницы.
     * @param catalogContent Категория товаров, по которой осуществляется поиск.
     * @param catalogSubItem Подкатегория товаров.
     * @param brandList      Список брендов для фильтрации.
     */
    @DisplayName("Проверка работы сайта ЯндексМаркета")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerCheckingMarket")
    public void testYandexMarket(String siteTitle,String catalogContent,
                                 String catalogSubItem, List<String> brandList) {
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch();
        yandexBeforeSearch.openSite(testsProperties.yandexMarketUrl(), siteTitle, YandexBeforeSearch.class)
                .findCatalog()
                .catalogContentMouseOver(catalogContent)
                .goToСatalogSubItem(catalogSubItem, YandexAfterSearch.class)
                .checkingCatalogSubItemTitle(catalogSubItem)
                .inputBrands(brandList)
                .waitElementsLoad()
                .checkFiltersWork(brandList);
    }
}
