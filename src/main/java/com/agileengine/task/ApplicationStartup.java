package com.agileengine.task;

import com.agileengine.task.logic.RefresherScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private final RefresherScheduler loaderScheduler;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartup.class);

    @Autowired
    public ApplicationStartup(RefresherScheduler loaderScheduler) {
        this.loaderScheduler = loaderScheduler;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("Starting scheduling of image loader");
        loaderScheduler.schedule();
    }
}
