package dat.controllers;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.dtos.AnimalDTO;
import dat.entities.Animal;
import dat.entities.VetClinic;
import dat.security.controllers.SecurityController;
import dat.security.daos.SecurityDAO;
import dat.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import io.javalin.Javalin;

import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.common.mapper.TypeRef;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnimalControllerDBTest {

    private static Javalin app;
    private EntityManager em;
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final static SecurityController securityController = SecurityController.getInstance();
    private final static SecurityDAO securityDAO = new SecurityDAO(emf);

    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static final String BASE_URL = "http://localhost:7070/api";

        @BeforeAll
        void setUpAll() {

            // Start api
            app = ApplicationConfig.startServer(7070);
        }

        @BeforeEach
        void setup() {

            //Populate the database with plants and vetClinics
            System.out.println("Populating database with plants and vetClinics");

            em = emf.createEntityManager();
            em.getTransaction().begin();

            // Create and persist test Plants and Resellers
            Animal animal1 = new Animal("Rose", "Albertine", 400.0, 199.50);
            Animal animal2 = new Animal("Lily", "White Lily", 50.0, 129.99);
            VetClinic vetClinic1 = new VetClinic("GardenCenter", "123 Green St", "123-456-7890");


            em.persist(animal1);
            em.persist(animal2);
            em.persist(vetClinic1);

            // Add plants to reseller and update reseller
            vetClinic1.addPlant(animal1);
            vetClinic1.addPlant(animal2);
            em.merge(vetClinic1);

            em.getTransaction().commit();

            UserDTO[] users = Populator.populateUsers(emf);
            userDTO = users[0];
            adminDTO = users[1];

            try {
                UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
                UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getUsername(), adminDTO.getPassword());
                userToken = "Bearer " + securityController.createToken(verifiedUser);
                adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
            }
            catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Animal ").executeUpdate();
            em.createQuery("DELETE FROM VetClinic ").executeUpdate();
            em.createQuery("DELETE FROM Role ").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (em.isOpen()) em.close();
    }

    @AfterAll
    void tearDownAll() {
        ApplicationConfig.stopServer(app);
    }


    @Test
    public void testGetAllPlants() {
        List<AnimalDTO> animalDTO =
                given()
                .header("Authorization", userToken) // Include the user token for authorization
                .when()
                .get(BASE_URL + "/plants")
                .then()
                .statusCode(200)
                        .body("size()", is(2))
                        .log().all()
                        .extract()
                        .as(new TypeRef<List<AnimalDTO>>() {});

        assertThat(animalDTO.size(), is(2));
        assertThat(animalDTO.get(0).getName(), is("Albertine"));
        assertThat(animalDTO.get(1).getName(), is("White Lily"));
    }

    @Test
    public void testGetPlantById() {
        AnimalDTO animalDTO = given()
                .header("Authorization", userToken) // Include the user token for authorization
                .when()
                .get(BASE_URL + "/plants/1")
                .then()
                .statusCode(200)
                .extract()
                .as(AnimalDTO.class); // Extract response as AnimalDTO

        assertThat(animalDTO.getName(), is("Albertine"));
    }


    @Test
    public void testGetPlantsByType() {
        AnimalDTO[] animalDTOS = given()
                .header("Authorization", userToken)
                .when()
                .get(BASE_URL + "/plants/planttype/Rose")
                .then()
                .statusCode(200)
                .extract()
                .as(AnimalDTO[].class);

        assertThat(animalDTOS.length, greaterThan(0));
        assertThat(animalDTOS[0].getPlantType(), is("Rose"));
    }


    @Test
    public void testGetPlantsByReseller() {
        AnimalDTO[] animalDTOS = given()
                .header("Authorization", userToken) // Include the user token for authorization
                .when()
                .get(BASE_URL + "/plants/reseller/1")
                .then()
                .statusCode(200)
                .extract()
                .as(AnimalDTO[].class);

        assertThat(animalDTOS.length, is(2));
        assertThat(animalDTOS[1].getName(), is("White Lily"));
    }

    @Test
    public void testCreatePlant() {
        AnimalDTO animalDTO = given()
                .header("Authorization", adminToken) // Include the admin token for authorization
                .contentType("application/json")
                .body("{\"name\":\"Daisy\",\"plantType\":\"Bellis Perennis\",\"maxHeight\":10.0,\"price\":5.99}")
                .when()
                .post(BASE_URL + "/plants")
                .then()
                .statusCode(201)
                .extract()
                .as(AnimalDTO.class);

        assertThat(animalDTO.getName(), is("Daisy"));
        assertThat(animalDTO.getPlantType(), is("Bellis Perennis"));
    }

    @Test
    public void testUpdatePlant() {
        AnimalDTO animalDTO = given()
                .header("Authorization", adminToken) // Include the admin token for authorization
                .contentType("application/json")
                .body("{\"name\":\"Daisy\",\"plantType\":\"Bellis Perennis\",\"maxHeight\":10.0,\"price\":5.99}")
                .when()
                .put(BASE_URL + "/plants/1")
                .then()
                .statusCode(200)
                .extract()
                .as(AnimalDTO.class);

        assertThat(animalDTO.getName(), is("Daisy"));
        assertThat(animalDTO.getMaxHeight(), is(10.0));
    }

    @Test
    public void testDeletePlant() {

        given()
                .header("Authorization", userToken)
                .when()
                .delete(BASE_URL + "/plants/1")
                .then()
                .statusCode(200);
    }


    @Test
    public void testAddPlantToReseller() {
        given()
                .header("Authorization", userToken) // Include the user token for authorization
                .when()
                .post(BASE_URL + "/plants/1/reseller/1")
                .then()
                .statusCode(200);
    }

}
