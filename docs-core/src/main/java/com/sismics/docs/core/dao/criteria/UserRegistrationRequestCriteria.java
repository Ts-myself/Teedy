package com.sismics.docs.core.dao.criteria;

import com.sismics.docs.core.model.jpa.UserRegistrationRequest;

/**
 * User registration request criteria.
 *
 * @author Teedy
 */
public class UserRegistrationRequestCriteria {
    /**
     * Status.
     */
    private UserRegistrationRequest.Status status;

    /**
     * Getter for status.
     *
     * @return status
     */
    public UserRegistrationRequest.Status getStatus() {
        return status;
    }

    /**
     * Setter for status.
     *
     * @param status status
     * @return this
     */
    public UserRegistrationRequestCriteria setStatus(UserRegistrationRequest.Status status) {
        this.status = status;
        return this;
    }
}