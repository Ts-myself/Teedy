package com.sismics.docs.core.model.jpa;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.common.base.MoreObjects;

/**
 * User registration request entity.
 * 
 * @author Teedy
 */
@Entity
@Table(name = "T_USER_REGISTRATION_REQUEST")
public class UserRegistrationRequest {
    /**
     * Request status.
     */
    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    /**
     * Request ID.
     */
    @Id
    @Column(name = "URR_ID_C", length = 36)
    private String id;

    /**
     * Username.
     */
    @Column(name = "URR_USERNAME_C", nullable = false, length = 50)
    private String username;

    /**
     * Email.
     */
    @Column(name = "URR_EMAIL_C", nullable = false, length = 100)
    private String email;

    /**
     * Hashed password.
     */
    @Column(name = "URR_PASSWORD_C", nullable = false, length = 200)
    private String password;

    /**
     * Create date.
     */
    @Column(name = "URR_CREATEDATE_D", nullable = false)
    private Date createDate;

    /**
     * Status.
     */
    @Column(name = "URR_STATUS_C", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Process date.
     */
    @Column(name = "URR_PROCESSDATE_D")
    private Date processDate;

    /**
     * Processed by (user ID).
     */
    @Column(name = "URR_PROCESSEDBY_C", length = 36)
    private String processedById;

    /**
     * Getter of id.
     * 
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter of id.
     * 
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter of username.
     * 
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter of username.
     * 
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter of email.
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter of email.
     * 
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter of password.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter of password.
     * 
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter of createDate.
     * 
     * @return createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Setter of createDate.
     * 
     * @param createDate createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Getter of status.
     * 
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Setter of status.
     * 
     * @param status status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Getter of processDate.
     * 
     * @return processDate
     */
    public Date getProcessDate() {
        return processDate;
    }

    /**
     * Setter of processDate.
     * 
     * @param processDate processDate
     */
    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    /**
     * Getter of processedById.
     * 
     * @return processedById
     */
    public String getProcessedById() {
        return processedById;
    }

    /**
     * Setter of processedById.
     * 
     * @param processedById processedById
     */
    public void setProcessedById(String processedById) {
        this.processedById = processedById;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("email", email)
                .add("status", status)
                .toString();
    }
}
