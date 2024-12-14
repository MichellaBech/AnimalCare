
package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.impl.AnimalDAO;
import dat.dtos.AnimalDTO;
import dat.entities.Animal;
import dat.exceptions.ApiException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AnimalController {

        private final AnimalDAO dao;
        Logger log = LoggerFactory.getLogger(AnimalController.class);

        public AnimalController() {
            EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
            this.dao = AnimalDAO.getInstance(emf);
        }

        public void read(Context ctx) throws ApiException {
            try {
                // request
                int id = ctx.pathParamAsClass("id", Integer.class).get();
                // DTO
                AnimalDTO plant = dao.read(id);
                    // response
                    log.info("Animal with ID {} found: {}", id, plant.getName());
                    ctx.res().setStatus(200);
                    ctx.json(plant, AnimalDTO.class);

            } catch (NumberFormatException e) {
                log.error("Invalid ID");
                ctx.status(400);
                throw new ApiException(400, "Missing required parameter: id");
            }
        }

        public void readAll(Context ctx) throws ApiException {
            // List of DTOS
            List<AnimalDTO> animalDTOS = dao.readAll();

            if(animalDTOS.isEmpty()) {
                log.warn("No animals found");
                ctx.status(404);
                throw new ApiException(404, "No animals found");
            }
            // response
            log.info("All animals found");
            ctx.res().setStatus(200);
            ctx.json(animalDTOS, AnimalDTO.class);
        }

        public void create(Context ctx) throws ApiException {
            try {
                // request
                AnimalDTO jsonRequest = ctx.bodyAsClass(AnimalDTO.class);

                if (jsonRequest == null) {
                    ctx.status(400);
                    throw new ApiException(400, "Bad request");
                }
                // DTO
                AnimalDTO animalDTO = dao.create(jsonRequest);

                // response
                log.info("Animal created: {}", animalDTO.getName());
                ctx.res().setStatus(201);
                ctx.json(animalDTO, AnimalDTO.class);
            } catch (ApiException e) {
                log.error("An error occurred while creating animal: {}", e.getMessage(), e);
                ctx.status(400);
                throw new ApiException(400, "Bad request");
            }
        }

        public void update(Context ctx) throws ApiException {
            try {
                // request
                int id = ctx.pathParamAsClass("id", Integer.class).get();
                // dto
                AnimalDTO animalDTO = dao.update(id, ctx.bodyAsClass(AnimalDTO.class));
                if (animalDTO == null) {
                    ctx.status(404);
                    log.warn("Animal with ID {} not found", id);
                    throw new ApiException(404, "Animal not found");
                }
                // response
                ctx.status(200);
                ctx.json(animalDTO, AnimalDTO.class);
            } catch (NumberFormatException e) {
                log.error("An error occurred while updating animal: {}", e.getMessage(), e);
                ctx.status(400);
                throw new ApiException(400, "Missing required parameter: id");
            }
        }

        public void delete(Context ctx) throws ApiException {
           try {
               // request
               int id = ctx.pathParamAsClass("id", Integer.class).get();
               dao.delete(id);
               // response
               ctx.res().setStatus(204);
           } catch (NumberFormatException e) {
               log.error("An error occurred while deleting animal: {}", e.getMessage(), e);
               ctx.status(400);
               throw new ApiException(400, "Missing required parameter: id");
           }
        }

}



