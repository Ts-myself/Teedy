package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.criteria.UserRegistrationRequestCriteria;
import com.sismics.docs.core.dao.dto.UserRegistrationRequestDto;
import com.sismics.docs.core.model.jpa.UserRegistrationRequest;
import com.sismics.docs.core.util.jpa.QueryParam;
import com.sismics.docs.core.util.jpa.QueryUtil;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User registration request DAO.
 * 
 * @author Teedy
 */
public class UserRegistrationRequestDao {
    /**
     * Creates a new user registration request.
     * 
     * @param username Username
     * @param email    Email
     * @param password Password
     * @return ID
     */
    public String create(String username, String email, String password) {
        // Create the UUID
        String id = UUID.randomUUID().toString();

        // Create the user registration request
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
        userRegistrationRequest.setId(id);
        userRegistrationRequest.setUsername(username);
        userRegistrationRequest.setEmail(email);
        userRegistrationRequest.setPassword(password);
        userRegistrationRequest.setCreateDate(new Date());
        userRegistrationRequest.setStatus(UserRegistrationRequest.Status.PENDING);
        em.persist(userRegistrationRequest);

        return id;
    }

    /**
     * Gets an active user registration request by username.
     * 
     * @param username Username
     * @return User registration request
     */
    public UserRegistrationRequest getActiveByUsername(String username) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery(
                "select r from UserRegistrationRequest r where r.username = :username and r.status = :status");
        q.setParameter("username", username);
        q.setParameter("status", UserRegistrationRequest.Status.PENDING);
        try {
            return (UserRegistrationRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Gets an active user registration request by email.
     * 
     * @param email Email
     * @return User registration request
     */
    public UserRegistrationRequest getActiveByEmail(String email) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em
                .createQuery("select r from UserRegistrationRequest r where r.email = :email and r.status = :status");
        q.setParameter("email", email);
        q.setParameter("status", UserRegistrationRequest.Status.PENDING);
        try {
            return (UserRegistrationRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Gets a user registration request by ID.
     * 
     * @param id ID
     * @return User registration request
     */
    public UserRegistrationRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select r from UserRegistrationRequest r where r.id = :id");
        q.setParameter("id", id);
        try {
            return (UserRegistrationRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Deletes a user registration request.
     * 
     * @param id User registration request ID
     */
    public void delete(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("delete from UserRegistrationRequest r where r.id = :id");
        q.setParameter("id", id);
        q.executeUpdate();
    }

    /**
     * Approves a user registration request.
     * 
     * @param id     User registration request ID
     * @param userId User ID approving the request
     */
    public void approve(String id, String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery(
                "update UserRegistrationRequest r set r.status = :status, r.processDate = :processDate, r.processedById = :processedById where r.id = :id");
        q.setParameter("status", UserRegistrationRequest.Status.APPROVED);
        q.setParameter("processDate", new Date());
        q.setParameter("processedById", userId);
        q.setParameter("id", id);
        q.executeUpdate();
    }

    /**
     * Rejects a user registration request.
     * 
     * @param id     User registration request ID
     * @param userId User ID rejecting the request
     */
    public void reject(String id, String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery(
                "update UserRegistrationRequest r set r.status = :status, r.processDate = :processDate, r.processedById = :processedById where r.id = :id");
        q.setParameter("status", UserRegistrationRequest.Status.REJECTED);
        q.setParameter("processDate", new Date());
        q.setParameter("processedById", userId);
        q.setParameter("id", id);
        q.executeUpdate();
    }

    // /**
    // * Find user registration requests by criteria.
    // *
    // * @param criteria Search criteria
    // * @param sortCriteria Sort criteria
    // * @param offset Offset
    // * @param limit Limit
    // * @return List of user registration requests
    // */
    // @SuppressWarnings("unchecked")
    // public List<UserRegistrationRequestDto>
    // findByCriteria(UserRegistrationRequestCriteria criteria,
    // String sortCriteria, Integer offset, Integer limit) {
    // List<String> criteriaList = new ArrayList<>();
    // List<QueryParam> paramList = new ArrayList<>();

    // StringBuilder sb = new StringBuilder(
    // "select r.URR_ID_C, r.URR_USERNAME_C, r.URR_EMAIL_C, r.URR_CREATEDATE_D,
    // r.URR_STATUS_C, r.URR_PROCESSDATE_D, u.USE_USERNAME_C ");
    // sb.append(" from T_USER_REGISTRATION_REQUEST r ");
    // sb.append(" left join T_USER u on u.USE_ID_C = r.URR_PROCESSEDBY_C ");

    // // Add search criteria
    // if (criteria.getStatus() != null) {
    // criteriaList.add("r.URR_STATUS_C = :status");
    // paramList.add(new QueryParam("status", criteria.getStatus().name()));
    // }

    // if (!criteriaList.isEmpty()) {
    // sb.append(" where ");
    // sb.append(String.join(" and ", criteriaList));
    // }

    // // Add sort
    // if (sortCriteria == null) {
    // // Default sort: create date DESC
    // sb.append(" order by r.URR_CREATEDATE_D desc");
    // } else {
    // sb.append(" order by ").append(sortCriteria);
    // }

    // // Create the query
    // EntityManager em = ThreadLocalContext.get().getEntityManager();
    // Query q = em.createNativeQuery(sb.toString());

    // // Set parameters
    // for (QueryParam queryParam : paramList) {
    // q.setParameter(queryParam.getName(), queryParam.getValue());
    // }

    // // Set limit and offset
    // if (limit != null) {
    // q.setMaxResults(limit);
    // if (offset != null) {
    // q.setFirstResult(offset);
    // }
    // }

    // // Execute the query
    // List<Object[]> resultList = q.getResultList();

    // // Assemble results
    // List<UserRegistrationRequestDto> requestDtoList = new ArrayList<>();
    // for (Object[] result : resultList) {
    // int i = 0;
    // UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
    // requestDto.setId((String) result[i++]);
    // requestDto.setUsername((String) result[i++]);
    // requestDto.setEmail((String) result[i++]);
    // requestDto.setCreateTimestamp(((Date) result[i++]).getTime());
    // requestDto.setStatus((String) result[i++]);
    // Date processDate = (Date) result[i++];
    // if (processDate != null) {
    // requestDto.setProcessTimestamp(processDate.getTime());
    // }
    // requestDto.setProcessedByUsername((String) result[i]);
    // requestDtoList.add(requestDto);
    // }

    // return requestDtoList;
    // }
}
