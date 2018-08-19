package com.tdimco.routestatistics;

import com.tdimco.routestatistics.dataaccess.SpanCollectionDao;
import com.tdimco.routestatistics.datareading.XmlIterator;
import com.tdimco.routestatistics.domain.Hour;
import com.tdimco.routestatistics.domain.Route;
import com.tdimco.routestatistics.domain.RouteStatistics;
import com.tdimco.routestatistics.domain.WeekDay;

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

//        for(WeekDay wd : xmlIterator.getSpanCollection().getWeekDays()) {
//            if(wd.getDayOfWeek().toString().equals("SATURDAY")) {
//                for(Hour h : wd.getHours()) {
//                    for(Route r : h.getHourCollection().keySet()) {
//                        if(r.toString().startsWith("532") && r.toString().endsWith("1046")) System.out.println(h.getHourCollection().get(r).toString());
//                    }
//                }
//            }
//        }

        SpanCollectionDao spanCollectionDao = new SpanCollectionDao();
        spanCollectionDao.create(xmlIterator.getSpanCollection());

    }

    private static void iterateXml(XmlIterator xmlIterator, String xml, boolean iterateFolder) {
        runBothIterations(xml, xmlIterator, iterateFolder);
    }

    private static void runBothIterations(String xmlUrl, XmlIterator xmlIterator, boolean iterateFolder) {
        long startTime = System.currentTimeMillis();
        if (iterateFolder) {
            xmlIterator.iterateXmlFolder(xmlUrl, false);
            xmlIterator.getSpanCollection().determineMaximumTimeForDrd(false);
            xmlIterator.iterateXmlFolder(xmlUrl, true);
            xmlIterator.getSpanCollection().determineMaximumTimeForDrd(true);
        } else {
            xmlIterator.iterateXmlFile(xmlUrl, false);
            xmlIterator.iterateXmlFile(xmlUrl, true);
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for method \"runBothIterations\": " + (elapsedTime / 1000) / 60 + "min " + (elapsedTime / 1000) % 60 + "sec");
    }
}
