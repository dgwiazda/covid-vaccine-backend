package com.dgwiazda.covidvaccine.functional.webScrapping.service;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Service
public class WebScraping {

    public static final String HREF_ATRRIBUTE = "href";

    //vaccine points
    public static final String VACCINE_POINTS_URL = "https://polon.nauka.gov.pl/pomoc/knowledge-base/wykaz-punktow-szczepien-plik-do-pobrania/";
    public static final String VACCINE_POINTS_CSS_QUERY = "td > a";
    public static final String VACCINE_POINTS_NAME = "vaccinePoints";
    public static final String VACCINE_POINTS_FILE_TYPE = ".xlsx";

    //nop
    public static final String NOP_DOMAIN = "https://www.gov.pl";
    public static final String NOP_URL = "https://www.gov.pl/web/szczepimysie/niepozadane-odczyny-poszczepienne";
    public static final String NOP_CSS_QUERY = "a.file-download";
    public static final String NOP_NAME = "nopReport";
    public static final String NOP_ATTR_FILE_TYPE = "aria-label";
    @Setter
    @Getter
    private String nopFileType = ".pdf";

    //infections
    public static final String INFECTIONS_URL = "https://www.arcgis.com/sharing/rest/content/items/153a138859bb4c418156642b5b74925b/data";
    public static final String INFECTIONS_NAME = "infections";
    public static final String INFECTIONS_FILE_TYPE = ".csv";

    //vaccines
    public static final String VACCINES_URL = "https://www.arcgis.com/sharing/rest/content/items/0b17f540e23e4871a1196fd4097f9659/data";
    public static final String VACCINES_NAME = "vaccines";
    public static final String VACCINES_FILE_TYPE = ".csv";

    public URL getData(String domain, String url, String cssQuery, String attributeKey) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select(cssQuery);
            return new URL(domain + links.last().attr(attributeKey));
        } catch (IOException e) {
            throw new RuntimeException("It's a bad url.", e);
        }
    }

    public void recognizeNopFileType() {
        try {
            Document document = Jsoup.connect(NOP_URL).get();
            Elements links = document.select(NOP_CSS_QUERY);
            String str = links.last().attr(NOP_ATTR_FILE_TYPE);
            String lastCharacters = str.substring(str.length() - 4);
            if (lastCharacters.equals(".docx")) {
                setNopFileType(".docx");
            } else {
                setNopFileType("wrong");
            }
        } catch (IOException e) {
            throw new RuntimeException("It's a bad url.", e);
        }
    }

    public void saveData(URL website, String name, String fileType) {
        try {
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(name + fileType);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a new file.", e);
        }
    }
}
