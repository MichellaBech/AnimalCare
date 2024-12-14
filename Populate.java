package dat.config;

import com.example.demo.entities.Doctor;
import com.example.demo.enums.Speciality;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Set;

public class Populate {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

        Set<Doctor> doctors = getDoctors();

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            doctors.forEach(em::persist);
            em.getTransaction().commit();
        } finally {
            emf.close();
        }
    }

    @NotNull
    private static Set<Doctor> getDoctors() {
        Doctor d1 = new Doctor(null, "Dr. Alice Smith", LocalDate.of(1975, 4, 12), 2000, "City Health Clinic", Speciality.FAMILY_MEDICINE);
        Doctor d2 = new Doctor(null, "Dr. Bob Johnson", LocalDate.of(1980, 8, 5), 2005, "Downtown Medical Center", Speciality.SURGERY);
        Doctor d3 = new Doctor(null, "Dr. Clara Lee", LocalDate.of(1983, 7, 22), 2008, "Green Valley Hospital", Speciality.PEDIATRICS);
        Doctor d4 = new Doctor(null, "Dr. David Park", LocalDate.of(1978, 11, 15), 2003, "Hillside Medical Practice", Speciality.PSYCHIATRY);
        Doctor d5 = new Doctor(null, "Dr. Emily White", LocalDate.of(1982, 9, 30), 2007, "Metro Health Center", Speciality.GERIATRICS);
        Doctor d6 = new Doctor(null, "Dr. Fiona Martinez", LocalDate.of(1985, 2, 17), 2010, "Riverside Wellness Clinic", Speciality.SURGERY);
        Doctor d7 = new Doctor(null, "Dr. George Kim", LocalDate.of(1979, 5, 29), 2004, "Summit Health Institute", Speciality.FAMILY_MEDICINE);

        return Set.of(d1, d2, d3, d4, d5, d6, d7);
    }
}
