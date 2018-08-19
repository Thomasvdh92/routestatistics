package com.tdimco.routestatistics.domain;

import com.tdimco.routestatistics.converters.MillisConverter;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO Deze data in een database krijgen
@Data
public class DeviceRoutes {

    private Device device;

    private List<DeviceRoute> routes;

    public DeviceRoutes(Device device) {
        this.device = device;
    }

    public void addRoute(LocalDateTime ldt, List<Detector> detectors, double routeTimeInMillis) {
        if(routes == null) {
            routes = new ArrayList<>();
        }
        DeviceRoute dr = new DeviceRoute(ldt, detectors, routeTimeInMillis);
        routes.add(dr);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o instanceof DeviceRoutes) {
            DeviceRoutes d = (DeviceRoutes) o;
            return this.device == d.device;
        }
        return false;
    }

    @Override
    public String toString() {
        String s = device.toString();
        for(DeviceRoute dr : routes) {
            s += dr.toString() + " ";
        }
        return s;
    }

    @Data
    public static class DeviceRoute {
        //TODO defaq waarom werkt het met een int array wel dfsgsfasdfsfgh...
        /** TODO
         * Moment van binnenkomen - Weekdag, tijdstip en detector
         * Moment van verlaten - Weekdag, tijdstip en detector
         */
        private LocalDateTime ldt;
        @Getter
        private List<Detector> detectors;
        private double routeTimeInMillis;

        public DeviceRoute(LocalDateTime ldt, List<Detector> detectors, double routeTimeInMillis) {
            this.ldt = ldt;
            this.detectors = detectors;
            this.routeTimeInMillis = routeTimeInMillis;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof DeviceRoute) {
                DeviceRoute d = (DeviceRoute) o;
                return this.ldt == d.ldt && this.detectors == d.detectors;
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append(ldt).append(" Detectors --> ");
            for(Detector d : detectors) {
                s.append(d).append(" ");
            }
            s.append(" route time: ").append(MillisConverter.convertMillis((long) routeTimeInMillis));
            return s.toString();
        }
    }
}
