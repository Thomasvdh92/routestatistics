package com.tdimco.routestatistics;

import com.tdimco.routestatistics.deviceroutescreation.XmlIteratorForDevices;
import com.tdimco.routestatistics.domain.*;

public class AppDeviceRouteCreater {

    public static void main(String[] args) {
        String xmlDay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 5\\05.xml";
        String xmlMonthMay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 5";

        XmlIteratorForDevices iterator = new XmlIteratorForDevices();
        iterator.getDeviceFromXml(xmlDay);
        iterator.iterateXmlFolder(xmlMonthMay);
        for(DeviceRoutes dr : iterator.getSpanCollection().getDeviceRoutesCollection()) {
            System.out.println(dr.toString());
        }
    }
}
