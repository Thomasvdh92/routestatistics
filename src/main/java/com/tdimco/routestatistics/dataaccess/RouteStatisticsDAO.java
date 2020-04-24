package com.tdimco.routestatistics.dataaccess;

import com.tdimco.routestatistics.domain.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class RouteStatisticsDAO implements IDAO<RouteStatistics> {

    @Override
    public RouteStatistics create(RouteStatistics routeStatistics) {
        return null;
    }

    @Override
    public RouteStatistics read(RouteStatistics routeStatistics) {
        try {
            Connection conn = MySQLJDBCUtil.getConnection();
            String query = "SELECT * FROM RouteStatistics WHERE Weekday = ? AND HourNumber = ? AND Route = ?";
            PreparedStatement prpdstmt = conn.prepareStatement(query);

            String weekDay = routeStatistics.getWeekDay().toString();
            int hourNumber = routeStatistics.getHour().getHourNumber();
            String route = routeStatistics.getRoute().toString();

            prpdstmt.setString(1, weekDay);
            prpdstmt.setInt(2, hourNumber);
            prpdstmt.setString(3, route);

//            System.out.println(prpdstmt);
            ResultSet rs = prpdstmt.executeQuery();
            RouteStatistics result = null;
            while (rs.next()) {
                result = new RouteStatistics();
                result.setWeekDay(new WeekDay(DayOfWeek.valueOf(rs.getString(1))));
                result.setHour(new Hour(rs.getInt(2)));
                Route r = Route.GetRouteFromString(rs.getString(3));
                result.setRoute(r);
                DayRouteData drd = new DayRouteData();
                drd.setTotalHits(rs.getInt(4));
                drd.setMinimumTime(rs.getInt(5));
                drd.setMaximumTime(rs.getInt(6));
                drd.setSum(rs.getInt(7));
                drd.setSumSquared(rs.getLong(8));
                drd.setSecondTotalHits(rs.getInt(9));
                drd.setSecondSum(rs.getInt(10));
                drd.setSecondSquared(rs.getLong(11));
                drd.setSecondMaxTime(rs.getInt(12));
                result.getHour().addDrd(r, drd);
            }
            conn.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RouteStatistics update(RouteStatistics routeStatistics) {
        return null;
    }

    @Override
    public void delete(RouteStatistics routeStatistics) {

    }

    @Override
    public List<RouteStatistics> getAll() {
        return null;
    }

    public ArrayList<RouteStatistics> getAmount(int amount) {
        ArrayList<RouteStatistics> returnList = new ArrayList<>();
        try {
            Connection conn = MySQLJDBCUtil.getConnection();
            String query = "SELECT * FROM RouteStatistics limit ?";
            PreparedStatement prpdstmt = conn.prepareStatement(query);

            prpdstmt.setInt(1, amount);
            ResultSet rs = prpdstmt.executeQuery();
            while (rs.next()) {
                RouteStatistics result = new RouteStatistics();
                result.setWeekDay(new WeekDay(DayOfWeek.valueOf(rs.getString(1))));
                result.setHour(new Hour(rs.getInt(2)));
                Route r = Route.GetRouteFromString(rs.getString(3));
                result.setRoute(r);
                DayRouteData drd = new DayRouteData();
                drd.setTotalHits(rs.getInt(4));
                drd.setMinimumTime(rs.getInt(5));
                drd.setMaximumTime(rs.getInt(6));
                drd.setSum(rs.getInt(7));
                drd.setSumSquared(rs.getLong(8));
                drd.setSecondTotalHits(rs.getInt(9));
                drd.setSecondSum(rs.getInt(10));
                drd.setSecondSquared(rs.getLong(11));
                drd.setSecondMaxTime(rs.getInt(12));
                result.getHour().addDrd(r, drd);
                returnList.add(result);
            }
            conn.close();
            return returnList;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
