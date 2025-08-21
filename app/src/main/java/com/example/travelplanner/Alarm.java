package com.example.travelplanner;

public class Alarm {
    private String tripName;
    private String startDate;
    private String finishDate;
    private String alarmTime;
    private String alarmTitle;

    public Alarm(String tripName, String startDate, String finishDate, String alarmTime, String alarmTitle) {
        this.tripName = tripName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.alarmTime = alarmTime;
        this.alarmTitle = alarmTitle;
    }

    public String getTripName()   { return tripName; }
    public String getStartDate()  { return startDate; }
    public String getFinishDate() { return finishDate; }
    public String getAlarmTime()  { return alarmTime; }
    public String getAlarmTitle() { return alarmTitle; }
}