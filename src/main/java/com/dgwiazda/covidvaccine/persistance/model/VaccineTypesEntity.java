package com.dgwiazda.covidvaccine.persistance.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "T_VACCINE_TYPES")
public class VaccineTypesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vaccineTypesIdGenSeq")
    @SequenceGenerator(name = "vaccineTypesIdGenSeq", sequenceName = "FRACTION_ID_SEQ", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;

    public VaccineTypesEntity(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
