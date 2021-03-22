package com.dgwiazda.covidvaccine.persistance.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "T_VACCINE_TYPE")
public class VaccineTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vaccineTypeIdGenSeq")
    @SequenceGenerator(name = "vaccineTypeIdGenSeq", sequenceName = "VACCINE_TYPE_ID_SEQ", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "information_before_take")
    private String informationBeforeTake;
    @Column(name = "way_of_giving")
    private String wayOfGiving;
    @Column(name = "NOP_information")
    private String nop;

    public VaccineTypeEntity(Long id, String name, String informationBeforeTake, String wayOfGiving, String nop) {
        this.id = id;
        this.name = name;
        this.informationBeforeTake = informationBeforeTake;
        this.wayOfGiving = wayOfGiving;
        this.nop = nop;
    }
}
