package com.tdimco.routestatistics.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 18-3-2018.
 */
@Data
public class Device {

    private String devId;

    private VehicleType vehicleType;

    private List<Detection> detections;

    public Device(String devId) {
        this.devId =devId;
        detections = new ArrayList<>();
    }

    public Device(String devId, String vehicleType) {
        this.devId = devId;
        switch (vehicleType) {
            case "C":
                this.vehicleType = VehicleType.C;
                break;
            case "U":
                this.vehicleType = VehicleType.U;
                break;
            case "T":
                this.vehicleType = VehicleType.T;
                break;
        }
    }

    public void addDetections(Detection detection) {
        if(detections == null) {
            detections = new ArrayList<>();
            detections.add(detection);
        } else if(!detections.get(detections.size()-1).getLdt().isAfter(detection.getLdt())) {
            detections.add(detection);
        }
    }

    public String printDetections() {
        StringBuilder r = new StringBuilder("Device{" +
                "devId='" + devId + '\'' +
                ", vehicleType=" + vehicleType +
                '}');
        for(int i =0;i<detections.size();i++) {
            r.append(detections.get(i).toString());
        }
        return r.toString();
    }

    @Override
    public String toString() {
        return "Device{" +
                "devId='" + devId + '\'' +
                ", vehicleType=" + vehicleType +
                '}';
    }

    public String getDevId() {
        return devId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return devId.equals(device.devId);
    }

    @Override
    public int hashCode() {
        return devId.hashCode();
    }
}
