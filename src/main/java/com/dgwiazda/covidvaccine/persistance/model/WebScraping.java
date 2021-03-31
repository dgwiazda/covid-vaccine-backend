package com.dgwiazda.covidvaccine.persistance.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class WebScraping {

    final private String hrefAtrribute = "href";

    //vaccine points
    final private String vaccinePointsUrl = "https://polon.nauka.gov.pl/pomoc/knowledge-base/wykaz-punktow-szczepien-plik-do-pobrania/";
    final private String vaccinePointsCssQuery = "td > a";
    final private String vaccinePointsName = "vaccinePoints";
    final private String vaccinePointsFileType = ".xlsx";

    //nop
    final String nopDomain = "https://www.gov.pl";
    final String nopUrl = "https://www.gov.pl/web/szczepimysie/niepozadane-odczyny-poszczepienne";
    final String nopCssQuery = "a.file-download";
    final String nopName = "nopReport";
    final String nopFileType = ".pdf";

    //infections
    final String infectionsUrl = "https://www.arcgis.com/sharing/rest/content/items/153a138859bb4c418156642b5b74925b/data";
    final String infectionsName = "infections";
    final String infectionsFileType = ".csv";

    //vaccines
    final String vaccinesUrl = "https://www.arcgis.com/sharing/rest/content/items/0b17f540e23e4871a1196fd4097f9659/data";
    final String vaccinesName = "vaccines";
    final String vaccinesFileType = ".csv";


    public URL getData(String domain, String url, String cssQuery, String attributeKey) throws MalformedURLException {
        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select(cssQuery);
            URL website = new URL(domain + links.last().attr(attributeKey));
            return website;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        URL error = new URL("");
        return error;
    }

    public void saveData(URL website, String name, String fileType) {
        try {
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(name + fileType);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getHrefAtrribute() {
        return hrefAtrribute;
    }

    public String getVaccinePointsUrl() {
        return vaccinePointsUrl;
    }

    public String getVaccinePointsCssQuery() {
        return vaccinePointsCssQuery;
    }

    public String getVaccinePointsName() {
        return vaccinePointsName;
    }

    public String getVaccinePointsFileType() {
        return vaccinePointsFileType;
    }

    public String getNopDomain() {
        return nopDomain;
    }

    public String getNopUrl() {
        return nopUrl;
    }

    public String getNopCssQuery() {
        return nopCssQuery;
    }

    public String getNopName() {
        return nopName;
    }

    public String getNopFileType() {
        return nopFileType;
    }

    public String getInfectionsUrl() {
        return infectionsUrl;
    }

    public String getInfectionsName() {
        return infectionsName;
    }

    public String getInfectionsFileType() {
        return infectionsFileType;
    }

    public String getVaccinesUrl() {
        return vaccinesUrl;
    }

    public String getVaccinesName() {
        return vaccinesName;
    }

    public String getVaccinesFileType() {
        return vaccinesFileType;
    }
}
