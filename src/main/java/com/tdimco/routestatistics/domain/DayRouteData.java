package com.tdimco.routestatistics.domain;

import lombok.Data;
import lombok.Getter;

/**
 * Collection of data. This class is also responsible for the regulation the iterations.
 */

@Data
public class DayRouteData {

    /**
     * Two static variables used to compare the first maximum time to the second maximum time.
     * If the second time does not diverge more than 10% from the first maximum time, the second static is
     * incremented. The first static is always incremented when an object is made.
     */
    @Getter
    private static int amountOfDrdObjects = 0;
    @Getter
    private static int amountNotDeviatingPastTenPrcnt = 0;

    private int totalHits;
    private double minimumTime;
    private double maximumTime;
    private double sum;
    private double sumSquared;
    public int secondTotalHits;
    public double secondSum;
    public double secondSquared;
    public double secondMaxTime;

    public DayRouteData() {
        this.totalHits= 0;
        this.minimumTime= 0;
        this.maximumTime = 0;
        this.sum = 0;
        this.sumSquared = 0;
        this.secondTotalHits=0;
        this.secondSum=0;
        this.secondSquared=0;
        this.secondMaxTime = 0;
        amountOfDrdObjects++;
    }

    public void setDrdValuesToFirst() {
        totalHits = secondTotalHits;
        secondTotalHits = 0;
        maximumTime = secondMaxTime;
        secondMaxTime = 0;
        sum = secondSum;
        secondSum = 0;
        sumSquared = secondSquared;
        secondSquared = 0;
        amountNotDeviatingPastTenPrcnt = 0;
    }

    /**
     * Method used to increment a class static
     */
    public static void incrementAmountDeviation() {
        amountNotDeviatingPastTenPrcnt++;
    }

    /**
     * Method to add collected data to the collection.
     * @param seconds Amount of time(in seconds) to be added
     * @param secondIteration Used to determine whether its the first or second iteration
     */
    public void addTimeToDayRouteData(double seconds, boolean secondIteration) {
        if(secondIteration) {
            if(seconds > maximumTime) return;
            secondTotalHits++;
            secondSum+= seconds;
            secondSquared += (Math.pow(seconds,2));
            return;
        }
        totalHits++;
        sum += seconds;
        sumSquared += Math.pow(seconds, 2);
        if(minimumTime == 0.0 || minimumTime > seconds) {
            minimumTime = seconds;
        }

    }

    public double calculateMaximumTime(int totalHits, double sum, double sumSquared) {
        if(totalHits<=1) {
            //maybe throw error
            return 0;
        }

        double averageSquared = Math.pow(sum/totalHits, 2);
        double sumTimesAverageSquared = totalHits * averageSquared;
        double sumSquaredMinusSumTimesAverageSquared = sumSquared - sumTimesAverageSquared;
        double postFinalNumber = sumSquaredMinusSumTimesAverageSquared/(totalHits - 1);
        double standardDevation =Math.sqrt(postFinalNumber);
        return ((sum/totalHits) + standardDevation * 2);
    }

    @Override
    public String toString() {
        return "Totalhits: "+ totalHits + " minimumtime: " +minimumTime + " total sum: "+ sum + " maximum time: "+ maximumTime + " sumsquared:"+sumSquared + "\n"
               + "2ndtotalhits" + secondTotalHits;
    }

}
