package com.sismics.docs.core.dao.dto;

/**
 * User registration request DTO.
 *
 * @author Teedy
 */
public class UserRegistrationRequestDto {
    /**
     * ID.
     */
    private String id;

    /**
     * Username.
     */
    private String username;

    /**
     * Email.
     */
    private String email;

    /**
     * Creation date timestamp.
     */
    private Long createTimestamp;

    /**
     * Status.
     */
    private String status;

    /**
     * Processing date timestamp.
     */
    private Long processTimestamp;

    /**
     * Processed by username.
     */
    private String processedByUsername;

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
     * Getter of createTimestamp.
     *
     * @return createTimestamp
     */
    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * Setter of createTimestamp.
     *
     * @param createTimestamp createTimestamp
     */
    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    /**
     * Getter of status.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter of status.
     *
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Getter of processTimestamp.
     *
     * @return processTimestamp
     */
    public Long getProcessTimestamp() {
        return processTimestamp;
    }

    /**
     * Setter of processTimestamp.
     *
     * @param processTimestamp processTimestamp
     */
    public void setProcessTimestamp(Long processTimestamp) {
        this.processTimestamp = processTimestamp;
    }

    /**
     * Getter of processedByUsername.
     *
     * @return processedByUsername
     */
    public String getProcessedByUsername() {
        return processedByUsername;
    }

    /**
     * Setter of processedByUsername.
     *
     * @param processedByUsername processedByUsername
     */
    public void setProcessedByUsername(String processedByUsername) {
        this.processedByUsername = processedByUsername;
    }
}