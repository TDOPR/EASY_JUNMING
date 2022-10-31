package com.haoliang.server;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class CustomNotifier extends AbstractStatusChangeNotifier {

    private static final Logger logger = LoggerFactory.getLogger(CustomNotifier.class);

    public CustomNotifier(InstanceRepository repository) {
        super(repository);
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if (event instanceof InstanceStatusChangedEvent) {
                logger.info("Instance {} ({}) is {}", instance.getRegistration().getName(), event.getInstance(),
                        ((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
            } else {
                logger.info("Instance {} ({}) {}", instance.getRegistration().getName(), event.getInstance(), event.getType());
            }
        });
    }

}
