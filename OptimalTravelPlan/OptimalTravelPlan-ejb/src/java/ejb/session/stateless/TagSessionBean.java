/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Tag;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DeleteTagException;
import util.exception.TagAlreadyExistException;
import util.exception.TagNotFoundException;

@Stateless
public class TagSessionBean implements TagSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public Tag createNewTag(Tag newTag) throws TagAlreadyExistException {
        try {
            em.persist(newTag);
            em.flush();
        } catch (Exception e) {
            throw new TagAlreadyExistException("Tag already exists!");
        }
        return newTag;
    }

    @Override
    public List<Tag> retrieveAllTags() {
        Query query = em.createQuery("SELECT t FROM Tag t ORDER BY t.name ASC");
        List<Tag> tagEntities = query.getResultList();

        for (Tag tagEntity : tagEntities) {
            tagEntity.getServices().size();
        }
        return tagEntities;
    }

    @Override
    public Tag updateTag(Tag newTag) {
        Tag tag = em.find(Tag.class, newTag.getTagId());
        tag.setName(newTag.getName());
        if (newTag.getServices() != null) {
            tag.setServices(newTag.getServices());
        }
        em.flush();
        return tag;
    }

    @Override
    public Tag retrieveTagByTagId(Long tagId) throws TagNotFoundException {
        Tag tag = em.find(Tag.class, tagId);
        if (tag != null) {
            return tag;
        } else {
            throw new TagNotFoundException("Tag ID " + tagId + " does not exist!");
        }
    }

    @Override
    public void deleteTag(Long tagId) throws DeleteTagException {
        Tag tagEntityToRemove = em.find(Tag.class, tagId);
        List<Customer> customers = customerSessionBean.retrieveAllCustomers();
        for (Customer c : customers) {
            if (c.getFavouriteTags().contains(tagEntityToRemove)) {
                throw new DeleteTagException("Tag ID " + tagId + " is associated with existing customers and cannot be deleted!");
            }
        }
        if (!tagEntityToRemove.getServices().isEmpty()) {
            throw new DeleteTagException("Tag ID " + tagId + " is associated with existing services and cannot be deleted!");
        } else {
            em.remove(tagEntityToRemove);
        }
    }

}
