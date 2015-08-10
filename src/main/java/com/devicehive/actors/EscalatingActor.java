package com.devicehive.actors;

import akka.actor.AbstractActor;
import akka.actor.AllForOneStrategy;
import akka.actor.SupervisorStrategy;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.escalate;

public abstract class EscalatingActor extends AbstractActor {

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new AllForOneStrategy(0, Duration.Zero(), param -> {
            return escalate();
        });
    }

}
