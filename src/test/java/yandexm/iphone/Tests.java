package yandexm.iphone;

import org.junit.jupiter.api.Test;
import pages.YandexAfterSearch;
import pages.YandexBeforeSearch;

import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static helpers.Properties.testsProperties;

public class Tests extends BaseTest{

    @Test
    public void testYandexMarket(){
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch();
        yandexBeforeSearch.openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", YandexBeforeSearch.class)
                .findCatalog()
                .catalogContentMouseOver("Электроника")
                .goToСatalogSubItem("Смартфоны", YandexAfterSearch.class)
                .checkingCatalogSubItemTitle("Смартфоны")
                .inputBrands(List.of("Apple"))
                .waitElementsLoad()
                .checkAllProductsContainBrand(List.of("Apple"));
        sleep(10000);
    }
}
