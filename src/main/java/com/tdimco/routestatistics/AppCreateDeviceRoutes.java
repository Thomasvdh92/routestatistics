package com.tdimco.routestatistics;

import com.tdimco.routestatistics.datareading.XmlIterator;

public class AppCreateDeviceRoutes {

    public static void main(String[] args) {
        String xmlMonthMay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 5";
        String xmlDay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 5\\05.xml";
        String voorbeeldxml = "C:\\School\\TDIMCOapp\\voorbeeld.xml";

        XmlIterator xmlIterator = new XmlIterator();
        xmlIterator.compileRoutesFromDeviceNodeList(xmlDay);
        for(int i = 0;i<xmlIterator.getSpanCollection().getDeviceRoutesCollection().size();i++) {
            System.out.println(xmlIterator.getSpanCollection().getDeviceRoutesCollection().get(i).toString());
        }
    }
}
