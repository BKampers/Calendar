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
            String type = calendar.getCalendarType();
            StringBuilder name = new StringBuilder();
            if (! type.isEmpty()) {
                name.append(Character.toUpperCase(type.charAt(0)));
                name.append(type.substring(1));
            }
            return name.toString();
        }
    }

    
    String yearText() {
        if (calendar instanceof bka.calendar.FrenchRepublicanCalendar) {
            bka.numeric.roman.Converter roman = new bka.numeric.roman.Converter();
            return roman.standard(calendar.get(Calendar.YEAR));
        }
        else if (calendar instanceof bka.calendar.EarthianCalendar) {
            return String.format("%04d", calendar.get(Calendar.YEAR));
        }
        else {
            return Integer.toString(calendar.get(Calendar.YEAR));
        }
    }

    
    String weekText() {
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        if (week <= 0) {
            return "";
        }
        if (calendar instanceof bka.calendar.FrenchRepublicanCalendar && week == calendar.getMaximum(Calendar.WEEK_OF_YEAR)) {
        if (bundle != null && bundle.containsKey("ComplementaryDays"))
            return bundle.getString(COMPLEMENTARY_DAYS_PROPERTY);
        }
        StringBuilder text = new StringBuilder();
        if (bundle != null && bundle.containsKey(WEEK_PROPERTY)) {
            text.append(bundle.getString(WEEK_PROPERTY));
        }
        else {
            text.append("Week");
        }
        text.append(' ');
        text.append(week);
        if (week == calendar.getMinimum(Calendar.WEEK_OF_YEAR) && calendar.get(Calendar.MONTH) == calendar.getMaximum(Calendar.MONTH)) {
            appendWeekYear(text, 1);
        }
        else if (week >= calendar.getMaximum(Calendar.WEEK_OF_YEAR) - 1 && calendar.get(Calendar.MONTH) == calendar.getMinimum(Calendar.MONTH)) {
            appendWeekYear(text, -1);
        }
        return text.toString();
    }


    private void appendWeekYear(StringBuilder text, int offset) {
        text.append(" (");
        text.append(calendar.get(Calendar.YEAR) + offset);
        text.append(')');
    }
    
    
    String monthText(Locale locale) {
        StringBuilder key = new StringBuilder(MONTH_PROPERTY);
        key.append(calendar.get(Calendar.MONTH));
        if (bundle != null && bundle.containsKey(key.toString())) {
            return bundle.getString(key.toString());
        }
        else {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMMM", locale);
            return format.format(calendar.getTime());
        }
    }
    
    
    String weekdayText(Locale locale) {
        String key = WEEKDAY_PROPERTY + Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);  
        }
        else {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("EEEE", locale);
            return format.format(calendar.getTime());
        }
    }


    String yearDayText() {
        String key = DAY_PROPERTY + Integer.toString(calendar.get(Calendar.DAY_OF_YEAR));
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        else {
            return "";
        }
    }
    
    
    String dayOfYearText() {
        return (Integer.toString(calendar.get(Calendar.DAY_OF_YEAR)) + " / " + Integer.toString(calendar.getActualMaximum(Calendar.DAY_OF_YEAR)));
    }
    
    
    private final Calendar calendar;
    private final ResourceBundle bundle;

    
    private static final String NAME_PROPERTY = "Name";
    private static final String WEEK_PROPERTY = "Week";
    private static final String COMPLEMENTARY_DAYS_PROPERTY = "ComplementaryDays";
    private static final String MONTH_PROPERTY = "Month";
    private static final String WEEKDAY_PROPERTY = "WeekDay";
    private static final String DAY_PROPERTY = "Day";

}
