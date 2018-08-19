package com.tdimco.routestatistics.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RouteStatistics {

    private WeekDay weekDay;
    private Hour hour;
    private Route route;

    public RouteStatistics(WeekDay weekDay, Hour hour, Route route) {
        this.weekDay = weekDay;
        this.hour = hour;
        this.route = route;
    }
}
