package test.drinkwater.examples.multiservice.boot;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.multiservice.IServiceA;
import drinkwater.examples.multiservice.IServiceC;
import drinkwater.examples.multiservice.boot.MultiServiceConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple App.
 */
public class MultiServiceTest {

    static DrinkWaterApplication app;

    @BeforeClass
    public static void setup() throws Exception {
        app = DrinkWaterApplication.create();
        MultiServiceConfiguration config = new MultiServiceConfiguration();
        app.addServiceBuilder(config);
        app.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        app.stop();
    }

    private static IServiceC getMockForIServiceC() {
        IServiceC mockedC = mock(IServiceC.class);
        when(mockedC.findData(ArgumentMatchers.any())).thenReturn("FOUND FROM MOCK");
        return mockedC;
    }

    @Test
    public void ShouldGetMockedValue() {

        app.changeService("serviceC", getMockForIServiceC());

        IServiceC servicec = app.getService("serviceC");

        String result = servicec.findData("whatever is passed here ");

        assertEquals("FOUND FROM MOCK", result);

    }

    @Test
    public void shouldGetValueFromMockInDependencies() {
        app.changeService("serviceC", getMockForIServiceC());

        IServiceA serviceA = app.getService("serviceA");

        String result = serviceA.getData("hello");

        assertEquals("A [initial : hello - result : B [C : [uppercase : FOUND FROM MOCK - append : this is text from properties]]]", result);

    }

}
