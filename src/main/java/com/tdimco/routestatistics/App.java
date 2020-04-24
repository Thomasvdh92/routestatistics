package com.tdimco.routestatistics;

import com.tdimco.routestatistics.dataaccess.SpanCollectionDao;
import com.tdimco.routestatistics.datareading.XmlIterator;
import com.tdimco.routestatistics.domain.*;

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        String xmlMonthMay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 5";
        String xmlDay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 6\\01.xml";
        String voorbeeldxml = "C:\\School\\TDIMCOapp\\voorbeeld.xml";

        XmlIterator xmlIterator = new XmlIterator();
        iterateXml(xmlIterator, xmlMonthMay, true);

        SpanCollectionDao spanCollectionDao = new SpanCollectionDao();
        try {
            spanCollectionDao.WriteExcel(xmlIterator.getSpanCollection());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        spanCollectionDao.create(xmlIterator.getSpanCollection());

    }

    private static void iterateXml(XmlIterator xmlIterator, String xml, boolean iterateFolder) {
        runBothIterations(xml, xmlIterator, iterateFolder);
    }

    private static void runBothIterations(String xmlUrl, XmlIterator xmlIterator, boolean iterateFolder) {
        long startTime = System.currentTimeMillis();
        if (iterateFolder) {
            xmlIterator.iterateXmlFolder(xmlUrl, false);
            xmlIterator.iterateXmlFolder(xmlUrl, true);
        } else {
            xmlIterator.iterateXmlFile(xmlUrl, false);
            xmlIterator.iterateXmlFile(xmlUrl, true);
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for method \"runBothIterations\": " + (elapsedTime / 1000) / 60 + "min " + (elapsedTime / 1000) % 60 + "sec");
    }
}
