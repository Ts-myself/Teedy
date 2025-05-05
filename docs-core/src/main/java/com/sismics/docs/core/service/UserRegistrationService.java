package com.sismics.docs.core.service;

import com.google.common.base.Strings;
import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.UserRegistrationRequestDao;
import com.sismics.docs.core.dao.criteria.UserRegistrationRequestCriteria;
import com.sismics.docs.core.dao.dto.UserRegistrationRequestDto;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRegistrationRequest;
import com.sismics.docs.core.util.EncryptionUtil;
import com.sismics.util.EmailUtil;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * User registration request service.
 *
 * @author Teedy
 */
public class UserRegistrationService {
    /**
     * Creates a new registration request.
     *
     * @param username Username
     * @param email    Email
     * @param password Password
     * @param locale   User locale
     * @return Request ID
     * @throws Exception If an error occurs
     */
    public String createRegistrationRequest(String username, String email, String password, String locale)
            throws Exception {
        // Check if the email is valid
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email == null || !email.matches(emailRegex)) {
            throw new Exception("InvalidEmail");
        }

        // Check if the username already exists
        UserDao userDao = new UserDao();
        User user = userDao.getActiveByUsername(username);
        if (user != null) {
            throw new Exception("AlreadyExistingUsername");
        }

        // Check if the email already exists
        user = userDao.getActiveByEmail(email);
        if (user != null) {
            throw new Exception("AlreadyExistingEmail");
        }

        // Check if there's already a pending request for this username
        UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
        UserRegistrationRequest existingRequest = requestDao.getActiveByUsername(username);
        if (existingRequest != null) {
            throw new Exception("AlreadyExistingUsername");
        }

        // Check if there's already a pending request for this email
        existingRequest = requestDao.getActiveByEmail(email);
        if (existingRequest != null) {
            throw new Exception("AlreadyExistingEmail");
        }

        // Encrypt password
        String encryptedPassword = EncryptionUtil.hashPassword(password);

        // Create the request
        return requestDao.create(username, email, encryptedPassword);
    }

    /**
     * Gets a registration request by ID.
     *
     * @param id Registration request ID
     * @return Registration request
     */
    public UserRegistrationRequest getRegistrationRequest(String id) {
        UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
        return requestDao.getById(id);
    }

    /**
     * Approves a registration request.
     *
     * @param id     Registration request ID
     * @param userId User ID approving the request
     * @throws Exception If an error occurs
     */
    public void approveRegistrationRequest(String id, String userId) throws Exception {
        UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
        UserRegistrationRequest request = requestDao.getById(id);

        if (request == null) {
            throw new Exception("RegistrationRequestNotFound");
        }

        if (request.getStatus() != UserRegistrationRequest.Status.PENDING) {
            throw new Exception("RegistrationRequestNotPending");
        }

        // Check if username or email is now taken by another user
        UserDao userDao = new UserDao();
        User existingUser = userDao.getActiveByUsername(request.getUsername());
        if (existingUser != null) {
            throw new Exception("AlreadyExistingUsername");
        }

        existingUser = userDao.getActiveByEmail(request.getEmail());
        if (existingUser != null) {
            throw new Exception("AlreadyExistingEmail");
        }

        // Create the user
        UserService userService = new UserService();
        userService.create(request.getUsername(), request.getPassword(), request.getEmail(),
                Constants.DEFAULT_THEME_ID, "eng", false);

        // Update the request status
        requestDao.approve(id, userId);
    }

    /**
     * Rejects a registration request.
     *
     * @param id     Registration request ID
     * @param userId User ID rejecting the request
     * @throws Exception If an error occurs
     */
    public void rejectRegistrationRequest(String id, String userId) throws Exception {
        UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
        UserRegistrationRequest request = requestDao.getById(id);

        if (request == null) {
            throw new Exception("RegistrationRequestNotFound");
        }

        if (request.getStatus() != UserRegistrationRequest.Status.PENDING) {
            throw new Exception("RegistrationRequestNotPending");
        }

        // Update the request status
        requestDao.reject(id, userId);
    }

    /**
     * Returns all pending registration requests.
     *
     * @return List of registration requests
     */
    public List<UserRegistrationRequest> findPendingRequests() {
        UserRegistrationRequestCriteria criteria = new UserRegistrationRequestCriteria()
                .setStatus(UserRegistrationRequest.Status.PENDING);

        return findByCriteria(criteria);
    }

    /**
     * Returns all registration requests.
     *
     * @return List of registration requests
     */
    public List<UserRegistrationRequest> findAllRequests() {
        return findByCriteria(null);
    }

    /**
     * Returns registration requests with the given status.
     *
     * @param status Status
     * @return List of registration requests
     */
    public List<UserRegistrationRequest> findByStatus(UserRegistrationRequest.Status status) {
        UserRegistrationRequestCriteria criteria = new UserRegistrationRequestCriteria()
                .setStatus(status);

        return findByCriteria(criteria);
    }

    /**
     * Finds registration requests by criteria.
     *
     * @param criteria Search criteria
     * @return List of registration requests
     */
    private List<UserRegistrationRequest> findByCriteria(UserRegistrationRequestCriteria criteria) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        StringBuilder sb = new StringBuilder("select r from UserRegistrationRequest r");

        // Add criteria
        if (criteria != null && criteria.getStatus() != null) {
            sb.append(" where r.status = :status");
        }

        // Order by create date (newest first)
        sb.append(" order by r.createDate desc");

        jakarta.persistence.Query q = em.createQuery(sb.toString());

        // Set parameters
        if (criteria != null && criteria.getStatus() != null) {
            q.setParameter("status", criteria.getStatus());
        }

        // Execute query
        @SuppressWarnings("unchecked")
        List<UserRegistrationRequest> requests = q.getResultList();
        return requests;
    }
}
