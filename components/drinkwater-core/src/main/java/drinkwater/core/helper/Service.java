package drinkwater.core.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import drinkwater.IServiceConfiguration;
import drinkwater.ITracer;
import drinkwater.ServiceScheme;
import drinkwater.ServiceState;
import drinkwater.core.CamelContextFactory;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.RouteBuilders;
import drinkwater.core.ServiceRepository;
import drinkwater.trace.BaseEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by A406775 on 29/12/2016.
 */
public class Service implements drinkwater.IDrinkWaterService {

    @JsonIgnore
    DrinkWaterApplication _dwa;
    @JsonIgnore
    DefaultCamelContext camelContext;
    @JsonIgnore
    PropertiesComponent propertiesComponent;
    @JsonIgnore
    private Logger logger = Logger.getLogger(this.getClass().getName());
    @JsonIgnore
    private ITracer tracer;

    private IServiceConfiguration serviceConfiguration;

    private ServiceState state = ServiceState.NotStarted;

//    public Service(IServiceConfiguration serviceConfiguration,
//                   ITracer tracer, DrinkWaterApplication dwa) {
//
//        //this.camelContext = fromContext;
//        this(serviceConfiguration, tracer);
//        this._dwa = dwa;
//    }

    public Service(IServiceConfiguration serviceConfiguration, ITracer tracer, DrinkWaterApplication dwa) {
        this.serviceConfiguration = serviceConfiguration;
        this.camelContext = CamelContextFactory.createCamelContext(serviceConfiguration);
        this.tracer = tracer;
        this._dwa = dwa;
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    public ITracer getTracer() {
        return tracer;
    }

    public PropertiesComponent getPropertiesComponent() {
        if (propertiesComponent == null) {
            propertiesComponent = camelContext.getComponent(
                    "properties", PropertiesComponent.class);
        }
        return propertiesComponent;
    }

    public String lookupProperty(String s) throws Exception {
        return getPropertiesComponent().parseUri(s);
    }

    @Override
    public void start() {
        try {
            this.getCamelContext().start();
            this.state = ServiceState.Up;
        } catch (Exception e) {
            throw new RuntimeException("Could not start service", e);
        }
    }

    @Override
    public void stop() {
        try {
            this.getCamelContext().stop();
            this.state = ServiceState.Stopped;
        } catch (Exception e) {
            throw new RuntimeException("could not stop service : ", e);
        }
    }

    public void configure(ServiceRepository app) throws Exception {
        if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanObject) {
            //nothing to configure here
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanClass) {
            this.camelContext.addRoutes(RouteBuilders.mapBeanClassRoutes(app, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Rest) {
            this.camelContext.addRoutes(RouteBuilders.mapRestRoutes(app, this));
        }
    }

    @Override
    public ServiceState getState() {
        return state;
    }

    @Override
    public IServiceConfiguration getConfiguration() {
        return serviceConfiguration;
    }

    @Override
    public String toString() {
        return serviceConfiguration.getServiceName() + " as " + serviceConfiguration.getScheme() +
                " [" + serviceConfiguration.getServiceClass() + "]";
    }

    @Override
    public void sendEvent(BaseEvent event) {
        //fixme : this just to avoid logging while debugging ?
        if (event.getPayloads() != null && event.getPayloads().length > 0) {
            if (event.getPayloads()[0].getClass().equals(Method.class)) {
                if (((Method) event.getPayloads()[0]).getName().equalsIgnoreCase("toString"))
                    //do not log it....
                    return;
            }
        }
        this._dwa.getEventAggregator().addEvent(event);
    }
}
