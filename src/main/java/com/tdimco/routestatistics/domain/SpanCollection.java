package com.tdimco.routestatistics.domain;

import com.tdimco.routestatistics.dataaccess.RouteStatisticsDAO;
import com.tdimco.routestatistics.dataaccess.RouteStatisticsMapper;
import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
public class SpanCollection {

    // A list containing all the weekdays
    private List<WeekDay> weekDays;

    private List<DeviceRoutes> deviceRoutesCollection;
    private List<Device> devices;

    public SpanCollection() {
        buildWeekDays();
        deviceRoutesCollection = new ArrayList<>();
        devices = new ArrayList<>();
    }

    // Build a list of weekdays using a custom domain class and java's DayOfWeek library
    private void buildWeekDays() {
        weekDays = new ArrayList<>();
        for(DayOfWeek d : DayOfWeek.values()) {
            weekDays.add(new WeekDay(d));
        }
    }

    /**
     * Generates a route and a time using the detections passed to this method.
     * @param detection1
     * @param detection2
     * @param secondIteration
     */
    public void addRouteDetection(Element detection1, Element detection2, boolean secondIteration) {
        Detector d1 = new Detector(Integer.parseInt(detection1.getAttribute("d")));
        Detector d2 = new Detector(Integer.parseInt(detection2.getAttribute("d")));
        Route r = new Route(d1, d2);
        LocalDateTime date1 = getDateFromDetection(detection1);
        LocalDateTime date2 = getDateFromDetection(detection2);
        double seconds = date1.until(date2, ChronoUnit.SECONDS);
        if(seconds < 7200) {
            getHourAndAddTime(secondIteration, r, seconds, date1);
        }
    }

    /**
     * Gets the weekday and hour from the date and adds the time to the that hour of that weekday for that route
     * @param secondIteration
     * @param r
     * @param seconds
     * @param date
     */
    private void getHourAndAddTime(boolean secondIteration, Route r, double seconds, LocalDateTime date) {
        WeekDay wd = new WeekDay(date.getDayOfWeek());
        int hour =  date.getHour();
        int indexOfWeekday = weekDays.indexOf(wd);
        weekDays.get(indexOfWeekday).getHours().get(hour).addTimeToDrd(r,seconds,secondIteration);
    }

    public DayRouteData getDrd(Route r, LocalDateTime date) {
        WeekDay wd = new WeekDay(date.getDayOfWeek());
        int hour =  date.getHour();
        int indexOfWeekday = weekDays.indexOf(wd);
        return weekDays.get(indexOfWeekday).getHours().get(hour).getHourCollection().get(r);
    }

    public LocalDateTime getDateFromDetection(Element detection1) {
        String s = detection1.getAttribute("t");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy HH:mm:ss");
        return LocalDateTime.parse(s, formatter);
    }

    public void determineMaximumTimeForDrd(boolean secondIteration) {
        for(WeekDay wd:weekDays) {
            for (Hour h : wd.getHours()) {
                h.setTotals();
                for (DayRouteData drd : h.getHourCollection().values()) {
                    if(secondIteration) {
                        drd.setSecondMaxTime(drd.calculateMaximumTime(drd.getSecondTotalHits(), drd.getSecondSum(), drd.getSecondSquared()));
                        continue;
                    }
                    drd.setMaximumTime(drd.calculateMaximumTime(drd.getTotalHits(), drd.getSum(), drd.getSumSquared()));
                }
            }
        }
    }

    public void gatherDetections(Device device, NodeList detectionsNodeList) {
        if(!devices.contains(device)){
            devices.add(device);
        }
        int index = devices.indexOf(device);
        for(int i=0;i<detectionsNodeList.getLength();i++){
            LocalDateTime ldt = getDateFromNodeList(detectionsNodeList, i);
            Detector d = getDetectorFromNodeList(detectionsNodeList, i);
            Detection detection = new Detection(ldt, d);
            devices.get(index).addDetections(detection);
        }
    }

    public void compileRoutes(Device device, NodeList detectionNodeList) {
        if(detectionNodeList.getLength() <= 1) return;

        int startDetec = 0;
        List<Detector> detectors = new ArrayList<>();
        detectors.add(getDetectorFromNodeList(detectionNodeList, startDetec));
        for(int currentDetec=1;currentDetec<detectionNodeList.getLength();currentDetec++) {
            // If detec 0 and 1 are equal, dont make a route
            if(!getDetectorFromNodeList(detectionNodeList, startDetec).equals(getDetectorFromNodeList(detectionNodeList, currentDetec)) &&
                    // If detector currently evaluated and previous detector are the same, dont make a route
                    !getDetectorFromNodeList(detectionNodeList, currentDetec-1).equals(getDetectorFromNodeList(detectionNodeList, currentDetec))) {

                if (isViableRouteTime(detectionNodeList, startDetec, currentDetec)) {
                    detectors.add(getDetectorFromNodeList(detectionNodeList, currentDetec));
                    if(currentDetec == detectionNodeList.getLength()-1 && detectors.size() > 1) {
                        int endDetec = currentDetec - 1;
                        double seconds = getDateFromNodeList(detectionNodeList, startDetec).until(getDateFromNodeList(detectionNodeList, endDetec), ChronoUnit.SECONDS);
                        if(seconds > 7200) continue;
                        passOnListToDeviceRoute(getDateFromNodeList(detectionNodeList, startDetec), device, detectors, seconds);
                    }
                } else if(detectors.size() > 1) {
                    int endDetec = currentDetec - 1;
                    double seconds = getDateFromNodeList(detectionNodeList, startDetec).until(getDateFromNodeList(detectionNodeList, endDetec), ChronoUnit.SECONDS);
                    if(seconds > 7200) continue;
                    passOnListToDeviceRoute(getDateFromNodeList(detectionNodeList, startDetec), device, detectors, seconds);
                    detectors = new ArrayList<>();
                    startDetec = currentDetec;
                    detectors.add(getDetectorFromNodeList(detectionNodeList, startDetec));
                } else {
                    detectors = new ArrayList<>();
                    startDetec = currentDetec;
                    detectors.add(getDetectorFromNodeList(detectionNodeList, startDetec));
                }
            }
        }
    }

    private void passOnListToDeviceRoute(LocalDateTime ldt, Device device, List<Detector> detectors, double seconds) {
        DeviceRoutes deviceRoutes = new DeviceRoutes(device);

        if(seconds <= 5) return;
        if(!deviceRoutesCollection.contains(deviceRoutes)) {
            deviceRoutesCollection.add(deviceRoutes);
        }
        int index = deviceRoutesCollection.indexOf(deviceRoutes);
        deviceRoutesCollection.get(index).addRoute(ldt, detectors, seconds);
    }

    private boolean isViableRouteTime(NodeList detectionNodeList, int detectorOneIndex, int detectorTwoIndex) {
        Element detection1 = (Element) detectionNodeList.item(detectorOneIndex);
        Element detection2 = (Element) detectionNodeList.item(detectorTwoIndex);
        LocalDateTime date1 = getDateFromDetection(detection1);
        LocalDateTime date2 = getDateFromDetection(detection2);
        double seconds = date1.until(date2, ChronoUnit.SECONDS);
        Detector d1= getDetectorFromNodeList(detectionNodeList, detectorOneIndex);
        Detector d2 = getDetectorFromNodeList(detectionNodeList, detectorTwoIndex);
        if(d1.equals(d2)) return false;
        DayOfWeek dayOfWeek = date1.getDayOfWeek();
        int hourNumber = date1.getHour();
        Hour hour = new Hour(hourNumber);
        Route r = new Route(d1, d2);
        RouteStatistics routeStatistics = new RouteStatistics(new WeekDay(dayOfWeek), hour, r);
        RouteStatistics result = new RouteStatisticsMapper().find(routeStatistics);
        DayRouteData drd;
        try {
            drd = result.getHour().getHourCollection().get(r);
            if (drd.getSecondTotalHits() ==1 || drd.getSecondMaxTime() == 0) return false;
            return seconds < drd.getSecondMaxTime();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private Detector getDetectorFromNodeList(NodeList detectionNodeList, int i) {
        Node node = detectionNodeList.item(i);
        Element element = (Element) node;
        return new Detector(Integer.parseInt(element.getAttribute("d")));
    }

    private LocalDateTime getDateFromNodeList(NodeList detectionNodeList, int index) {
        Node node = detectionNodeList.item(index);
        Element element = (Element) node;
        return getDateFromDetection(element);
    }

}
