package helpers;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

/**
 * Класс {@code DataProvider} предназначен для предоставления значений параметров теста.
 * Используется в тестах для генерации различных наборов данных, необходимых для проверки функциональности.
 *
 * @author Наливайко Дмитрий
 *
 */
public class DataProvider {

    /**
     * Метод {@code providerCheckingMarket} предоставляет значения для теста подкаталога "Ноутбуки" Яндекс.Маркета.
     * Возвращает их в виде потока аргументов, который может быть использован в параметризованных тестах.
     *
     * @return поток значений параметров, содержащий название маркетплейса, категорию, подкатегорию,
     *         минимальную и максимальную цену, список брендов и количество ожидаемых результатов.
     *
     * @author Наливайко Дмитрий
     */
    public static Stream<Arguments> providerCheckingMarket(){
        List<String> brandsNames = List.of("Lenovo", "HP");
        return Stream.of(
                Arguments.of("Яндекс Маркет", "Электроника", "Ноутбуки", "10000", "30000", brandsNames, 12)
        );
    }
}
