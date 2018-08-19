package com.tdimco.routestatistics.domain;

import com.tdimco.routestatistics.dataaccess.RouteStatisticsDAO;
import lombok.Getter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpanCollection {

    @Getter
    private List<WeekDay> weekDays;
    @Getter
    private List<DeviceRoutes> deviceRoutesCollection;

    public SpanCollection() {
        buildWeekDays();
        deviceRoutesCollection = new ArrayList<>();
    }

    private void buildWeekDays() {
        weekDays = new ArrayList<>();
        for(DayOfWeek d : DayOfWeek.values()) {
            weekDays.add(new WeekDay(d));
        }
    }

    public void addRouteDetection(Element detection1, Element detection2, boolean secondIteration) {
        Detector d1 = new Detector(Integer.parseInt(detection1.getAttribute("d")));
        Detector d2 = new Detector(Integer.parseInt(detection2.getAttribute("d")));
        Route r = new Route(d1, d2);
        LocalDateTime date1 = getDateFromDetection(detection1);
        LocalDateTime date2 = getDateFromDetection(detection2);
        double seconds = date1.until(date2, ChronoUnit.SECONDS);
        getHourAndAddTime(secondIteration, r, seconds, date1);
    }

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

    public void compileRoutes(Device device, NodeList detectionNodeList) {

        int startDetec = 0;
        List<Detector> detectors = new ArrayList<>();
        detectors.add(getDetectorFromNodeList(detectionNodeList, startDetec));
        for(int currentDetec=1;currentDetec<detectionNodeList.getLength();currentDetec++) {
            if(!getDetectorFromNodeList(detectionNodeList, startDetec).equals(getDetectorFromNodeList(detectionNodeList, currentDetec)) &&
                    !getDetectorFromNodeList(detectionNodeList, currentDetec-1).equals(getDetectorFromNodeList(detectionNodeList, currentDetec))) {

                if (isViableRouteTime(detectionNodeList, startDetec, currentDetec)) {
                    detectors.add(getDetectorFromNodeList(detectionNodeList, currentDetec));
                    int detectionsize = detectionNodeList.getLength();
                    if(currentDetec == detectionNodeList.getLength()-1 && detectors.size() > 1) {
                        int endDetec = currentDetec - 1;
                        double seconds = getDateFromNodeList(detectionNodeList, startDetec).until(getDateFromNodeList(detectionNodeList, endDetec), ChronoUnit.SECONDS);
                        if(TimeUnit.SECONDS.toHours((long)seconds) < 5) continue;
                        passOnListToDeviceRoute(getDateFromNodeList(detectionNodeList, startDetec), device, detectors, seconds);
                    }
                } else if(detectors.size() > 1) {
                    int endDetec = currentDetec - 1;
                    double seconds = getDateFromNodeList(detectionNodeList, startDetec).until(getDateFromNodeList(detectionNodeList, endDetec), ChronoUnit.SECONDS);
                    if(TimeUnit.SECONDS.toHours((long)seconds) > 5) continue;
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
        RouteStatistics result = new RouteStatisticsDAO().read(routeStatistics);
        DayRouteData drd = result.getHour().getHourCollection().get(r);
        if (drd.getTotalHits() ==1 || drd.getMaximumTime() == 0) return false;
        return seconds < drd.getMaximumTime();
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
