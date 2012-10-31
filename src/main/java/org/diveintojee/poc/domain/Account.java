/**
 *
 */
package org.diveintojee.poc.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ext.JodaSerializers.DateTimeSerializer;
import org.diveintojee.poc.domain.validation.Create;
import org.diveintojee.poc.domain.validation.Update;
import org.diveintojee.poc.domain.validation.ValidEmail;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author louis.gueye@gmail.com
 */
@Entity
@Table(name = Account.TABLE_NAME, uniqueConstraints = {@UniqueConstraint(columnNames = {Account.COLUMN_NAME_EMAIL})})
@XmlRootElement
public class Account extends AbstractEntity {

    public static final String TABLE_NAME = "account";
    public static final String TABLE_NAME_USER_AUTHORITY = "accounts_authorities";
    public static final String TABLE_NAME_USER_RESTAURANT = "accounts_restaurants";

    public static final String COLUMN_NAME_ID = "account_id";
    public static final String COLUMN_NAME_LAST_CONNECTION = "last_connection";
    public static final String COLUMN_NAME_FIRST_NAME = "first_name";
    public static final String COLUMN_NAME_LAST_NAME = "last_name";
    public static final String COLUMN_NAME_EMAIL = "email";

    public static final int CONSTRAINT_FIRST_NAME_MAX_SIZE = 50;
    public static final int CONSTRAINT_LAST_NAME_MAX_SIZE = 50;
    public static final int CONSTRAINT_EMAIL_MAX_SIZE = 100;
    public static final int CONSTRAINT_PASSWORD_MAX_SIZE = 200;

    /**
     *
     */
    private static final long serialVersionUID = -5952533696555432772L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Account.COLUMN_NAME_ID)
    private Long id;

    @Column(name = Account.COLUMN_NAME_FIRST_NAME)
    @NotEmpty(message = "{account.firstName.required}", groups = {Create.class, Update.class})
    @Size(max = Account.CONSTRAINT_FIRST_NAME_MAX_SIZE, message = "{account.firstName.max.size}", groups = {
            Create.class, Update.class})
    private String firstName;

    @Column(name = Account.COLUMN_NAME_LAST_NAME)
    @NotEmpty(message = "{account.lastName.required}", groups = {Create.class, Update.class})
    @Size(max = Account.CONSTRAINT_LAST_NAME_MAX_SIZE, message = "{account.lastName.max.size}", groups = {
            Create.class, Update.class})
    private String lastName;

    @NotEmpty(message = "{account.email.required}", groups = {Create.class, Update.class})
    @ValidEmail(message = "{account.email.valid.format.required}", groups = {Create.class, Update.class})
    @Size(max = Account.CONSTRAINT_EMAIL_MAX_SIZE, message = "{account.email.max.size}", groups = {Create.class,
            Update.class})
    private String email;

    @NotEmpty(message = "{account.password.required}", groups = {Create.class, Update.class})
    @Size(max = Account.CONSTRAINT_PASSWORD_MAX_SIZE, message = "{account.password.max.size}", groups = {Create.class,
            Update.class})
    private String password;

    private boolean locked;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime created;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime updated;

    @Column(name = Account.COLUMN_NAME_LAST_CONNECTION)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime lastConnection;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Account other = (Account) obj;
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
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * @return the id
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * @return the lastConnection
     */
    public DateTime getLastConnection() {
        return this.lastConnection;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(final DateTime created) {
        this.created = created;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param id the id to set
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @param lastConnection the lastConnection to set
     */
    public void setLastConnection(final DateTime lastConnection) {
        this.lastConnection = lastConnection;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
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

}
