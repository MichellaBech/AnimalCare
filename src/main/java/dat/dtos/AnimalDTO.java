package dat.dtos;

import dat.entities.Animal;
import dat.enums.Species;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class AnimalDTO {
        private Long id;
        private String name;
        private Species species;
        private int age;

        public AnimalDTO(Animal animal) {
            this.id = animal.getId();
            this.name = animal.getName();
            this.species = animal.getSpecies();
            this.age = animal.getAge();
        }

        public AnimalDTO(String name, Species species, int age) {
            this.name = name;
            this.species = species;
            this.age = age;
        }

    }
