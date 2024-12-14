package dat.entities;

import dat.dtos.VetClinicDTO;
import lombok.Data;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class VetClinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phone;


    // OneToMany relationship with OpeningHours
    @OneToMany(mappedBy = "veterinaryClinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OpeningHours> openingHours;

    // Add convenience method to populate opening hours
    public void addOpeningHour(OpeningHours openingHour) {
        if (openingHours == null) {
            openingHours = new HashSet<>();
        }
        openingHours.add(openingHour);
        openingHour.setVeterinaryClinic(this);
    }

    public VetClinic(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public VetClinic() {
    }

    public VetClinic(VetClinicDTO vetClinicDTO) {
        this.name = vetClinicDTO.getName();
        this.address = vetClinicDTO.getAddress();
        this.phone = vetClinicDTO.getPhone();
    }

}
