import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoServise;
import ru.netology.geo.GeoServiseImpl;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;


public class MessageSenderImplTest {

    @Test
    void send_russian_text() {
        String ip = "172.0.0.0";
        HashMap mapRus = new HashMap();
        mapRus.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);
        GeoServiseImpl geoServise = Mockito.mock(GeoServiseImpl.class);
        Mockito.when(geoServise.byIp(ip))
                .thenReturn(new Location("Moscow", Country.RUSSIA, null, 0));

        LocalizationServiceImpl localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationService.locale(Country.RUSSIA))
                .thenReturn("Добро пожаловать");

        MessageSenderImpl messageSender = new MessageSenderImpl(geoServise, localizationService);
        String preferences = messageSender.send(mapRus);
        String expected = "Добро пожаловать";
        Assertions.assertEquals(expected, preferences);
    }

    @Test
    void send_english_text() {
        String ip = "96.0.0.0";
        HashMap mapEng = new HashMap();
        mapEng.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);

        GeoServiseImpl geoServise = Mockito.mock(GeoServiseImpl.class);
        Mockito.when(geoServise.byIp(ip))
                .thenReturn(new Location("New York", Country.USA, null, 0));

        LocalizationServiceImpl localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationService.locale(Country.USA))
                .thenReturn("Welcome");
        MessageSenderImpl messageSender = new MessageSenderImpl(geoServise, localizationService);
        String preferences = messageSender.send(mapEng);
        String expected = "Welcome";
        Assertions.assertEquals(expected, preferences);

        Mockito.verify(localizationService, Mockito.times(2))
                .locale(Mockito.<Country>any());

        Mockito.verify(geoServise, Mockito.times(1))
                .byIp(Mockito.<String>any());
    }

    @Test
    void send_text() {
        GeoServiseImpl geoServise = Mockito.spy(GeoServiseImpl.class);
        LocalizationServiceImpl localizationService = Mockito.spy(LocalizationServiceImpl.class);
        MessageSenderImpl messageSender = new MessageSenderImpl(geoServise, localizationService);

        HashMap mapRu = new HashMap();
        mapRu.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.0.0.0");

        HashMap mapEng = new HashMap();
        mapEng.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.0.0.0");

        String expectedENG = "Welcome";
        String expectedRu = "Добро пожаловать";

        String preferencesENG = messageSender.send(mapEng);
        String preferencesRu = messageSender.send(mapRu);

        Assertions.assertEquals(expectedENG, preferencesENG);
        Assertions.assertEquals(expectedRu, preferencesRu);
    }

    @Test
    void tests_to_verify_location_by_ip() {
        String ip = "96.0.0.0";
        Location expected = new Location("New York", Country.USA, "10th Avenue", 32);
        GeoServise geoServise = new GeoServiseImpl();
        Location preferences = geoServise.byIp(ip);

        Assertions.assertEquals(expected.getCountry(), preferences.getCountry());
    }

    @Test
    void checking_the_returned_text() {
        String expected = "Добро пожаловать";
        String preferences = new LocalizationServiceImpl().locale(Country.RUSSIA);
        Assertions.assertEquals(expected, preferences);
    }
    @Test
    void testExpectedException() {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
            GeoServiseImpl exception = new GeoServiseImpl();
            exception.byCoordinates(1, 1);
        });

        Assertions.assertEquals("Not implemented", thrown.getMessage());
    }
}
