package org.androidtransfuse.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.androidtransfuse.processor.MergeableTags;

import java.util.Collections;
import java.util.Set;

/**
 * @author John Ericksen
 */

public abstract class Mergeable {

    @XStreamAlias("transfuse:tag")
    @XStreamAsAttribute
    private MergeableTags tags = new MergeableTags();

    public abstract String getIdentifier();

    public void addMergeTag(String tag) {
        tags.getTags().add(tag);
    }

    public Set<String> getMergeTags() {
        if (tags == null) {
            return Collections.emptySet();
        }
        return tags.getTags();
    }

}
