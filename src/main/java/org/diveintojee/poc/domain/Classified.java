/**
 *
 */
package org.diveintojee.poc.domain;

import com.google.common.base.Objects;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ext.JodaSerializers.DateTimeSerializer;
import org.diveintojee.poc.domain.validation.Create;
import org.diveintojee.poc.domain.validation.Update;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author louis.gueye@gmail.com
 */
@Entity
@Table(name = Classified.TABLE_NAME)
@XmlRootElement
public class Classified extends AbstractEntity {

    public static final String TABLE_NAME = "classified";

    public static final String COLUMN_NAME_ID = "classified_id";

    public static final int CONSTRAINT_TITLE_MAX_SIZE = 50;
    public static final int CONSTRAINT_DESCRIPTION_MAX_SIZE = 50;

    /**
     *
     */
    private static final long serialVersionUID = -5952533696555432772L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Classified.COLUMN_NAME_ID)
    private Long id;

    @NotEmpty(message = "{classified.title.required}", groups = {Create.class, Update.class})
    @Size(max = Classified.CONSTRAINT_TITLE_MAX_SIZE, message = "{classified.title.max.size}", groups = {
            Create.class, Update.class})
    private String title;

    @NotEmpty(message = "{classified.description.required}", groups = {Create.class, Update.class})
    @Size(max = Classified.CONSTRAINT_DESCRIPTION_MAX_SIZE, message = "{classified.description.max.size}", groups = {
            Create.class, Update.class})
    private String description;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime created;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime updated;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Classified other = (Classified) obj;
        if (this.id == null) {
            if (other.id != null) return false;
        } else if (!this.id.equals(other.id)) return false;
        return true;
    }

    /**
     * @return the created
     */
    public DateTime getCreated() {
        return this.created;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return the id
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(final DateTime created) {
        this.created = created;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @param id the id to set
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the updated
     */
    public DateTime getUpdated() {
        return this.updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(DateTime updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("title", title).
                add("description", description).
                add("created", created).
                add("updated", updated).
                toString();
    }
}
