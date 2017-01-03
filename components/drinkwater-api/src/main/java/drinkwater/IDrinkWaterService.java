package drinkwater;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface IDrinkWaterService {

    ITracer getTracer();

    IServiceConfiguration configuration();

    void start();

    void stop();

    ServiceState getState();
}