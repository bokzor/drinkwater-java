package drinkwater.trace;

import java.time.Instant;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServerSentEvent extends BaseEvent {
    public ServerSentEvent(Instant instant, String correlationId, String description, Payload payloads) {
        super(instant, "SES", correlationId, description, payloads);
    }
}
