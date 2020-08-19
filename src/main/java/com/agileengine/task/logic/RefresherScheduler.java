package com.agileengine.task.logic;

import com.agileengine.task.configuration.AgileEngineApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RefresherScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefresherScheduler.class);
    private final ThreadPoolTaskScheduler loaderScheduler = new ThreadPoolTaskScheduler();
    private List<ScheduledFuture<?>> loaderScheduledList = new ArrayList<>();
    private Refresher refresher;
    private AgileEngineApiConfig agileEngineApiConfig;
    private AtomicBoolean refreshingInProgress = new AtomicBoolean(false);

    public RefresherScheduler(Refresher refresher, AgileEngineApiConfig agileEngineApiConfig) {
        this.refresher = refresher;
        this.agileEngineApiConfig = agileEngineApiConfig;
    }

    @PostConstruct
    public void init() {
        loaderScheduler.setPoolSize(agileEngineApiConfig.getPoolSize());
        loaderScheduler.initialize();
    }

    public void schedule() {

        CronTrigger loaderTrigger = new CronTrigger(agileEngineApiConfig.getCronExpression());
        LOGGER.info("Scheduling refresher with cron expression [{}]", loaderTrigger);
        final ScheduledFuture<?> loaderScheduledFuture = loaderScheduler.schedule(refresher::refresh, loaderTrigger);
        loaderScheduledList.add(loaderScheduledFuture);

        Instant now = Instant.now();
        LOGGER.info("Scheduling refresher with time [{}]", now);
        loaderScheduledList.add(loaderScheduler.schedule(refresher::refresh, now));
    }

    @PreDestroy
    public void removeRefresher() {
        loaderScheduledList.stream().map(scheduledLoader -> scheduledLoader.cancel(false));
    }
}
