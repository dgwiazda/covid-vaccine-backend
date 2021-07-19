package com.dgwiazda.covidvaccine.functional.files.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProvinceEnum {

    KRAJ("Cały kraj"),
    DOLNOSLASKIE("dolnośląskie"),
    KUJAWSKO_POMORSKIE("kujawsko-pomorskie"),
    LUBELSKIE("lubelskie"),
    LUBUSKIE("lubuskie"),
    LODZKIE("łódzkie"),
    MALOPOLSKIE("małopolskie"),
    MAZOWIECKIE("mazowieckie"),
    OPOLSKIE("opolskie"),
    PODKARPACKIE("podkarpackie"),
    PODLASKIE("podlaskie"),
    POMORSKIE("pomorskie"),
    SLASKIE("śląskie"),
    SWIETOKRZYSKIE("świętokrzyskie"),
    WARMINSKO_MAZURSKIE("warmińsko-mazurskie"),
    WIELKOPOLSKIE("wielkopolskie"),
    ZACHODNIPOMORSKIE("zachodniopomorskie"),
    INNE("wrong");

    private final String id;
}
