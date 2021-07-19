package com.dgwiazda.covidvaccine.statistics.vaccines.persistance.model;

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
@Table(name = "T_VACCINES")
public class VaccinesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vaccinesIdGenSeq")
    @SequenceGenerator(name = "vaccinesIdGenSeq", sequenceName = "vaccines_id_seq", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROVINCE")
    private String province;

    @Column(name = "VACCINES_COUNT")
    private Long vaccinesCount;

    @Column(name = "DATE")
    private LocalDate date;
}
