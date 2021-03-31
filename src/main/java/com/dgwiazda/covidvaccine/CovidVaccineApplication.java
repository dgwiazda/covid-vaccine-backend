package com.dgwiazda.covidvaccine;

import com.dgwiazda.covidvaccine.persistance.model.WebScraping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@SpringBootApplication
public class CovidVaccineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CovidVaccineApplication.class, args);
    }

    @Scheduled(cron = "0 0 12 * * ?")
    private void getAndSaveEveryDay() throws MalformedURLException {
        WebScraping webScraping = new WebScraping();

        URL vaccinePointsData = webScraping.getData("", webScraping.getVaccinePointsUrl(), webScraping.getVaccinePointsCssQuery(), webScraping.getHrefAtrribute());
        webScraping.saveData(vaccinePointsData, webScraping.getVaccinePointsName(), webScraping.getVaccinePointsFileType());

        URL nopData = webScraping.getData(webScraping.getNopDomain(), webScraping.getNopUrl(), webScraping.getNopCssQuery(), webScraping.getHrefAtrribute());
        webScraping.saveData(nopData, webScraping.getNopName(), webScraping.getNopFileType());

        URL vaccinesData = new URL(webScraping.getVaccinesUrl());
        webScraping.saveData(vaccinesData, webScraping.getVaccinesName(), webScraping.getVaccinesFileType());

        URL infectionsData = new URL(webScraping.getInfectionsUrl());
        webScraping.saveData(infectionsData, webScraping.getInfectionsName(), webScraping.getInfectionsFileType());
    }

    @Configuration
    @EnableScheduling
    @ConditionalOnProperty(name = "sheduling.enabled", matchIfMissing = true)
    class SchedulingConfiguration {

    }
}
