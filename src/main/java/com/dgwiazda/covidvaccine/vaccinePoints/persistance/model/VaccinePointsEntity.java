package com.dgwiazda.covidvaccine.vaccinePoints.persistance.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_VACCINE_POINTS")
public class VaccinePointsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vaccinePointsIdGenSeq")
    @SequenceGenerator(name = "vaccinePointsIdGenSeq", sequenceName = "vaccine_points_id_seq", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TOWN")
    private String town;

    @Column(name = "STREET")
    private String street;

    @Column(name = "NFZ_DEPARTMENT")
    private String nfzDepartment;

    @Column(name = "ADDITION_DATE")
    private LocalDate additionDate;
}
