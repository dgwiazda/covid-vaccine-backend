package com.dgwiazda.covidvaccine.functional.files.reader;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Component
public class BasicCsvReader {

    public final static String INFECTIONS_FILE_NAME = "infections.csv";
    public final static String VACCINES_FILE_NAME = "vaccines.csv";
    public final static String PATH = "C:\\Users\\mlodz\\Desktop\\studiaMGR\\magisterka\\covid-vaccine-backend\\";

    public List<String[]> readCsv(String fileName) {
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(PATH + fileName))
                .withCSVParser(csvParser)
                .withSkipLines(1)
                .build()) {
            return reader.readAll();
        } catch (IOException e) {
            throw new RuntimeException("File not found.", e);
        } catch (CsvException e) {
            throw new RuntimeException("Failed to read a .csv file.", e);
        }
    }
}
