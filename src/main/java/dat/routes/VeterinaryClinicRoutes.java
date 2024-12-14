package dat.routes;

import dat.controllers.AnimalController;
import dat.controllers.VetClinicController;
import dat.daos.impl.VetClinicDAO;
import dat.entities.VetClinic;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class VeterinaryClinicRoutes {
    private final VetClinicController controller = new VetClinicController();


    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", controller::create, Role.ANYONE);
            get("/", controller::readAll, Role.ANYONE);
            get("/{id}", controller::read, Role.ANYONE);
            put("/{id}", controller::update, Role.ANYONE);
            delete("/{id}", controller::delete, Role.ANYONE);
        };
    }
}
