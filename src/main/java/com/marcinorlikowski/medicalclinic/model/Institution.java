package com.marcinorlikowski.medicalclinic.model;

import com.marcinorlikowski.medicalclinic.dto.CreateInstitutionCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String city;
    private String postalCode;
    private String streetName;
    private String buildingNumber;
    @ManyToMany(mappedBy = "institutions")
    private List<Doctor> doctors = new ArrayList<>();

    public Institution(CreateInstitutionCommand command) {
        this.name = command.name();
        this.city = command.city();
        this.postalCode = command.postalCode();
        this.streetName = command.streetName();
        this.buildingNumber = command.buildingNumber();
    }

    public void updateInstitution(CreateInstitutionCommand command) {
        this.name = command.name();
        this.city = command.city();
        this.postalCode = command.postalCode();
        this.streetName = command.streetName();
        this.buildingNumber = command.buildingNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Institution))
            return false;
        Institution other = (Institution) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
