package com.tdimco.routestatistics.converters;

import java.util.concurrent.TimeUnit;

public class MillisConverter {

    public static String convertMillis(long seconds) {

        if(seconds < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.SECONDS.toDays(seconds);
        seconds -= TimeUnit.DAYS.toSeconds(days);
        long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        seconds -= TimeUnit.MINUTES.toSeconds(minutes);

        StringBuilder sb = new StringBuilder(64);
        if(days != 0) {
            sb.append(days);
            sb.append(" Days ");
        }
        if(hours != 0) {
            sb.append(hours);
            sb.append(" Hours ");
        }
        if(minutes != 0) {
            sb.append(minutes);
            sb.append(" Min ");
        }
        sb.append(seconds);
        sb.append(" Sec");

        return(sb.toString());
    }

}
