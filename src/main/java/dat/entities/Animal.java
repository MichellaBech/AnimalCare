package dat.entities;

import dat.dtos.AnimalDTO;
import dat.enums.Species;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private Species species;
    private int age;


    public Animal(AnimalDTO animalDTO) {
        this.id = animalDTO.getId();
        this.name = animalDTO.getName();
        this.species = animalDTO.getSpecies();
        this.age = animalDTO.getAge();
    }

    public Animal(String name, Species species, int age) {
        this.name = name;
        this.species = species;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id) &&
                Objects.equals(name, animal.name) &&
                Objects.equals(species, animal.species) &&
                Objects.equals(age, animal.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, species, age);
    }
}
