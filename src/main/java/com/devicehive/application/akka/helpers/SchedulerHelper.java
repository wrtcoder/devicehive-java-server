package com.devicehive.application.akka.helpers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.devicehive.actors.messages.PingAllPeers;
import com.devicehive.application.akka.ActorsConfig;
import com.devicehive.application.akka.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.AbstractFunction1;
import scala.util.Success;
import scala.util.Try;

import java.util.concurrent.TimeUnit;

@Component
public class SchedulerHelper {

    @Autowired
    private ActorSystem system;
    @Autowired
    private Timeout timeout;
    @Autowired
    private SpringExtension springExt;

    public void scheduleWebSocketPing() {
        system.actorSelection("/user/pingActor").resolveOne(timeout).onComplete(new AbstractFunction1<Try<ActorRef>, Void>() {
            @Override
            public Void apply(Try<ActorRef> tryOpj) {
                ActorRef pingActor;
                if (tryOpj instanceof Success) {
                    pingActor = tryOpj.get();
                } else {
                    pingActor = system.actorOf(springExt.props(ActorsConfig.ActorBeanName.PING_ACTOR), "pingActor");
                }
                FiniteDuration scheduleDuration = Duration.create(30, TimeUnit.SECONDS);
                system.scheduler().schedule(scheduleDuration, scheduleDuration, pingActor, PingAllPeers.getInstance(), system.dispatcher(), ActorRef.noSender());
                return null;
            }
        }, system.dispatcher());
    }

}
