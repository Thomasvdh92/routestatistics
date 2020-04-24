package com.tdimco.routestatistics.dataaccess;

import com.tdimco.routestatistics.domain.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Method to write spancollection data to a excel file
     * @param spanCollection
     * @throws IOException
     */
    public void WriteExcel(SpanCollection spanCollection) throws IOException {
        long startTime = System.currentTimeMillis();
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (int i = 0; i < spanCollection.getWeekDays().size(); i++) {
            int rowCount = -1;
            XSSFSheet sheet = workbook.createSheet(spanCollection.getWeekDays().get(i).toString());
            Row row = sheet.createRow(++rowCount);
            int columnCount = -1;
            String[] headers = {"Hour", "Total hits", "Total sum in hours", "Total sum squared in hours", "Total average in minutes"};
            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.createCell(++columnCount);
                cell.setCellValue(headers[j]);
                sheet.autoSizeColumn(j);
            }
            XSSFWorkbook dayRouteDataWorkbook = new XSSFWorkbook();
            for (int j = 0; j < spanCollection.getWeekDays().get(i).getHours().size(); j++) {
                columnCount = -1;
                Hour hourData = spanCollection.getWeekDays().get(i).getHours().get(j);
                row = sheet.createRow(++rowCount);
                Cell cell = row.createCell(++columnCount);
                cell.setCellValue(hourData.getHourNumber());
                if (hourData.getHourTotalHits() > 0) {
                    cell = row.createCell(++columnCount);
                    cell.setCellValue(hourData.getHourTotalHits());
                    cell = row.createCell(++columnCount);
                    cell.setCellValue(hourData.getTotalSumInHours());
                    cell = row.createCell(++columnCount);
                    cell.setCellValue(hourData.getTotalSumSquaredInHours());
                    cell = row.createCell(++columnCount);
                    cell.setCellValue(hourData.getTotalAverageInMinutes());
                    int secondRowCount = -1;
                    XSSFSheet hourSheet = dayRouteDataWorkbook.createSheet(String.valueOf(hourData.getHourNumber()));
                    String[] dayRouteDataHeaders = {"Route", "Total hits", "Minimum time", "Maximum time", "Sum" ,"Sum squared", "Second total hits", "Second sum", "Second sum squared", "Second maximum time"};
                    row = hourSheet.createRow(++secondRowCount);
                    int secondColumnCount = -1;
                    for (int k = 0; k < dayRouteDataHeaders.length; k++) {
                        cell = row.createCell(++secondColumnCount);
                        cell.setCellValue(dayRouteDataHeaders[k]);
                        sheet.autoSizeColumn(k);
                    }
                    for (Map.Entry<Route, DayRouteData> entry: hourData.getHourCollection().entrySet()) {
                        row = hourSheet.createRow(++secondRowCount);
                        secondColumnCount = -1;
                        cell = row.createCell(++secondColumnCount);
                        cell.setCellValue(entry.getKey().toString());
                        cell = row.createCell(++secondColumnCount);
                        cell.setCellValue(entry.getValue().getTotalHits());
                        cell = row.createCell(++secondColumnCount);
                        cell.setCellValue(entry.getValue().getMinimumTime());
                        cell = row.createCell(++secondColumnCount);
                        cell.setCellValue(entry.getValue().getMaximumTime());
                        cell = row.createCell(++secondColumnCount);
                        cell.setCellValue(entry.getValue().getSum());
                        cell = row.createCell(++secondColumnCount);
                        cell.setCellValue(entry.getValue().getSumSquared());
                        if(entry.getValue().getSecondTotalHits() > 0) {
                            cell = row.createCell(++secondColumnCount);
                            cell.setCellValue(entry.getValue().getSecondTotalHits());
                            cell = row.createCell(++secondColumnCount);
                            cell.setCellValue(entry.getValue().getSecondSum());
                            cell = row.createCell(++secondColumnCount);
                            cell.setCellValue(entry.getValue().getSecondSquared());
                            cell = row.createCell(++secondColumnCount);
                            cell.setCellValue(entry.getValue().getSecondMaxTime());
                        }
                    }
                    String excelName = spanCollection.getWeekDays().get(i).toString().toLowerCase() + "-dayroutedata.xlsx";
                    try (FileOutputStream outputStream = new FileOutputStream(excelName)) {
                        dayRouteDataWorkbook.write(outputStream);
                    }
                }
            }
        }


        try (FileOutputStream outputStream = new FileOutputStream("weekdaydata.xlsx")) {
            workbook.write(outputStream);
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for method \"Write to excel\": " + (elapsedTime / 1000) / 60 + "min " + (elapsedTime / 1000) % 60 + "sec");
    }
}
