/*
** Copyright © Bart Kampers
*/


package bka.calendar.swing;


import java.util.*;
import java.util.logging.*;

class Formatter {
    
    
    Formatter(Calendar calendar) {
        this.calendar = calendar;
        ResourceBundle calendarBundle = null;
        try {
            calendarBundle = ResourceBundle.getBundle(calendar.getClass().getName());
        }
        catch (java.util.MissingResourceException ex) {
            String className = Formatter.class.getName();
            Logger.getLogger(className).log(Level.FINER, className, ex);
        }
        bundle = calendarBundle;
    }
    
    
    String nameText() {
        if (bundle != null) {
            return bundle.getString(NAME_PROPERTY);
        }
        else {
            return "";
        }
    }

    
    String yearText() {
        if (calendar instanceof bka.calendar.FrenchRepublicanCalendar) {
            bka.numeric.roman.Converter roman = new bka.numeric.roman.Converter();
            return roman.standard(calendar.get(Calendar.YEAR));
        }
        else {
            return Integer.toString(calendar.get(Calendar.YEAR));
        }
    }

    
    String weekText() {
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        if (week > 0) {
            StringBuilder text = new StringBuilder();
            if (bundle != null && bundle.containsKey(WEEK_PROPERTY)) {
                text.append(bundle.getString(WEEK_PROPERTY));
            }
            else {
                text.append("Week");
            }
            text.append(' ');
            text.append(week);
            return text.toString();
        }
        else {
            return "";
        }
    }
    
    
    String monthText() {
        StringBuilder key = new StringBuilder(MONTH_PROPERTY);
        key.append(calendar.get(Calendar.MONTH));
        if (bundle != null && bundle.containsKey(key.toString())) {
            return bundle.getString(key.toString());
        }
        else {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMMM");
            return format.format(calendar.getTime());
        }
    }
    
    
    String weekdayText() {
        String key = WEEKDAY_PROPERTY + Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);  
        }
        else {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("EEEE");
            return format.format(calendar.getTime());
        }
    }


    String yearDayText() {
        if (bundle != null) {
            return bundle.getString(DAY_PROPERTY + Integer.toString(calendar.get(Calendar.DAY_OF_YEAR)));
        }
        else {
            return "";
        }
    }
    
  
    private final Calendar calendar;
    private final ResourceBundle bundle;

    
    private static final String NAME_PROPERTY = "Name";
    private static final String WEEK_PROPERTY = "Week";
    private static final String MONTH_PROPERTY = "Month";
    private static final String WEEKDAY_PROPERTY = "WeekDay";
    private static final String DAY_PROPERTY = "Day";

}
