package dat.controllers;

import dat.daos.impl.VetClinicDAO;
import dat.dtos.AnimalDTO;
import dat.dtos.VetClinicDTO;
import dat.exceptions.ApiException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VetClinicController implements IController<VetClinicDTO, Integer> {

    private final VetClinicDAO dao;
    Logger log = LoggerFactory.getLogger(VetClinicController.class);

    public VetClinicController() {
        EntityManagerFactory emf = dat.config.HibernateConfig.getEntityManagerFactory();
        this.dao = VetClinicDAO.getInstance(emf); // Brug getInstance
    }

    @Override
    public void read(Context ctx) throws ApiException {
        try {
            // request
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            // DTO
            VetClinicDTO vetClinic = dao.read(id);
            // response
            log.info("Animal with ID {} found: {}", id, vetClinic.getName());
            ctx.res().setStatus(200);
            ctx.json(vetClinic, VetClinicDTO.class);

        } catch (NumberFormatException e) {
            log.error("Invalid ID");
            ctx.status(400);
            throw new ApiException(400, "Missing required parameter: id");
        }
    }

    @Override
    public void readAll(Context ctx) throws ApiException {
        // List of DTOS
        List<VetClinicDTO> vetClinicDTOS = dao.readAll();

        if(vetClinicDTOS.isEmpty()) {
            log.warn("No VetClinics found");
            ctx.status(404);
            throw new ApiException(404, "No VetClinics found");
        }
        // response
        log.info("All VetClinics found");
        ctx.res().setStatus(200);
        ctx.json(vetClinicDTOS, VetClinicDTO.class);
    }

    @Override
    public void create(Context ctx) throws ApiException {
        try {
            // request
            VetClinicDTO jsonRequest = ctx.bodyAsClass(VetClinicDTO.class);

            if (jsonRequest == null) {
                ctx.status(400);
                throw new ApiException(400, "Bad request");
            }
            // DTO
            VetClinicDTO vetClinicDTO = dao.create(jsonRequest);

            // response
            log.info("VetClinic created: {}", vetClinicDTO.getName());
            ctx.res().setStatus(201);
            ctx.json(vetClinicDTO, VetClinicDTO.class);
        } catch (ApiException e) {
            log.error("An error occurred while creating VetClinic: {}", e.getMessage(), e);
            ctx.status(400);
            throw new ApiException(400, "Bad request");
        }
    }

    @Override
    public void update(Context ctx) throws ApiException {
        try {
            // request
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            // dto
            VetClinicDTO vetClinicDTO = dao.update(id, ctx.bodyAsClass(VetClinicDTO.class));
            if (vetClinicDTO == null) {
                ctx.status(404);
                log.warn("VetClinic with ID {} not found", id);
                throw new ApiException(404, "VetClinic not found");
            }
            // response
            ctx.status(200);
            ctx.json(vetClinicDTO, AnimalDTO.class);
        } catch (NumberFormatException e) {
            log.error("An error occurred while updating VetClinic: {}", e.getMessage(), e);
            ctx.status(400);
            throw new ApiException(400, "Missing required parameter: id");
        }
    }
    @Override
    public void delete(Context ctx) throws ApiException {
        try {
            // request
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            dao.delete(id);
            // response
            ctx.res().setStatus(204);
        } catch (NumberFormatException e) {
            log.error("An error occurred while deleting VetClinic: {}", e.getMessage(), e);
            ctx.status(400);
            throw new ApiException(400, "Missing required parameter: id");
        }
    }
}
