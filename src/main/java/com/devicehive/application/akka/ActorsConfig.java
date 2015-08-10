package com.devicehive.application.akka;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.devicehive.application.akka.helpers.SchedulerHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class ActorsConfig {
    public static final String PEER_NAME_PATTERN = "peer-ws-%s";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SchedulerHelper schedulerHelper;

    @PostConstruct
    public void init() {
        schedulerHelper.scheduleWebSocketPing();
    }

    @Bean(destroyMethod = "shutdown")
    public ActorSystem actorSystem() {
        return ActorSystem.create("dh-actor-system", akkaConfiguration());
    }

    @Bean
    public Config akkaConfiguration() {
        return ConfigFactory.load("akka.conf");
    }

    @Bean
    public Timeout akkaTimeout() {
        return new Timeout(10, TimeUnit.SECONDS);
    }

    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension(applicationContext);
    }

    /**
     * Holder for actor spring beans names
     */
    public interface ActorBeanName {
        String REMOTE_PEER_ACTOR = "remotePeerActor";
        String PING_ACTOR = "remotePeersPingActor";
        String WEB_SOCKET_REQUEST_DISPATCHER = "wsRequestDispatcher";
        String REQUEST_HANDLER_MAPPER = "requestHandlerMapper";
    }

}
