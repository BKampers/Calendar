/*
** Copyright © Bart Kampers
*/

package bka.calendar;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EarthianCalendar extends Calendar {
    
    
    public static final int ARIES = 0;
    public static final int TAURUS = 1;
    public static final int GEMINI = 2;
    public static final int CANCER = 3;
    public static final int LEO = 4;
    public static final int VIRGO = 5;
    public static final int LIBRA = 6;
    public static final int SCORPIUS = 7;
    public static final int SAGITTARIUS = 8;
    public static final int CAPRICORNUS = 9;
    public static final int AQUARIUS = 10;
    public static final int PISCES = 11;
    
    public static final int SOL = Calendar.SUNDAY;
    public static final int LUNA = Calendar.MONDAY;
    public static final int MARS = Calendar.TUESDAY;
    public static final int MERCURIUS = Calendar.WEDNESDAY;
    public static final int IUPPITER = Calendar.THURSDAY;
    public static final int VENUS = Calendar.FRIDAY;
    public static final int SATURNUS = Calendar.SATURDAY;
    

    @Override
    protected void computeTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected void computeFields() {
        long millisSinceEpoch = time - EPOCH;
        TimeZone timeZone = getTimeZone();
        if (timeZone != null) {
            Logger.getLogger(bka.calendar.EarthianCalendar.class.getSimpleName()).log(Level.FINE, timeZone.getDisplayName());
            fields[ZONE_OFFSET] = timeZone.getRawOffset();
            isSet[ZONE_OFFSET] = true;
            fields[DST_OFFSET] = timeZone.getDSTSavings();
            isSet[DST_OFFSET] = true;
            millisSinceEpoch += timeZone.getOffset(time);
        }
        long generationIndex = millisSinceEpoch / MILLIS_PER_GENERATION;
        if (millisSinceEpoch < 0 && millisSinceEpoch % MILLIS_PER_GENERATION != 0) {
            generationIndex--;
        }
        long generationStart = generationIndex * MILLIS_PER_GENERATION;
        int yearIndex = yearIndex(millisSinceEpoch - generationStart);
        long yearStart = generationStart + yearOffset(yearIndex);
        fields[ERA] = 0;
        isSet[ERA] = true;
        
        fields[YEAR] = (int) (generationIndex * YEARS_PER_GENERATION + yearIndex(millisSinceEpoch - generationStart));
        int bimesterIndex = (int) ((millisSinceEpoch - yearStart) / (DAYS_PER_BIMESTER * MILLIS_PER_DAY));
        int dayOfBimester = (int) ((millisSinceEpoch - yearStart) % (DAYS_PER_BIMESTER * MILLIS_PER_DAY) / MILLIS_PER_DAY);
        if (dayOfBimester < 30) {
            fields[MONTH] = bimesterIndex * 2;
            fields[DAY_OF_MONTH] = dayOfBimester + 1;
        }
        else {
            fields[MONTH] = bimesterIndex * 2 + 1;
            fields[DAY_OF_MONTH] = dayOfBimester - 29;            
        }
        fields[DAY_OF_YEAR] = (int) ((millisSinceEpoch - yearStart) / MILLIS_PER_DAY) + 1;
                
        fields[DAY_OF_WEEK] = dayOfWeek(millisSinceEpoch) + 1;
        
        fields[WEEK_OF_YEAR] = weekOfYear(millisSinceEpoch);
                
        computeTimeFields((int) (millisSinceEpoch % MILLIS_PER_DAY));
    }
        
    
    private int weekOfYear(long millisSinceEpoch) {
        long generationIndex = millisSinceEpoch / MILLIS_PER_GENERATION;
        if (millisSinceEpoch < 0 && millisSinceEpoch % MILLIS_PER_GENERATION != 0) {
            generationIndex--;
        }
        long generationStart = generationIndex * MILLIS_PER_GENERATION;
        int yearIndex = yearIndex(millisSinceEpoch - generationStart);
        long yearStart = generationStart + yearOffset(yearIndex);
        int year = (int) (generationIndex * YEARS_PER_GENERATION + yearIndex(millisSinceEpoch - generationStart));
        int yearStartDayOfWeek = dayOfWeek(yearStart);
        int dayOfYear = (int) ((millisSinceEpoch - yearStart) / MILLIS_PER_DAY) + 1;
        if (yearStartDayOfWeek < 4) {
            int shift = dayOfYear + yearStartDayOfWeek - 1;
            int weekOfYear = shift / DAYS_PER_WEEK + 1;
            if (weekOfYear == 53 && dayCount(year) - dayOfYear < 3 - dayOfWeek(millisSinceEpoch)) {
                weekOfYear = 1;
            }
            return weekOfYear;    
        }
        else if (dayOfYear + yearStartDayOfWeek > DAYS_PER_WEEK) {
            int shift = dayOfYear + yearStartDayOfWeek - 1;
            int weekOfYear = shift / DAYS_PER_WEEK;
            if (weekOfYear == 53 && dayCount(year) - dayOfYear < 3 - dayOfWeek(millisSinceEpoch)) {
                weekOfYear = 1;
            }
            return weekOfYear;    
        }
        else {
            return weekOfYear(millisSinceEpoch - yearStartDayOfWeek * MILLIS_PER_DAY);
        }
        
//        int shift = (yearStartDayOfWeek < 4) ? dayOfYear + yearStartDayOfWeek - 1 : dayOfYear - (DAYS_PER_WEEK - yearStartDayOfWeek);
//        if (shift < 0 || yearStartDayOfWeek <= 4 && shift == 0) {
//            return weekOfYear(millisSinceEpoch - yearStartDayOfWeek * MILLIS_PER_DAY);
//        }
//        else {
//            int weekOfYear = shift / DAYS_PER_WEEK + 1;
//            if (weekOfYear == 53 && dayCount(year) - dayOfYear < 3 - dayOfWeek(millisSinceEpoch)) {
//                weekOfYear = 1;
//            }
//            return weekOfYear;
//        }
    }
    
    
    private int dayCount(int year) {
        int generationIndex =  (int) (year % YEARS_PER_GENERATION);
        if (generationIndex < 0) {
            generationIndex += YEARS_PER_GENERATION;
        }
        return (generationIndex % 4 == 2) ? 366 : 365;
    }


    @Override
    public void add(int field, int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void roll(int field, boolean up) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public int getMinimum(int field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public int getMaximum(int field) {
        switch (field) {
            case MILLISECOND: return 999;
            case SECOND: return 59;
            case MINUTE: return 59;
            case HOUR: return 11;
            case DAY_OF_WEEK: return DAYS_PER_WEEK - 1;
            case MONTH: return 11;
            case YEAR: return Integer.MAX_VALUE;
            default: return 0;
        }
    }


    @Override
    public int getGreatestMinimum(int field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public int getLeastMaximum(int field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    private int yearIndex(long offset) {
        int i = 1;
        while (yearOffset(i) <= offset) {
            i++;
        }
        assert 1 <= i && i <= YEARS_PER_GENERATION; 
        return i - 1;
    }


    private long yearOffset(int yearIndex) {
        return yearIndex * MILLIS_PER_YEAR + ((yearIndex + 1) / 4) * MILLIS_PER_DAY;
    }
    
    
    private int dayOfWeek(long millisSinceEpoch) {
        int dayOfWeek = (int) ((millisSinceEpoch / MILLIS_PER_DAY + (MERCURIUS-1)) % DAYS_PER_WEEK);
        if (dayOfWeek < 0) {
            dayOfWeek += DAYS_PER_WEEK;
        }
        return dayOfWeek;
    }
    
    
    private void computeTimeFields(int millis) {
        if (millis < 0) {
            millis += MILLIS_PER_DAY;
        }
        int hourOfDay = (int) (millis % MILLIS_PER_DAY / MILLIS_PER_HOUR);
        fields[AM_PM] = (hourOfDay < 12) ? AM : PM;
        fields[HOUR] = hourOfDay % 12;
        fields[HOUR_OF_DAY] = hourOfDay;
        fields[MINUTE] = (int) (millis % MILLIS_PER_HOUR / MILLIS_PER_MINUTE);
        fields[SECOND] = (int) (millis % MILLIS_PER_MINUTE / 1000);
        fields[MILLISECOND] = millis % 1000;
    }
        

    private static final long EPOCH = 1174435200000L; // March 21, 2007, Midnight UTC 
    
    private static final long MILLIS_PER_MINUTE = 60 * 1000;
    private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE; 
    private static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    private static final long MILLIS_PER_YEAR = 365 * MILLIS_PER_DAY;

    private static final long YEARS_PER_GENERATION = 33;
    private static final long LEAP_YEARS_PER_GENERATION = 8;
    private static final long MILLIS_PER_GENERATION = YEARS_PER_GENERATION * MILLIS_PER_YEAR + LEAP_YEARS_PER_GENERATION * MILLIS_PER_DAY;

    private static final int DAYS_PER_WEEK = 7;
    private static final int DAYS_PER_BIMESTER = 61;

}
