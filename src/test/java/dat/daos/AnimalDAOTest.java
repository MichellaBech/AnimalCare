package dat.daos;
/*
import dat.config.HibernateConfig;
import dat.daos.impl.AnimalDAO;
import dat.dtos.AnimalDTO;
import dat.entities.Animal;
import dat.entities.VetClinic;
import dat.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnimalDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private AnimalDAO animalDAO;
    private Animal testAnimal;

    @BeforeAll
    public void init() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        animalDAO = new AnimalDAO(emf);
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();

        // Reset and populate test data
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Animal").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE plant_id_seq RESTART WITH 1").executeUpdate();
        em.getTransaction().commit();

        testAnimal = new Animal();
        testAnimal.setName("Animal test");
        testAnimal.setMaxHeight(100);
        testAnimal.setPrice(150);
        testAnimal.setPlantType("Test type");

        em.getTransaction().begin();
        em.persist(testAnimal);
        em.getTransaction().commit();
    }

    @AfterEach
    public void tearDown() {
        if (em.isOpen()) em.close();
    }

    @AfterAll
    public void tearDownEntityManagerFactory() {
        if (emf.isOpen()) emf.close();
    }

    @Test
    public void create() throws ApiException {
        AnimalDTO newPlant = new AnimalDTO();
        newPlant.setName("New Animal");
        newPlant.setMaxHeight(200.00);
        newPlant.setPrice(250.00);
        newPlant.setPlantType("New type");

        AnimalDTO createdPlant = null;
            createdPlant = animalDAO.create(newPlant);

        assertNotNull(createdPlant);
        assertEquals("New Animal", createdPlant.getName());
        assertEquals(200.00, createdPlant.getMaxHeight());
        assertEquals(250.00, createdPlant.getPrice());
        assertEquals("New type", createdPlant.getPlantType());
    }

    @Test
    public void readAll() throws ApiException {
        List<AnimalDTO> plants = animalDAO.readAll();

        assertNotNull(plants);
        assertFalse(plants.isEmpty());

        AnimalDTO firstPlant = plants.get(0);
        assertEquals("Animal test", firstPlant.getName());
        assertEquals(100, firstPlant.getMaxHeight());
        assertEquals(150, firstPlant.getPrice());
        assertEquals("Test type", firstPlant.getPlantType());
    }

    @Test
    public void readById() throws ApiException {
        AnimalDTO foundPlant = animalDAO.read(Math.toIntExact(testAnimal.getId()));

        assertNotNull(foundPlant);
        assertEquals("Animal test", foundPlant.getName());
        assertEquals(100, foundPlant.getMaxHeight());
        assertEquals(150, foundPlant.getPrice());
        assertEquals("Test type", foundPlant.getPlantType());
    }

    @Test
    public void update() throws ApiException {
        // Arrange: Opret et nyt AnimalDTO-objekt med opdaterede data
        AnimalDTO updatedAnimalDTO = new AnimalDTO();
        updatedAnimalDTO.setName("Updated Animal");
        updatedAnimalDTO.setMaxHeight(200.00);
        updatedAnimalDTO.setPrice(250.00);
        updatedAnimalDTO.setPlantType("Updated type");

        // Act: Opdater planten i databasen
        AnimalDTO result = animalDAO.update(Math.toIntExact(testAnimal.getId()), updatedAnimalDTO);

        // Assert: Verificér at opdateringen returnerer det forventede resultat
        assertNotNull(result);
        assertEquals("Updated Animal", result.getName());
        assertEquals(200.00, result.getMaxHeight());
        assertEquals(250.00, result.getPrice());
        assertEquals("Updated type", result.getPlantType());

        // Læs planten igen direkte fra databasen for at sikre, at ændringerne blev gemt
        Animal updatedAnimal = em.find(Animal.class, testAnimal.getId());
        em.refresh(updatedAnimal); // Forfrisk objektet for at sikre opdaterede data

        // Assert: Verificér, at dataene i databasen er opdateret
        assertEquals("Updated Animal", updatedAnimal.getName());
        assertEquals(200.00, updatedAnimal.getMaxHeight());
        assertEquals(250.00, updatedAnimal.getPrice());
        assertEquals("Updated type", updatedAnimal.getPlantType());
    }


    @Test
    public void testAddPlantToReseller() throws ApiException {
        em.getTransaction().begin();

        // Create and persist a new vetClinic and plants for testing
        VetClinic vetClinic = new VetClinic("Lyngby Plantecenter", "Firskovvej 18", "33212334");
        Animal animal = new Animal("Rose", "Albertine", 400.0, 199.50);

        // Persist these new objects so they exist in the database for the test
        em.persist(vetClinic);
        em.persist(animal);

        em.getTransaction().commit();

        assertNotNull(vetClinic);
        assertNotNull(animal);

        VetClinic updatedVetClinic = animalDAO.addPlantToReseller(vetClinic.getId().intValue(), animal.getId().intValue());

        assertTrue(updatedVetClinic.getAnimals().contains(animal), "Animal should be added to the vetClinic's list");
    }

    @Test
    public void testGetPlantsByReseller() throws ApiException {
        em.getTransaction().begin();

        // Create and persist a new vetClinic and animals for testing
        VetClinic vetClinic = new VetClinic("Lyngby Plantecenter", "Firskovvej 18", "33212334");
        Animal animal1 = new Animal("Rose", "Albertine", 400.0, 199.50);
        Animal animal2 = new Animal("Bush", "Aronia", 200.0, 169.50);

        // Persist these new objects so they exist in the database for the test
        em.persist(vetClinic);
        em.persist(animal1);
        em.persist(animal2);

        // Link animals to vetClinic
        vetClinic.addPlant(animal1);
        vetClinic.addPlant(animal2);

        // Update vetClinic with associated animals
        em.merge(vetClinic);
        em.getTransaction().commit();
        em.clear(); // Clears the EntityManager cache

        em.getTransaction().begin(); // Start a new transaction to get the updated vetClinic object

        // Now test the DAO method to get animals by vetClinic
        List<Animal> animals = animalDAO.getPlantsByReseller(vetClinic.getId().intValue());

        em.getTransaction().commit(); // Commit the transaction

        // Assertions
        assertEquals(2, animals.size(), "VetClinic should have 2 animals associated.");
        assertTrue(animals.contains(animal1));
        assertTrue(animals.contains(animal2));
    }


    @Test
    public void testFilterPlantsByType() throws ApiException {
        em.getTransaction().begin();
        Animal roseAnimal = new Animal("Rose", "New Dawn", 300.0, 250.50);
        Animal appleAnimal = new Animal("Rose", "Golden Delicious", 400.0, 300.0);
        em.persist(appleAnimal);
        em.persist(roseAnimal);
        em.getTransaction().commit();

        List<AnimalDTO> filteredPlants = animalDAO.plantsByType("rose");

        assertEquals(2, filteredPlants.size(), "There should be 2 plants with type 'Rose'");
        assertTrue(filteredPlants.stream().allMatch(p -> p.getPlantType().equalsIgnoreCase("rose")),
                "All filtered plants should have the type 'Rose'");
    }


    }

*/



