/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Tag;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteTagException;
import util.exception.TagAlreadyExistException;
import util.exception.TagNotFoundException;

/**
 *
 * @author sucram
 */
@Local
public interface TagSessionBeanLocal {

    public List<Tag> retrieveAllTags();

    public Tag createNewTag(Tag newTag) throws TagAlreadyExistException;

    public Tag retrieveTagByTagId(Long tagId) throws TagNotFoundException;

    public void deleteTag(Long tagId) throws DeleteTagException;

    public Tag updateTag(Tag newTag);
    
}
