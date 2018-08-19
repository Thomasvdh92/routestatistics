package com.tdimco.routestatistics.dataaccess;

import com.tdimco.routestatistics.domain.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SpanCollectionDao implements IDAO<SpanCollection> {

    @Override
    public SpanCollection create(SpanCollection spanCollection) {
        try {
            Connection conn = MySQLJDBCUtil.getConnection();
            String query = "INSERT INTO RouteStatistics(Weekday, HourNumber, Route, TotalHits, MinimumTime, MaximumTime, TotalSum, " +
                    "SumSquared, SecondTotalHits, SecondSum, SecondSquared, SecondMaxTime) " +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement prpdstmt = conn.prepareStatement(query);
            for (WeekDay weekDay : spanCollection.getWeekDays()) {
                for (Hour hour : weekDay.getHours()) {
                    for (Route r : hour.getHourCollection().keySet()) {
                        DayRouteData drd = hour.getHourCollection().get(r);
                        if (drd.getTotalHits() <= 1) continue;
                        prpdstmt.setString(1, weekDay.getDayOfWeek().toString());
                        prpdstmt.setInt(2, hour.getHourNumber());
                        prpdstmt.setString(3, r.toString());
                        prpdstmt.setInt(4, drd.getTotalHits());
                        prpdstmt.setDouble(5, drd.getMinimumTime());
                        prpdstmt.setDouble(6, drd.getMaximumTime());
                        prpdstmt.setDouble(7, drd.getSum());
                        prpdstmt.setDouble(8, drd.getSumSquared());
                        prpdstmt.setInt(9, drd.getSecondTotalHits());
                        prpdstmt.setDouble(10, drd.getSecondSum());
                        prpdstmt.setDouble(11, drd.getSecondSquared());
                        prpdstmt.setDouble(12, drd.getSecondMaxTime());
                        prpdstmt.execute();
                    }
                }
            }
            conn.close();
        } catch (SQLException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public SpanCollection read(SpanCollection spanCollection) {
        return null;
    }

    @Override
    public SpanCollection update(SpanCollection spanCollection) {
        return null;
    }

    @Override
    public void delete(SpanCollection spanCollection) {

    }

    @Override
    public List<SpanCollection> getAll() {
        return null;
    }
}
