package com.tdimco.routestatistics;

import com.tdimco.routestatistics.dataaccess.RouteStatisticsDAO;
import com.tdimco.routestatistics.dataaccess.RouteStatisticsMapper;
import com.tdimco.routestatistics.domain.RouteStatistics;

import java.util.ArrayList;

public class testapp {

    public static void main(String[] args) {

        ArrayList<RouteStatistics> list = new RouteStatisticsDAO().getAmount(100);

        ArrayList<RouteStatistics> newList = new ArrayList<>();

        for(int i=0;i<list.size();i++) {
            newList.add(new RouteStatisticsMapper().find(list.get(i)));
        }

        for(int i=0;i<list.size();i++) {
            System.out.println(new RouteStatisticsMapper().find(list.get(i)));
        }



    }
}
