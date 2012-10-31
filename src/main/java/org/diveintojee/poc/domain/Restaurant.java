/**
 *
 */
package org.diveintojee.poc.domain;

import com.google.common.base.Preconditions;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ext.JodaSerializers;
import org.diveintojee.poc.domain.validation.Create;
import org.diveintojee.poc.domain.validation.Update;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author louis.gueye@gmail.com
 */
@Entity
@Table(name = Restaurant.TABLE_NAME, uniqueConstraints = {@UniqueConstraint(columnNames = {Restaurant.COLUMN_NAME_COMPANY_ID})})
@XmlRootElement
public class Restaurant extends AbstractEntity {

    public static final String TABLE_NAME = "restaurant";
    public static final String TABLE_NAME_RESTAURANT_FOOD_SPECIALTY = "restaurant_food_specialty";

    public static final String COLUMN_NAME_ID = "restaurant_id";
    public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_NAME_MAIN_OFFER = "main_offer";
    public static final String COLUMN_NAME_COMPANY_ID = "company_id";

    public static final int CONSTRAINT_NAME_MAX_SIZE = 50;
    public static final int CONSTRAINT_DESCRIPTION_MAX_SIZE = 200;
    public static final int CONSTRAINT_PHONE_NUMBER_MAX_SIZE = 20;
    public static final int CONSTRAINT_MAIN_OFFER_MAX_SIZE = 200;
    public static final int CONSTRAINT_COMPANY_ID_MAX_SIZE = 50;

    /**
     *
     */
    private static final long serialVersionUID = -5952533696555432772L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Restaurant.COLUMN_NAME_ID)
    private Long id;

    @NotEmpty(message = "{restaurant.name.required}", groups = {Create.class, Update.class})
    @Size(max = Restaurant.CONSTRAINT_NAME_MAX_SIZE, message = "{restaurant.name.max.size}", groups = {Create.class,
            Update.class})
    private String name;

    @Size(max = Restaurant.CONSTRAINT_DESCRIPTION_MAX_SIZE, message = "{restaurant.description.max.size}", groups = {
            Create.class, Update.class})
    private String description;

    @Column(name = Restaurant.COLUMN_NAME_COMPANY_ID)
    @NotEmpty(message = "{restaurant.companyId.required}", groups = {Create.class, Update.class})
    @Size(max = Restaurant.CONSTRAINT_COMPANY_ID_MAX_SIZE, message = "{restaurant.companyId.max.size}", groups = {
            Create.class, Update.class})
    private String companyId;

    @Column(name = Restaurant.COLUMN_NAME_PHONE_NUMBER)
    @NotEmpty(message = "{restaurant.phoneNumber.required}", groups = {Create.class, Update.class})
    @Size(max = Restaurant.CONSTRAINT_PHONE_NUMBER_MAX_SIZE, message = "{restaurant.phoneNumber.max.size}", groups = {
            Create.class, Update.class})
    private String phoneNumber;

    @Column(name = Restaurant.COLUMN_NAME_MAIN_OFFER)
    @Size(max = Restaurant.CONSTRAINT_MAIN_OFFER_MAX_SIZE, message = "{restaurant.mainOffer.max.size}", groups = {
            Create.class, Update.class})
    private String mainOffer;

    private Boolean kosher;

    private Boolean halal;

    private Boolean vegetarian;


    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = JodaSerializers.DateTimeSerializer.class)
    private DateTime created;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = JodaSerializers.DateTimeSerializer.class)
    private DateTime updated;

    /**
     *
     */
    public Restaurant() {
        super();
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Restaurant other = (Restaurant) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getMainOffer() {
        return mainOffer;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    public Boolean isHalal() {
        return halal;
    }

    public Boolean isKosher() {
        return kosher;
    }

    public Boolean isVegetarian() {
        return vegetarian;
    }

    public void setCompanyId(final String companyId) {
        this.companyId = companyId;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setHalal(final Boolean halal) {
        this.halal = halal;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setKosher(final Boolean kosher) {
        this.kosher = kosher;
    }

    public void setMainOffer(final String mainOffer) {
        this.mainOffer = mainOffer;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setVegetarian(final Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public DateTime getCreated() {
      return created;
    }

    public void setCreated(DateTime created) {
      this.created = created;
    }

    public DateTime getUpdated() {
      return updated;
    }

    public void setUpdated(DateTime updated) {
      this.updated = updated;
    }
}
