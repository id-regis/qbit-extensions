package io.advantageous.qbit.service.discovery;

import io.advantageous.qbit.reactive.Callback;
import io.advantageous.qbit.service.Startable;
import io.advantageous.qbit.service.Stoppable;
import io.advantageous.qbit.service.health.HealthStatus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service Discovery
 * created by rhightower on 3/23/15.
 */
public interface ServiceDiscovery extends Startable, Stoppable {


    /**
     * Generates a unique string, used for generating unique service ids
     *
     * @param port port
     * @return unique id incorporating host name if possible.
     */
    static String uniqueString(int port) {
        try {
            return port + "-" + InetAddress.getLocalHost().getHostName().replace('.', '-');
        } catch (UnknownHostException e) {
            return port + "-" + UUID.randomUUID().toString();
        }
    }

    /**
     * Register the service with the service discovery service if applicable.
     *
     * @param serviceName serviceName
     * @param port        port
     * @return EndpointDefinition
     */
    default EndpointDefinition register(
            final String serviceName,
            final int port) {

        return new EndpointDefinition(HealthStatus.PASS,
                serviceName + "." + uniqueString(port),
                serviceName, null, port);
    }

    /**
     * Register with the service discovery system and specify a TTL so that if
     * the service does not send a checkIn that it is marked down.
     * TTL is time to live.
     *
     * @param serviceName       service name
     * @param port              port
     * @param timeToLiveSeconds ttl
     * @return EndpointDefinition
     */
    default EndpointDefinition registerWithTTL(
            final String serviceName,
            final int port,
            final int timeToLiveSeconds) {

        return new EndpointDefinition(HealthStatus.PASS,
                serviceName + "." + uniqueString(port),
                serviceName, null, port, timeToLiveSeconds);
    }

    /**
     * Register an end point given an id, and a TTL.
     * This gets used if you want to be specific about what you call the service.
     *
     * @param serviceName       service name
     * @param serviceId         service id
     * @param port              port
     * @param timeToLiveSeconds ttl
     * @return EndpointDefinition
     */
    @SuppressWarnings("UnusedReturnValue")
    default EndpointDefinition registerWithIdAndTimeToLive(
            final String serviceName, final String serviceId, final int port, final int timeToLiveSeconds) {

        return new EndpointDefinition(HealthStatus.PASS,
                serviceId,
                serviceName, null, port, timeToLiveSeconds);
    }

    /**
     * Register with id. Specify a unique id that is not autogenerated
     *
     * @param serviceName service name
     * @param serviceId   service id
     * @param port        port
     * @return EndpointDefinition
     */
    default EndpointDefinition registerWithId(final String serviceName, final String serviceId, final int port) {

        return new EndpointDefinition(HealthStatus.PASS,
                serviceId,
                serviceName, null, port);
    }


    /**
     * Watch for changes in this service name and send change events if the service changes.
     *
     * @param serviceName serviceName
     */
    default void watch(String serviceName) {
    }

    /**
     * CheckIn with the service discovery mechanism. The service may be marked as down if it does
     * not check in, in the amount of time specified by the ttl if the service disovery provider supports
     * ttl and checkin (Consul does).
     *
     * @param serviceId    serviceId
     * @param healthStatus healthStatus
     */
    default void checkIn(String serviceId, HealthStatus healthStatus) {

    }


    /**
     * This is like `checkIn` but does an HealthStatus.SUCCESS if applicable.
     *
     * @param serviceId serviceId
     */
    default void checkInOk(String serviceId) {

    }

    /**
     * Load the services.
     * <p>
     * Depending on the underlying implementation the services are either periodically loaded
     * or loaded whenever a change is detected.
     * <p>
     * This version of the method is based on the last event change and / or the last poll of the
     * services from the remote system (i.e., Consul) if applicable.
     * <p>
     * This may also may trigger a remote call, but it will always return right away.
     *
     * @param serviceName service name
     * @return list of EndpointDefinition
     */
    default List<EndpointDefinition> loadServices(final String serviceName) {

        return Collections.emptyList();
    }

    /**
     * Loads services async.
     *
     * @param callback    callback
     * @param serviceName name of service
     */
    default void loadServicesAsync(Callback<List<EndpointDefinition>> callback, final String serviceName) {

    }


    /**
     * See `loadServices` this is like `loadServices` except it forces a remote call.
     * This is a blocking call to loadServices.
     *
     * @param serviceName service name.
     * @return list of EndpointDefinition
     */
    default List<EndpointDefinition> loadServicesNow(final String serviceName) {

        return Collections.emptyList();
    }

    /**
     * Start the service discovery system if applicable.
     */
    default void start() {
    }

    /**
     * Stop the service discovery system if applicable.
     */
    default void stop() {
    }


    /**
     * This just loads the end points that were registered locally.
     * These are the endpoints that this JVM and this ServiceDiscovery is managing.
     *
     * @return set of EndpointDefinitions
     */
    default Set<EndpointDefinition> localDefinitions() {
        return Collections.emptySet();
    }
}