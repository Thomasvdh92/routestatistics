package com.tdimco.routestatistics.domain;

import lombok.Data;

/**
 * Created by Thomas on 19-3-2018.
 */

@Data
public class Route implements Comparable<Route> {
    private Detector detectorOne;
    private Detector detectorTwo;

    public Route(Detector detectorOne, Detector detectorTwo) {
        this.detectorOne = detectorOne;
        this.detectorTwo = detectorTwo;
    }

    public String toString() {
        return detectorOne.toString() + " --> " + detectorTwo.toString();
    }

    public static Route GetRouteFromString(String route) {
        String[] splited = route.split("\\s+");
        return new Route(new Detector(Integer.valueOf(splited[0])), new Detector(Integer.valueOf(splited[2])));
    }

    @Override
    public int hashCode() {
        int result = detectorOne != null ? detectorOne.hashCode() : 0;
        result = 31 * result + (detectorTwo != null ? detectorTwo.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;
        if (detectorOne != null ? !detectorOne.equals(route.detectorOne) : route.detectorOne != null) return false;
        return detectorTwo != null ? detectorTwo.equals(route.detectorTwo) : route.detectorTwo == null;
    }

    @Override
    public int compareTo(Route o) {
        return this.detectorOne.compareTo(o.detectorOne);
    }
}
