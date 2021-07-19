package com.dgwiazda.covidvaccine.functional.files.reader;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class BasicDocxReader {

    public final static String NOP_FILE_NAME = "nopReport.docx";
    public final static String PATH = "C:\\Users\\mlodz\\Desktop\\studiaMGR\\magisterka\\covid-vaccine-backend\\";

    public XWPFDocument readDocx() {
        try {
            FileInputStream fis = new FileInputStream(PATH + NOP_FILE_NAME);
            return new XWPFDocument(fis);
        } catch (IOException e) {
            throw new RuntimeException("Can not read a .docx file", e);
        }
    }
}
