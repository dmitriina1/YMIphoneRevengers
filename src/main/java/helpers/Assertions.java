package helpers;

import io.qameta.allure.Step;

/**
 * Класс {@code Assertions} предназначен для  переопределения методов класса {@code Assertions}.
 * Он предоставляет методы для более детального отображения в Allure-отчете.
 *
 * @author Наливайко Дмитрий
 */
public class Assertions {

    /**
     * Метод {@code assertTrue} переопределяет метод {@code assertTrue}.
     *
     * @param condition состояние - булевое значение, которое при {@code false} выводит {@code message}
     * @param message сообщение получаемое при {@code false}
     */
    @Step("Проверяем что нет ошибки: {message}")
    public static void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }
}
