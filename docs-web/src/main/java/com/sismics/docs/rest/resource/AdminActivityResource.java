package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.AuditLogDao;
import com.sismics.docs.core.dao.dto.AuditLogDto;
import com.sismics.docs.core.model.context.AppContext;
import com.sismics.docs.core.util.jpa.PaginatedList;
import com.sismics.docs.core.util.jpa.PaginatedLists;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ForbiddenClientException;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Admin activity resource.
 * 
 * @author [YourName]
 */
@Path("/admin/activity")
public class AdminActivityResource extends BaseResource {
    /**
     * Returns all user activities.
     *
     * @param limit      Page limit
     * @param offset     Page offset
     * @param sortColumn Sort column
     * @param asc        If true, ascending sorting, else descending
     * @return Response
     */
    @GET
    public Response get(
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset,
            @QueryParam("sort_column") Integer sortColumn,
            @QueryParam("asc") Boolean asc) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Initialize the returned object
        JsonObjectBuilder response = Json.createObjectBuilder();
        JsonArrayBuilder activities = Json.createArrayBuilder();

        // Build the query
        AuditLogDao auditLogDao = new AuditLogDao();
        PaginatedList<AuditLogDto> paginatedList = PaginatedLists.create(limit, offset);
        SortCriteria sortCriteria = new SortCriteria(sortColumn, asc);

        auditLogDao.findAllUserActivities(paginatedList, sortCriteria);

        // Build the response
        for (AuditLogDto auditLogDto : paginatedList.getResultList()) {
            activities.add(Json.createObjectBuilder()
                    .add("id", auditLogDto.getId())
                    .add("create_date", auditLogDto.getCreateTimestamp())
                    .add("username", auditLogDto.getUsername())
                    .add("class", auditLogDto.getEntityClass())
                    .add("type", auditLogDto.getType().name())
                    .add("message", auditLogDto.getMessage() != null ? auditLogDto.getMessage() : ""));
        }

        response.add("total", paginatedList.getResultCount())
                .add("activities", activities);

        return Response.ok().entity(response.build()).build();
    }
}
