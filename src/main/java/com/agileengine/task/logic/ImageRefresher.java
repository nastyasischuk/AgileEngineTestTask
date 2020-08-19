package com.agileengine.task.logic;

import com.agileengine.task.db.ImageService;
import com.agileengine.task.dto.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ImageRefresher implements Refresher, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);
    private ApplicationContext applicationContext;
    private AtomicBoolean refreshingInProgress = new AtomicBoolean(false);
    private ImageService imageService;

    @Autowired
    public ImageRefresher(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void refresh() {
        if (refreshingInProgress.compareAndSet(false, true)) {
            LOGGER.info("Refreshing images");
            Loader loader = applicationContext.getBean(Loader.class);
            List<Image> images = loader.load();
            updateImagesCache(images);
            refreshingInProgress.set(false);
        }
    }

    private void updateImagesCache(List<Image> images) {
        LOGGER.info("Updating image cache");
        try {
            imageService.batchUpdate(images);
            LOGGER.info("Successfully updated images");
        }catch (Exception e){
            LOGGER.error("Failed to update images {}", e.getMessage());
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
