package com.example.travelplanner;

public class Alarm {
    private String tripName;
    private String startDate;
    private String finishDate;
    private String alarmTime;

    public Alarm(String tripName, String startDate, String finishDate, String alarmTime) {
        this.tripName = tripName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.alarmTime = alarmTime;
    }

    public String getTripName() { return tripName; }
    public String getStartDate() { return startDate; }
    public String getFinishDate() { return finishDate; }
    public String getAlarmTime() { return alarmTime; }
}
