/*
package dat.daos;

import dat.config.HibernateConfig;
import dat.daos.impl.VetClinicDAO;
import dat.dtos.VetClinicDTO;
import dat.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VetClinicDAOTest {

    private static EntityManagerFactory emf;
    private VetClinicDAO vetClinicDAO;

    @BeforeAll
    public void init() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        vetClinicDAO = VetClinicDAO.getInstance(emf);
    }

    @BeforeEach
    public void setUp() {
        // Rens databasen før hver test
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM VetClinic").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE vetclinic_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public void tearDownAll() {
        emf.close();
    }

    // Test: Create VetClinic
    @Test
    public void testCreateVetClinic() throws ApiException {
        VetClinicDTO vetClinicDTO = new VetClinicDTO("Central Vet", "123 Main St", "12345678");
        VetClinicDTO created = vetClinicDAO.create(vetClinicDTO);

        assertNotNull(created);
        assertEquals("Central Vet", created.getName());
        assertEquals("123 Main St", created.getAddress());
        assertEquals("12345678", created.getPhone());
    }

    // Test: Read VetClinic by ID
    @Test
    public void testReadVetClinicById() throws ApiException {
        VetClinicDTO vetClinicDTO = vetClinicDAO.create(new VetClinicDTO("Westside Vet", "456 West Ave", "98765432"));
        VetClinicDTO found = vetClinicDAO.read(Math.toIntExact(vetClinicDTO.getId()));

        assertNotNull(found);
        assertEquals("Westside Vet", found.getName());
    }

    // Test: Read All VetClinics
    @Test
    public void testReadAllVetClinics() throws ApiException {
        vetClinicDAO.create(new VetClinicDTO("Central Vet", "123 Main St", "12345678"));
        vetClinicDAO.create(new VetClinicDTO("Westside Vet", "456 West Ave", "98765432"));

        List<VetClinicDTO> vetClinics = vetClinicDAO.readAll();

        assertNotNull(vetClinics);
        assertEquals(2, vetClinics.size());
    }

    // Test: Update VetClinic
    @Test
    public void testUpdateVetClinic() throws ApiException {
        // Opret en ny VetClinic
        VetClinicDTO original = vetClinicDAO.create(new VetClinicDTO("Central Vet", "123 Main St", "12345678"));

        // Opdater felter
        VetClinicDTO updatedDTO = new VetClinicDTO("Updated Vet", "456 New St", "99988877");
        VetClinicDTO updated = vetClinicDAO.update(Math.toIntExact(original.getId()), updatedDTO);

        assertNotNull(updated);
        assertEquals("Updated Vet", updated.getName());
        assertEquals("456 New St", updated.getAddress());
        assertEquals("99988877", updated.getPhone());
    }

    // Test: Delete VetClinic
    @Test
    public void testDeleteVetClinic() throws ApiException {
        VetClinicDTO vetClinicDTO = vetClinicDAO.create(new VetClinicDTO("Delete Vet", "999 Delete St", "00011122"));

        assertDoesNotThrow(() -> vetClinicDAO.delete(Math.toIntExact(vetClinicDTO.getId())));

        ApiException exception = assertThrows(ApiException.class, () -> vetClinicDAO.read(Math.toIntExact(vetClinicDTO.getId())));
        assertEquals("VetClinic not found", exception.getMessage());
    }

    // Test: Read Non-existing VetClinic
    @Test
    public void testReadNonExistingVetClinic() {
        ApiException exception = assertThrows(ApiException.class, () -> vetClinicDAO.read(999));
        assertEquals("VetClinic not found", exception.getMessage());
    }
}

*/
