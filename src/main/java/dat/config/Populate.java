package dat.config;

import dat.entities.Animal;
import dat.entities.OpeningHours;
import dat.entities.VetClinic;
import dat.enums.Species;
import dat.enums.Weekday;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Populate {

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


        // Opret nogle testdata
        List<Animal> animals = getAnimals();
        List<VetClinic> vetClinics = getVetClinics();

        /*

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            animals.forEach(em::persist);
            vetClinics.forEach(em::persist);
            em.getTransaction().commit();
        } finally {
            emf.close();
        }
*/

        // Persist klinikkerne i databasen
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            vetClinics.forEach(em::persist);
            em.getTransaction().commit();
        } finally {
            emf.close();
        }
    }

    private static List<VetClinic> getVetClinics() {
        List<VetClinic> vetClinics = new ArrayList<>();

        VetClinic vetClinic1 = new VetClinic("Lyngby Dyreklinik", "Firskovvej 18", "33212334");
        VetClinic vetClinic2 = new VetClinic("Glostrup Dyreklinik", "Tværvej 4", "23456789");
        VetClinic vetClinic3 = new VetClinic("Holbæk Dyrehospital", "Stenhusvej 49", "15161718");

        // Opret og tilknyt åbningstider til klinikken
        vetClinic1.addOpeningHour(new OpeningHours(Weekday.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        vetClinic1.addOpeningHour(new OpeningHours(Weekday.TUESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        vetClinic1.addOpeningHour(new OpeningHours(Weekday.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)));
        vetClinic1.addOpeningHour(new OpeningHours(Weekday.THURSDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        vetClinic1.addOpeningHour(new OpeningHours(Weekday.FRIDAY, LocalTime.of(8, 0), LocalTime.of(14, 0)));

        vetClinic2.addOpeningHour(new OpeningHours(Weekday.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        vetClinic2.addOpeningHour(new OpeningHours(Weekday.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        vetClinic2.addOpeningHour(new OpeningHours(Weekday.SUNDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        vetClinic2.addOpeningHour(new OpeningHours(Weekday.FRIDAY, LocalTime.of(17, 0), LocalTime.of(23, 0)));

        vetClinic3.addOpeningHour(new OpeningHours(Weekday.MONDAY, LocalTime.of(0, 0), LocalTime.of(8, 0)));
        vetClinic3.addOpeningHour(new OpeningHours(Weekday.TUESDAY, LocalTime.of(0, 0), LocalTime.of(8, 0)));
        vetClinic3.addOpeningHour(new OpeningHours(Weekday.WEDNESDAY, LocalTime.of(0, 0), LocalTime.of(7, 0)));
        vetClinic3.addOpeningHour(new OpeningHours(Weekday.THURSDAY, LocalTime.of(0, 0), LocalTime.of(8, 0)));
        vetClinic3.addOpeningHour(new OpeningHours(Weekday.FRIDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)));


        vetClinics.add(vetClinic1);
        vetClinics.add(vetClinic2);
        vetClinics.add(vetClinic3);

        return vetClinics;
    }

    private static List<Animal> getAnimals() {
        List<Animal> animals = new ArrayList<>();

        animals.add(new Animal("Bulldog", Species.DOG, 5));
        animals.add(new Animal("Siamese Cat", Species.CAT, 3));
        animals.add(new Animal("Parrot", Species.BIRD, 2));
        animals.add(new Animal("Persian Cat", Species.CAT, 4));
        animals.add(new Animal("Golden Retriever", Species.DOG, 6));

        return animals;
    }
}
