package dat.routes;
import dat.controllers.AnimalController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AnimalRoutes {

    private final AnimalController animalController = new AnimalController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", animalController::create, Role.ANYONE);
            get("/", animalController::readAll, Role.ANYONE);
            get("/{id}", animalController::read, Role.ANYONE);
            put("/{id}", animalController::update, Role.ANYONE);
            delete("/{id}", animalController::delete, Role.ANYONE);
        };
    }
}

