package dat.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import dat.enums.Weekday;


    @Entity
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(exclude = "veterinaryClinic")
    @ToString(exclude = "veterinaryClinic")  // Exclude relationship from toString to avoid recursion
    @Table(name = "opening_hours")
    public class OpeningHours {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false, updatable = false)
        private Long id;

        // Enum to represent the day of the week
        @Enumerated(EnumType.STRING)
        @Column(name = "weekday", nullable = false)
        private Weekday weekday;

        // Start and end times for the shift
        @Column(name = "start_time", nullable = false)
        private LocalTime startTime;

        @Column(name = "end_time", nullable = false)
        private LocalTime endTime;

        // Many-to-One relationship with VeterinaryClinic, lazy load to improve performance
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "veterinary_clinic_id", nullable = false)
        private VetClinic veterinaryClinic;

        public OpeningHours(Weekday weekday, LocalTime startTime, LocalTime endTime) {
            this.weekday = weekday;
            this.startTime = startTime;
            this.endTime = endTime;
        }


        // Method to check if a vet is currently on duty for the given day and time
        public boolean isOnDuty(LocalTime currentTime, Weekday currentWeekday) {
            if (this.weekday != currentWeekday) {
                return false;
            }
            return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        }
    }
