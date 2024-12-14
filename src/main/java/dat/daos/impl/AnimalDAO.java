package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.AnimalDTO;
import dat.entities.Animal;
import dat.entities.VetClinic;
import dat.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AnimalDAO implements IDAO<AnimalDTO, Integer> {

    private static AnimalDAO instance;
    private static EntityManagerFactory emf;
    private static final Logger logger = LoggerFactory.getLogger(AnimalDAO.class);

    public AnimalDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static AnimalDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AnimalDAO();
        }
        return instance;
    }

    @Override
    public AnimalDTO read(Integer integer) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            Animal animal = em.find(Animal.class, integer);
            if (animal == null) {
                logger.warn("Animal with ID {} not found", integer);
                throw new ApiException(404, "Animal not found");
            }
            return new AnimalDTO(animal);
        }
    }

    @Override
    public List<AnimalDTO> readAll() throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<AnimalDTO> query = em.createQuery("SELECT new dat.dtos.AnimalDTO(h) FROM Animal h", AnimalDTO.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            logger.error("Error reading all animals: ", e);
        throw new ApiException(400, "Something went wrong during readAll");
    }
    }

    @Override
    public AnimalDTO create(AnimalDTO animalDTO) throws ApiException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Animal animal = new Animal(animalDTO);
            em.persist(animal);
            em.getTransaction().commit();
            return new AnimalDTO(animal);
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error creating animal: ", e);
            throw new ApiException(400, "Animal already exists or something else went wrong");
        } finally {
            em.close();
        }
    }

    @Override
    public AnimalDTO update(Integer integer, AnimalDTO animalDTO) throws ApiException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Animal existingAnimal = em.find(Animal.class, integer);
            if (existingAnimal == null) {
                throw new ApiException(404, "Animal not found for update");
            }

            // Opdater kun felter, der ikke er null i animalDTO
            if (animalDTO.getName() != null) {
                existingAnimal.setName(animalDTO.getName());
            }
            if (animalDTO.getSpecies() != null) {
                existingAnimal.setSpecies(animalDTO.getSpecies());
            }
            if (animalDTO.getAge() != 0) {
                existingAnimal.setAge(animalDTO.getAge());
            }

            // Merg og commit
            Animal mergedAnimal = em.merge(existingAnimal);
            em.getTransaction().commit();
            return mergedAnimal != null ? new AnimalDTO(mergedAnimal) : null;
        }  catch (PersistenceException e) {
            logger.error("Error updating animal: ", e);
        throw new ApiException(400, "Animal not found or something else went wrong during update");
    } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer integer) throws ApiException {
        EntityManager em = emf.createEntityManager();
        try  {
            em.getTransaction().begin();
            Animal animal = em.find(Animal.class, integer);
            if (animal != null) {
                em.remove(animal);
            } else {
                logger.warn("Animal with ID {} not found", integer);
                throw new ApiException(404, "Animal not found for delete");
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


}
