package com.dgwiazda.covidvaccine.statistics.nop.persistance.model;


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
@Table(name = "T_NOP")
public class NopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nopIdGenSeq")
    @SequenceGenerator(name = "nopIdGenSeq", sequenceName = "nop_id_seq", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SEX")
    private String sex;

    @Column(name = "NOP_DESCRIPTION")
    private String nopDescription;

    @Column(name = "DATE")
    private LocalDate date;

    @Column(name = "PROVINCE")
    private String province;
}
