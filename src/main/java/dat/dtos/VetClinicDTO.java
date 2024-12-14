package dat.dtos;

import dat.entities.VetClinic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class VetClinicDTO {

        private Long id;
        private String name;
        private String address;
        private String phone;

        public VetClinicDTO(VetClinic vetClinic) {
            this.id = vetClinic.getId();
            this.name = vetClinic.getName();
            this.address = vetClinic.getAddress();
            this.phone = vetClinic.getPhone();
        }

        public VetClinicDTO(String name, String address, String phone) {
            this.name = name;
            this.address = address;
            this.phone = phone;
        }



}
