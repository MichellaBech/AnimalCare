package dat.controllers;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.dtos.VetClinicDTO;
import dat.entities.VetClinic;
import dat.security.controllers.SecurityController;
import dat.security.daos.SecurityDAO;
import dat.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import io.javalin.Javalin;

import io.restassured.common.mapper.TypeRef;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VetClinicControllerDBTest {

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
        app = ApplicationConfig.startServer(7070);
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        em.getTransaction().begin();

        // Populate database with test VetClinics
        VetClinic vetClinic1 = new VetClinic("Central Animal Hospital", "123 Main St", "123-456-7890");
        VetClinic vetClinic2 = new VetClinic("Westside Vet", "456 West Ave", "987-654-3210");

        em.persist(vetClinic1);
        em.persist(vetClinic2);

        em.getTransaction().commit();

        UserDTO[] users = Populator.populateUsers(emf);
        userDTO = users[0];
        adminDTO = users[1];

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getUsername(), adminDTO.getPassword());
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM VetClinic").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
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

    // Test: Hent alle VetClinics
    @Test
    public void testGetAllVetClinics() {
        List<VetClinicDTO> vetClinics =
                given()
                        .header("Authorization", userToken)
                        .when()
                        .get(BASE_URL + "/vetclinics")
                        .then()
                        .statusCode(200)
                        .body("size()", is(2))
                        .log().all()
                        .extract()
                        .as(new TypeRef<List<VetClinicDTO>>() {});

        assertThat(vetClinics.size(), is(2));
        assertThat(vetClinics.get(0).getName(), is("Central Animal Hospital"));
    }

    // Test: Hent VetClinic efter ID
    @Test
    public void testGetVetClinicById() {
        VetClinicDTO vetClinic = given()
                .header("Authorization", userToken)
                .when()
                .get(BASE_URL + "/vetclinics/1")
                .then()
                .statusCode(200)
                .extract()
                .as(VetClinicDTO.class);

        assertThat(vetClinic.getName(), is("Central Animal Hospital"));
    }

    // Test: Opret en ny VetClinic
    @Test
    public void testCreateVetClinic() {
        VetClinicDTO newClinic = given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .body("{\"name\":\"Northside Vet\",\"address\":\"789 North Blvd\",\"phone\":\"111-222-3333\"}")
                .when()
                .post(BASE_URL + "/vetclinics")
                .then()
                .statusCode(201)
                .extract()
                .as(VetClinicDTO.class);

        assertThat(newClinic.getName(), is("Northside Vet"));
        assertThat(newClinic.getAddress(), is("789 North Blvd"));
    }

    // Test: Opdater en VetClinic
    @Test
    public void testUpdateVetClinic() {
        VetClinicDTO updatedClinic = given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .body("{\"name\":\"Updated Vet Clinic\",\"address\":\"New Address\",\"phone\":\"123-123-1234\"}")
                .when()
                .put(BASE_URL + "/vetclinics/1")
                .then()
                .statusCode(200)
                .extract()
                .as(VetClinicDTO.class);

        assertThat(updatedClinic.getName(), is("Updated Vet Clinic"));
        assertThat(updatedClinic.getAddress(), is("New Address"));
    }

    // Test: Slet en VetClinic
    @Test
    public void testDeleteVetClinic() {
        given()
                .header("Authorization", adminToken)
                .when()
                .delete(BASE_URL + "/vetclinics/1")
                .then()
                .statusCode(200);
    }
}
