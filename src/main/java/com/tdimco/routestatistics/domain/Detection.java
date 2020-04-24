package com.tdimco.routestatistics.domain;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class Detection {

    private LocalDateTime ldt;
    private Detector detector;

    public Detection(LocalDateTime ldt, Detector detector) {
        this.ldt = ldt;
        this.detector = detector;
    }

    @Override
    public String toString() {
        return "Date: " + ldt.toString() + " - at detector: " + detector.toString();
    }

}
