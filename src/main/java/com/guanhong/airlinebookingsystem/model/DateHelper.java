package com.guanhong.airlinebookingsystem.model;

import java.sql.Date;
import java.util.Calendar;

public class DateHelper {

    public DateHelper() {
    }

    public Date tomorrow() {
        return datePlusSomeDays(today(),1);
    }

    public Date today(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return new Date(calendar.getTime().getTime());
    }

    public Date datePlusSomeDays(Date date, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return new Date(calendar.getTime().getTime());
    }
}
