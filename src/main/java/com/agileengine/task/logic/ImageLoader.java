package com.agileengine.task.logic;

import com.agileengine.task.client.AgileEngineClient;
import com.agileengine.task.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ImageLoader implements Loader {
    private Boolean finished = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);
    private AgileEngineClient agileEngineClient;
    private Integer pageCount;
    private Integer pageNumber = 0;

    @Autowired
    public ImageLoader(AgileEngineClient agileEngineClient) {
        this.agileEngineClient = agileEngineClient;
    }

    @PostConstruct
    public void init() {
        Page page = agileEngineClient.getInitialPage();
        finished = !page.getHasMore();
        pageCount = page.getPageCount();
        LOGGER.info("About to load {} pages", pageCount);
    }

    public List<Image> load() {
        List<Image> images = new ArrayList<>();
        while (!finished && pageNumber < pageCount) {
            List<String> idsImages = loadPage();
            if (idsImages != null) {
                LOGGER.info("Loaded page {}, images: {} ", pageNumber, idsImages);
                images.addAll(loadImages(idsImages, pageNumber));
            }
        }
        return images;
    }

    private List<String> loadPage() {
        List<String> imageIds = new ArrayList<>();
        Page page;
            page = agileEngineClient.getPage(pageNumber);
        imageIds.addAll(page.getPictures()
                .stream()
                .map(ImageItem::getId)
                .collect(Collectors.toList()));
        LOGGER.info("Loaded page {}", page);
        if (!page.getHasMore()) {
            finished = true;
        }
        pageNumber++;
        return imageIds;
    }


    private List<Image> loadImages(final List<String> ids, Integer pageNumber) {
        return ids.stream()
                .map(imageId -> {
                    ImageDto image;
                        image = agileEngineClient.getImageDetails(imageId);
                    Image imageEntity = new Image();
                    imageEntity.setAuthor(image.getAuthor());
                    imageEntity.setCamera(image.getCamera());
                    imageEntity.setCroppedPicture(image.getCroppedPicture());
                    imageEntity.setFullPicture(image.getFullPicture());
                    imageEntity.setTags(image.getTags());
                    imageEntity.setId(imageId);
                    imageEntity.setPage(pageNumber);
                    //todo parse tags from string to list to search
                    LOGGER.debug("Image details with id:{} loaded", imageId);
                    return imageEntity;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

