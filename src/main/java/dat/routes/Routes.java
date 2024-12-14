package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final AnimalRoutes animalRoutes = new AnimalRoutes();
    private final VeterinaryClinicRoutes veterinaryClinicRoutes = new VeterinaryClinicRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
                path("/animals", animalRoutes.getRoutes());
                path("/veterinaryclinics", veterinaryClinicRoutes.getRoutes());

        };
    }
}
