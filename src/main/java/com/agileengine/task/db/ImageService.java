package com.agileengine.task.db;


import com.agileengine.task.dto.Image;
import com.agileengine.task.dto.SearchTerm;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ImageService {
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void batchUpdate(List<Image> images) {
        images.forEach(image -> {
            Image found = entityManager.find(Image.class, image.getId());
            if (found == null) {
                entityManager.persist(image);
            } else {
                entityManager.merge(image);
            }
        });
    }

    public List<Image> searchForImages(SearchTerm searchTerm) {
        if (searchTerm == null) {
            return entityManager.createQuery("SELECT e FROM Image e").getResultList();
        } else {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Image> cr = cb.createQuery(Image.class);
            Root<Image> root = cr.from(Image.class);
            cr.select(root);
            if (StringUtils.isNotBlank(searchTerm.getAuthor())) {
                cr.where(cb.equal(root.get("author"), searchTerm.getAuthor()));
            }
            if (StringUtils.isNotBlank(searchTerm.getCamera())) {
                cr.where(cb.equal(root.get("camera"), searchTerm.getCamera()));
            }
            if (StringUtils.isNotBlank(searchTerm.getCroppedPicture())) {
                cr.where(cb.equal(root.get("cropped_picture"), searchTerm.getCroppedPicture()));
            }
            if (StringUtils.isNotBlank(searchTerm.getFullPicture())) {
                cr.where(cb.equal(root.get("fullPicture"), searchTerm.getFullPicture()));
            }
            if (StringUtils.isNotBlank(searchTerm.getTags())) {
                cr.where(cb.like(root.get("tags"), searchTerm.getTags()));
            }
            TypedQuery<Image> query = entityManager.createQuery(cr);
            return query.getResultList();
        }

    }

}
