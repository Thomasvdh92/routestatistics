package com.tdimco.routestatistics.domain;

import lombok.Data;

/**
 * Created by Thomas on 18-3-2018.
 */
@Data
public class Device {

    private String devId;

    private VehicleType vehicleType;

    public Device(String devId) {
        this.devId =devId;
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
