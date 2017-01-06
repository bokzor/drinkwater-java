package test.drinkwater.core.model.forTracing;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceAConfiguration extends ServiceConfigurationBuilder {

    @Override
    public void configure() {
        addService("serviceD", IServiceD.class, ServiceDImpl.class);
        addService("serviceB", IServiceB.class).withProperty("drinkwater.rest.port", 8888).asRemote();
        addService("serviceA", IServiceA.class, new ServiceAImpl(), "serviceD", "serviceB").withProperty("drinkwater.rest.port", 7777).asRest();
    }
}
