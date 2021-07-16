package com.dgwiazda.covidvaccine;

import com.dgwiazda.covidvaccine.files.service.FilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class FilesServiceJob {

    private final FilesService filesService;

    @Scheduled(cron = "0 20 12 * * ?")
    private void getAndSaveEveryDay() {
        filesService.saveAll();
    }

    @Scheduled(cron = "0 23 12 * * ?")
    private void updateDatabaseTables() {
        filesService.updateAllTables();
    }
}
