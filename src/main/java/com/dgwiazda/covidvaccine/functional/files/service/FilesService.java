package com.dgwiazda.covidvaccine.functional.files.service;

import com.dgwiazda.covidvaccine.functional.files.enums.ProvinceEnum;
import com.dgwiazda.covidvaccine.functional.webScrapping.service.WebScraping;
import com.dgwiazda.covidvaccine.statistics.infections.persistance.model.InfectionsEntity;
import com.dgwiazda.covidvaccine.statistics.nop.persistance.model.NopEntity;
import com.dgwiazda.covidvaccine.statistics.vaccines.persistance.model.VaccinesEntity;
import com.dgwiazda.covidvaccine.statistics.infections.persistance.dao.InfectionsRepository;
import com.dgwiazda.covidvaccine.statistics.nop.persistance.dao.NopRepository;
import com.dgwiazda.covidvaccine.statistics.vaccines.persistance.dao.VaccinesRepository;
import com.dgwiazda.covidvaccine.functional.files.reader.BasicCsvReader;
import com.dgwiazda.covidvaccine.functional.files.reader.BasicDocxReader;
import com.dgwiazda.covidvaccine.functional.files.reader.BasicXlsxReader;
import com.dgwiazda.covidvaccine.vaccinePoints.persistance.dao.VaccinePointsRepository;
import com.dgwiazda.covidvaccine.vaccinePoints.persistance.model.VaccinePointsEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilesService {

    private final InfectionsRepository infectionsRepository;
    private final VaccinePointsRepository vaccinePointsRepository;
    private final VaccinesRepository vaccinesRepository;
    private final NopRepository nopRepository;
    private final WebScraping webScraping;

    @Setter
    @Getter
    private LocalDateTime updateDateTime = LocalDateTime.now();

    private final Logger logger = LoggerFactory.getLogger(FilesService.class);

    public void saveAll() {
        saveVaccinePoints();
        saveNop();
        saveVaccines();
        saveInfections();
    }

    public void updateAllTables() {
        // update only if file updated
        if (webScraping.getNopFileType().equals(".docx")) {
            updateTableNop();
        }
        updateTableVaccinePoints();
        updateTableInfections();
        updateTableVaccines();
        setUpdateDateTime(LocalDateTime.now());
    }

    private void saveVaccinePoints() {
        try {
            URL vaccinePointsData = webScraping.getData("",
                    WebScraping.VACCINE_POINTS_URL,
                    WebScraping.VACCINE_POINTS_CSS_QUERY,
                    WebScraping.HREF_ATRRIBUTE
            );
            webScraping.saveData(vaccinePointsData,
                    WebScraping.VACCINE_POINTS_NAME,
                    WebScraping.VACCINE_POINTS_FILE_TYPE
            );
        } catch (Exception e) {
            logger.error("Exception occurred while getting vaccine points data from website or updating file.", e);
        }
    }

    private void saveNop() {
        try {
            URL nopData = webScraping.getData(WebScraping.NOP_DOMAIN,
                    WebScraping.NOP_URL,
                    WebScraping.NOP_CSS_QUERY,
                    WebScraping.HREF_ATRRIBUTE
            );
            // check extension and set to nopFileType
            webScraping.recognizeNopFileType();
            // update file only if has .docx extension
            if (webScraping.getNopFileType().equals(".docx")) {
                webScraping.saveData(nopData,
                        WebScraping.NOP_NAME,
                        webScraping.getNopFileType());
            }
        } catch (Exception e) {
            logger.error("Exception occurred while getting nop data from website or updating file.", e);
        }
    }

    private void saveVaccines() {
        try {
            URL vaccinesData = new URL(WebScraping.VACCINES_URL);
            webScraping.saveData(vaccinesData,
                    WebScraping.VACCINES_NAME,
                    WebScraping.VACCINES_FILE_TYPE
            );
        } catch (Exception e) {
            logger.error("Exception occurred while getting vaccines data from website or updating file.", e);
        }
    }

    private void saveInfections() {
        try {
            URL infectionsData = new URL(WebScraping.INFECTIONS_URL);
            webScraping.saveData(infectionsData,
                    WebScraping.INFECTIONS_NAME,
                    WebScraping.INFECTIONS_FILE_TYPE
            );
        } catch (Exception e) {
            logger.error("Exception occurred while getting infections data from website or updating file.", e);
        }
    }

    private void updateTableInfections() {
        BasicCsvReader basicCsvReader = new BasicCsvReader();
        List<String[]> csvFile = basicCsvReader.readCsv(BasicCsvReader.INFECTIONS_FILE_NAME);
        for (String[] csvRows : csvFile) { // for any row in file table
            int currentCell = 0; // value created to read column number
            InfectionsEntity infectionsEntity = new InfectionsEntity();
            for (String cellAttribute : csvRows) { // for any cell in row
                if (currentCell == 1) {
                    infectionsEntity.setInfectionCases(Long.parseLong(cellAttribute));
                } else if (currentCell == 3) {
                    infectionsEntity.setDeaths(Long.parseLong(cellAttribute));
                } else if (currentCell == 13) {
                    String province = fromTerytToProvince(cellAttribute);
                    infectionsEntity.setProvince(province);
                } else if (currentCell == 14) {
                    infectionsEntity.setDate(LocalDate.now());
                }
                currentCell++;
            }
            // if this row hadn't any null values => save
            if (infectionsEntity.getDate() != null &&
                    infectionsEntity.getDeaths() != null &&
                    infectionsEntity.getInfectionCases() != null &&
                    infectionsEntity.getProvince() != null) {
                infectionsRepository.save(infectionsEntity);
            }
        }
    }

    private void updateTableVaccines() {
        BasicCsvReader basicCsvReader = new BasicCsvReader();
        List<String[]> csvFile = basicCsvReader.readCsv(BasicCsvReader.VACCINES_FILE_NAME);
        for (String[] csvRows : csvFile) { // for any rows in file table
            int currentCell = 0; // value created to read column number
            boolean skip = false;
            VaccinesEntity vaccinesEntity = new VaccinesEntity();
            for (String cellAttribute : csvRows) { // for any cell in row
                if (currentCell == 1) {
                    vaccinesEntity.setVaccinesCount(Long.parseLong(cellAttribute));
                } else if (currentCell == 5) {
                    // read province (can return "wrong", if not expected territory)
                    String province = fromTerytToProvince(cellAttribute);
                    if (province.equals("wrong")) { // not expected => leave columns loop
                        skip = true;
                        break;
                    }
                    vaccinesEntity.setProvince(province);
                }
                vaccinesEntity.setDate(LocalDate.now());
                currentCell++;
            }
            // if this row hadn't any null values => save
            if (!skip &&
                    vaccinesEntity.getDate() != null &&
                    vaccinesEntity.getProvince() != null &&
                    vaccinesEntity.getVaccinesCount() != null) {
                vaccinesRepository.save(vaccinesEntity);
            }
        }
    }

    private void updateTableVaccinePoints() {
        BasicXlsxReader xlsxReader = new BasicXlsxReader();
        long insertedRows = vaccinePointsRepository.count(); // check how much rows already inserted
        // for any rows in file table
        for (int rowNumber = 1; rowNumber < xlsxReader.getNumberOfRows() + 1; rowNumber++) {
            VaccinePointsEntity vaccinePointsEntity = new VaccinePointsEntity();
            if (rowNumber > insertedRows) { // continue only if row wasn't inserted in db yet
                // for any column in row
                for (int colNumber = 0; colNumber < xlsxReader.getNumberOfCells(); colNumber++) {
                    if (colNumber == 0) {
                        vaccinePointsEntity.setName(xlsxReader.readCellData(rowNumber, colNumber));
                    } else if (colNumber == 1) {
                        if (rowNumber == 868) { // column with null Town value
                            vaccinePointsEntity.setTown("Gdańsk");
                        } else {
                            vaccinePointsEntity.setTown(xlsxReader.readCellData(rowNumber, colNumber));
                        }
                    } else if (colNumber == 2) {
                        vaccinePointsEntity.setStreet(xlsxReader.readCellData(rowNumber, colNumber));
                    } else if (colNumber == 4) {
                        vaccinePointsEntity.setNfzDepartment(xlsxReader.readCellData(rowNumber, colNumber));
                    } else if (colNumber == xlsxReader.getNumberOfCells() - 1) {
                        // get integer day, month, year values from string
                        ArrayList<Integer> dayMonthYear = getDate(xlsxReader.readCellData(rowNumber, colNumber),
                                "-",
                                false);
                        vaccinePointsEntity.setAdditionDate(LocalDate.of(
                                dayMonthYear.get(2),
                                dayMonthYear.get(1),
                                dayMonthYear.get(0)
                        ));
                    }
                }
                // if this row hadn't any null values => save
                if (vaccinePointsEntity.getAdditionDate() != null &&
                        vaccinePointsEntity.getName() != null &&
                        vaccinePointsEntity.getNfzDepartment() != null &&
                        vaccinePointsEntity.getStreet() != null &&
                        vaccinePointsEntity.getTown() != null) {
                    vaccinePointsRepository.save(vaccinePointsEntity);
                }
            }
        }
    }

    private void updateTableNop() {
        BasicDocxReader documentReader = new BasicDocxReader();
        XWPFDocument document = documentReader.readDocx();
        List<IBodyElement> bodyElements = document.getBodyElements();
        long insertedRows = nopRepository.count(); // check how much rows already inserted
        long currentRow = 0;
        for (IBodyElement bodyElement : bodyElements) {
            if (bodyElement instanceof XWPFTable) { // look for table in file
                XWPFTable table = (XWPFTable) bodyElement;
                List<XWPFTableRow> rows = table.getRows();
                for (XWPFTableRow row : rows) { // for any row in table
                    List<XWPFTableCell> tableCells = row.getTableCells();
                    NopEntity nopEntity = new NopEntity();
                    int cellNumber = 0; // 5191 nulle
                    if (currentRow == 0) { // table header
                        currentRow++;
                        continue;
                    }
                    if (currentRow > insertedRows) { // continue only if row wasn't inserted in db yet
                        for (XWPFTableCell tableCell : tableCells) { // for any column in row
                            if (cellNumber == 1) {
                                if (!tableCell.getText().isEmpty() && tableCell.getText() != null && tableCell.getText().equals("")) {
                                    ArrayList<Integer> dayMonthYear = getDate(tableCell.getText(),
                                            ".",
                                            true);
                                    if (currentRow > 1196 && currentRow < 1203) { // rows with bad months in data
                                        nopEntity.setDate(LocalDate.of(
                                                dayMonthYear.get(2),
                                                2,
                                                dayMonthYear.get(0))
                                        );
                                    } else {
                                        nopEntity.setDate(LocalDate.of(
                                                dayMonthYear.get(2),
                                                dayMonthYear.get(1),
                                                dayMonthYear.get(0))
                                        );
                                    }
                                } else {
                                    break;
                                }
                            } else if (cellNumber == 2) {
                                if (!tableCell.getText().isEmpty() && tableCell.getText() != null && tableCell.getText().equals("")) {
                                    nopEntity.setProvince(tableCell.getText());
                                } else {
                                    break;
                                }
                            } else if (cellNumber == 4) {
                                if (!tableCell.getText().isEmpty() && tableCell.getText() != null && tableCell.getText().equals("")) {
                                    String sex = findSexInNopTable(tableCell.getText());
                                    nopEntity.setSex(sex);
                                } else {
                                    break;
                                }
                            } else if (cellNumber == 5) {
                                if (!tableCell.getText().isEmpty() && tableCell.getText() != null && tableCell.getText().equals("")) {
                                    nopEntity.setNopDescription(tableCell.getText());
                                } else {
                                    break;
                                }
                            }
                            cellNumber++;
                        }
                        // if this row hadn't any null values => save
                        if (nopEntity.getDate() != null &&
                                nopEntity.getNopDescription() != null &&
                                nopEntity.getSex() != null) {
                            nopRepository.save(nopEntity);
                        }
                    }
                    currentRow++;
                }
            }
        }
    }

    private String fromTerytToProvince(String teryt) {
        String province;
        switch (teryt) {
            case "t00":
                province = ProvinceEnum.KRAJ.getId();
                break;
            case "t02":
                province = ProvinceEnum.DOLNOSLASKIE.getId();
                break;
            case "t04":
                province = ProvinceEnum.KUJAWSKO_POMORSKIE.getId();
                break;
            case "t06":
                province = ProvinceEnum.LUBELSKIE.getId();
                break;
            case "t08":
                province = ProvinceEnum.LUBUSKIE.getId();
                break;
            case "t10":
                province = ProvinceEnum.LODZKIE.getId();
                break;
            case "t12":
                province = ProvinceEnum.MALOPOLSKIE.getId();
                break;
            case "t14":
                province = ProvinceEnum.MAZOWIECKIE.getId();
                break;
            case "t16":
                province = ProvinceEnum.OPOLSKIE.getId();
                break;
            case "t18":
                province = ProvinceEnum.PODKARPACKIE.getId();
                break;
            case "t20":
                province = ProvinceEnum.PODLASKIE.getId();
                break;
            case "t22":
                province = ProvinceEnum.POMORSKIE.getId();
                break;
            case "t24":
                province = ProvinceEnum.SLASKIE.getId();
                break;
            case "t26":
                province = ProvinceEnum.SWIETOKRZYSKIE.getId();
                break;
            case "t28":
                province = ProvinceEnum.WARMINSKO_MAZURSKIE.getId();
                break;
            case "t30":
                province = ProvinceEnum.WIELKOPOLSKIE.getId();
                break;
            case "t32":
                province = ProvinceEnum.ZACHODNIPOMORSKIE.getId();
                break;
            default:
                province = ProvinceEnum.INNE.getId();
                break;
        }
        return province;
    }

    // dayMonthYear: true if date = day/month/year, false if date = year/month/day
    private ArrayList<Integer> getDate(String date, String delimiter, Boolean dayMonthYear) {
        String temp = "";
        int day = 0, month = 0, year = 0;
        for (int i = 0; i < 3; i++) { // 3 times => 3 values from date
            int splitter = date.indexOf(delimiter);
            if (splitter != -1) { // if date contains delimiter
                temp = date.substring(0, splitter);
                date = date.substring(splitter + 1);
            }
            if (dayMonthYear) {
                if (i == 0) {
                    day = Integer.parseInt(temp);
                }
                if (i == 1) {
                    month = Integer.parseInt(temp);
                }
                if (i == 2) {
                    year = Integer.parseInt(date);
                }
            } else {
                if (i == 0) {
                    year = Integer.parseInt(temp);
                }
                if (i == 1) {
                    month = Integer.parseInt(temp);
                }
                if (i == 2) {
                    day = Integer.parseInt(date);
                }
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        list.add(day);
        list.add(month);
        list.add(year);
        return list;
    }

    // function to read correctly sex data
    private String findSexInNopTable(String sex) {
        for (int i = 0; i < sex.length(); i++) {
            char c = sex.charAt(i);
            if (c == 'K' || c == 'k') {
                return "K";
            }
            if (c == 'M' || c == 'm') {
                return "M";
            }
        }
        return null;
    }
}
