package com.tdimco.routestatistics;

import com.tdimco.routestatistics.deviceroutescreation.XmlIteratorForDevices;
import com.tdimco.routestatistics.domain.*;
import com.tdimco.routestatistics.excelwriters.DevicesToExcel;

public class AppDeviceRouteCreater {

    public static void main(String[] args) {
        String xmlDay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 5\\05.xml";
        String xmlMonthMay = "F:\\Data Haven Bedrijf\\Originele Data\\2014 5";

        XmlIteratorForDevices iterator = new XmlIteratorForDevices();
        iterator.getDeviceFromXml(xmlDay);
        iterator.iterateXmlFolder(xmlMonthMay);
        for(Device d : iterator.getSpanCollection().getDevices()) {
            System.out.println(d.printDetections());
        }
        DevicesToExcel devicesToExcel = new DevicesToExcel(iterator.getSpanCollection().getDevices());
        devicesToExcel.writeToExcel();
//        for(DeviceRoutes dr : iterator.getSpanCollection().getDeviceRoutesCollection()) {
//            System.out.println(dr.toString());
//        }
    }
}
