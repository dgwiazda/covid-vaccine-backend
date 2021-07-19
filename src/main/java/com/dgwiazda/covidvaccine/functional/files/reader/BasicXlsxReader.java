package com.dgwiazda.covidvaccine.functional.files.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class BasicXlsxReader {

    public final static String VACCINE_POINTS_FILE_NAME = "vaccinePoints.xlsx";
    public final static String PATH = "C:\\Users\\mlodz\\Desktop\\studiaMGR\\magisterka\\covid-vaccine-backend\\";


    private Workbook readXlsx() {
        try {
            FileInputStream fis = new FileInputStream(PATH + VACCINE_POINTS_FILE_NAME);
            return new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException("Can not read a file", e);
        }
    }

    public String readCellData(int vRow, int vColumn) {
        BasicXlsxReader xlsxReader = new BasicXlsxReader();
        Workbook wb = xlsxReader.readXlsx();
        String value;
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(vRow);
        Cell cell = row.getCell(vColumn);
        value = cell.getStringCellValue();
        return value;
    }

    public Integer getNumberOfRows() {
        BasicXlsxReader xlsxReader = new BasicXlsxReader();
        Workbook wb = xlsxReader.readXlsx();
        return wb.getSheetAt(0).getLastRowNum();
    }

    public Short getNumberOfCells() {
        BasicXlsxReader xlsxReader = new BasicXlsxReader();
        Workbook wb = xlsxReader.readXlsx();
        return wb.getSheetAt(0).getRow(0).getLastCellNum();
    }
}
