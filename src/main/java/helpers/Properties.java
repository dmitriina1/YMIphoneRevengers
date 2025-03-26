package helpers;

import org.aeonbits.owner.ConfigFactory;

/**
 * Класс {@code Properties} предназначен для загрузки и хранения конфигурационных свойств тестов.
 * Использует библиотеку Owner для работы с файлами конфигурации.
 *
 * @author Наливайко Дмитрий
 */
public class Properties {

    /**
     * Экземпляр интерфейса {@code TestsProperties}, который содержит значения конфигурационных параметров.
     * Параметры загружаются из файла конфигурации, определенного в {@code TestsProperties}.
     *
     * @author Наливайко Дмитрий
     */
    public static TestsProperties testsProperties = ConfigFactory.create(TestsProperties.class);

}
