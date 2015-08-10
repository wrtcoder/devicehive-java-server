package com.devicehive.application.akka;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

public class SpringExtension implements Extension {

    private final ApplicationContext applicationContext;

    public SpringExtension(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Props props(String actorBeanName, Object ... args) {
        return Props.create(SpringActorProducer.class, applicationContext, actorBeanName, args);
    }

}
