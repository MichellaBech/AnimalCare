package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.AnimalDTO;
import dat.dtos.VetClinicDTO;
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
public class VetClinicDAO implements IDAO<VetClinicDTO, Integer> {

    private static VetClinicDAO instance;
    private static EntityManagerFactory emf;
    private static final Logger logger = LoggerFactory.getLogger(VetClinicDAO.class);


    public static VetClinicDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new VetClinicDAO();
        }
        return instance;
    }

    @Override
    public VetClinicDTO read(Integer integer) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            VetClinic vetClinic = em.find(VetClinic.class, integer);
            if (vetClinic == null) {
                logger.warn("VetClinic with ID {} not found", integer);
                throw new ApiException(404, "VetClinic not found");
            }
            return new VetClinicDTO(vetClinic);
        }
    }

    @Override
    public List<VetClinicDTO> readAll() throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<VetClinicDTO> query = em.createQuery("SELECT new dat.dtos.VetClinicDTO(h) FROM VetClinic h", VetClinicDTO.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            logger.error("Error reading all VetClinics: ", e);
            throw new ApiException(400, "Something went wrong during readAll");
        }
    }

    @Override
    public VetClinicDTO create(VetClinicDTO vetClinicDTO) throws ApiException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            VetClinic vetClinic = new VetClinic(vetClinicDTO);
            em.persist(vetClinic);
            em.getTransaction().commit();
            return new VetClinicDTO(vetClinic);
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error creating VetClinic: ", e);
            throw new ApiException(400, "VetClinic already exists or something else went wrong");
        } finally {
            em.close();
        }
    }

    @Override
    public VetClinicDTO update(Integer integer, VetClinicDTO vetClinicDTO) throws ApiException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            VetClinic existingVetClinic = em.find(VetClinic.class, integer);
            if (existingVetClinic == null) {
                throw new ApiException(404, "VetClinic not found for update");
            }

            // Opdater kun felter, der ikke er null i animalDTO
            if (vetClinicDTO.getName() != null) {
                existingVetClinic.setName(vetClinicDTO.getName());
            }
            if (vetClinicDTO.getAddress() != null) {
                existingVetClinic.setAddress(vetClinicDTO.getAddress());
            }
            if (vetClinicDTO.getPhone() != null) {
                existingVetClinic.setPhone(vetClinicDTO.getPhone());
            }


            // Merg og commit
            VetClinic mergedVetClinic = em.merge(existingVetClinic);
            em.getTransaction().commit();
            return mergedVetClinic != null ? new VetClinicDTO(mergedVetClinic) : null;
        }  catch (PersistenceException e) {
            logger.error("Error updating VetClinic: ", e);
            throw new ApiException(400, "VetClinic not found or something else went wrong during update");
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer integer) throws ApiException {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                VetClinic vetClinic = em.find(VetClinic.class, integer);
                if (vetClinic == null) {
                    throw new ApiException(404, "VetClinic not found for deletion");
                }
                em.remove(vetClinic);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                logger.error("Error deleting VetClinic: ", e);
                throw new ApiException(400, "Something went wrong during deletion");
            } finally {
                em.close();
            }
        }



}
