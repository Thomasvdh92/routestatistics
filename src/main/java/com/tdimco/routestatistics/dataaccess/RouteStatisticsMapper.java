package com.tdimco.routestatistics.dataaccess;

import com.tdimco.routestatistics.domain.RouteStatistics;

import java.util.ArrayList;
import java.util.List;

public class RouteStatisticsMapper implements IMapper<RouteStatistics> {

    public static List<RouteStatistics> routeStatisticsList = new ArrayList<>();
    private RouteStatisticsDAO routeStatisticsDAO = new RouteStatisticsDAO();

    @Override
    public RouteStatistics find(RouteStatistics routeStatistics) {
        int index = routeStatisticsList.indexOf(routeStatistics);
        if(index != -1) {
            System.out.println(index);
            return routeStatisticsList.get(index);
        } else {
            RouteStatistics r = new RouteStatisticsDAO().read(routeStatistics);
            routeStatisticsList.add(r);
            return r;
        }
    }
}
