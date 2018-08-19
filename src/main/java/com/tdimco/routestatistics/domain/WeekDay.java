package com.tdimco.routestatistics.domain;

import lombok.Data;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 27-3-2018.
 */
@Data
public class WeekDay {

    private DayOfWeek dayOfWeek;

    private List<Hour> hours;

    public WeekDay(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        buildHours();
    }

    private void buildHours() {
        hours = new ArrayList<>();
        for(int i=0;i<24;i++) {
            Hour hour = new Hour(i);
            if(hour == null) System.out.println("ja");
            hours.add(hour);
        }
    }

    public String toString() {
        return dayOfWeek.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekDay wd = (WeekDay) o;
        return this.dayOfWeek.equals(wd.dayOfWeek);
    }

}
