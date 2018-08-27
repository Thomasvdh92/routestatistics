package com.tdimco.routestatistics.excelwriters;

import com.tdimco.routestatistics.domain.Detection;
import com.tdimco.routestatistics.domain.Detector;
import com.tdimco.routestatistics.domain.Device;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DevicesToExcel {

    private List<Device> deviceList;

    public DevicesToExcel(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public void writeToExcel() {
        Workbook workbook = new XSSFWorkbook();

        CreationHelper creationHelper = workbook.getCreationHelper();

        String[] columns = {"Device","Date","Detections"};

        Sheet sheet = workbook.createSheet("Hour data collection");
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }


        int rowNum = 1;
        int extraColumns = 0;
        int amountOfColumns = columns.length;
        for(Device d : deviceList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(d.getDevId() + " - " + d.getVehicleType().toString());
            int j =2;

            LocalDate compareLD = d.getDetections().get(0).getLdt().toLocalDate();
            row.createCell(1).setCellValue(compareLD.toString());
            for(int i = 0;i<d.getDetections().size();i++) {

                LocalDate currentLD = d.getDetections().get(i).getLdt().toLocalDate();
                if(i!=0 && currentLD.isAfter(compareLD)) {
                    if(j > extraColumns) extraColumns = j;
                    compareLD = currentLD;
                    row.createCell(1).setCellValue(compareLD.toString());
                    row = sheet.createRow(rowNum++);
                    j = 2;
                }
                LocalDateTime ldt = d.getDetections().get(i).getLdt();
                Detector detector = d.getDetections().get(i).getDetector();
                row.createCell(j).setCellValue(ldt.toLocalTime().toString() + " - " + detector.getDetectorId());
                j++;


            }
        }


        amountOfColumns += extraColumns;
        for (int i = 0; i < amountOfColumns; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            FileOutputStream fileOut = new FileOutputStream("F:\\Generated Excel Files" + "\\" + "DeviceDetections" + ".xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
