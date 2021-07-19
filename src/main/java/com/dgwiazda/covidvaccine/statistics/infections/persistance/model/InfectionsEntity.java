package com.dgwiazda.covidvaccine.statistics.infections.persistance.model;

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
@Table(name = "T_INFECTIONS")
public class InfectionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "infectionsIdGenSeq")
    @SequenceGenerator(name = "infectionsIdGenSeq", sequenceName = "infections_id_seq", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROVINCE")
    private String province;

    @Column(name = "INFECTION_CASES")
    private Long infectionCases;

    @Column(name = "DEATHS")
    private Long deaths;

    @Column(name = "DATE")
    private LocalDate date;
}
