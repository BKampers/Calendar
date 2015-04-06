/*
** Copyright © Bart Kampers
*/

package bka.calendar;


import java.util.*;


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
            fields[ZONE_OFFSET] = timeZone.getRawOffset();
            isSet[ZONE_OFFSET] = true;
            fields[DST_OFFSET] = timeZone.getDSTSavings();
            isSet[DST_OFFSET] = true;
            millisSinceEpoch += timeZone.getOffset(time);
        }
        DateCalculations calculations = new DateCalculations(millisSinceEpoch);

        fields[ERA] = 0;
        isSet[ERA] = true;
        fields[YEAR] = calculations.year;
        isSet[YEAR] = true;
        fields[DAY_OF_YEAR] = calculations.dayOfYear;
        isSet[DAY_OF_YEAR] = true;
        fields[DAY_OF_WEEK] = calculations.dayOfWeekIndex + 1;
        isSet[DAY_OF_WEEK] = true;
        fields[WEEK_OF_YEAR] = weekOfYear(calculations);
        isSet[WEEK_OF_YEAR] = true;
                
        computeMonthFields(calculations);        
        computeTimeFields((int) (millisSinceEpoch % MILLIS_PER_DAY));
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
            case ERA: return 0;
            case YEAR: return Integer.MAX_VALUE;
            case MONTH: return 11;
            case WEEK_OF_YEAR: return 53;
            case WEEK_OF_MONTH: return 5;
            case DAY_OF_MONTH: return 31;
            case DAY_OF_YEAR: return 366;
            case DAY_OF_WEEK: return DAYS_PER_WEEK;
            case DAY_OF_WEEK_IN_MONTH: return DAYS_PER_WEEK;
            case AM_PM: return PM;
            case HOUR: return 11;
            case HOUR_OF_DAY: return 23;
            case MINUTE: return 59;
            case SECOND: return 59;
            case MILLISECOND: return 999;
            case ZONE_OFFSET: return 14 * MILLIS_PER_HOUR;
            case DST_OFFSET: return 2 * MILLIS_PER_HOUR;
            default: throw new IllegalArgumentException();
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

    
    private void computeMonthFields(DateCalculations calculations) {
        long millisSinceYearStart = calculations.millisSinceEpoch - calculations.yearStart;
        int bimesterIndex = (int) (millisSinceYearStart / MILLIS_PER_BIMESTER);
        int dayOfBimesterIndex = (int) (millisSinceYearStart % MILLIS_PER_BIMESTER / MILLIS_PER_DAY);
        int month = bimesterIndex * 2; 
        int dayOfMonthIndex = dayOfBimesterIndex;
        if (30 <= dayOfBimesterIndex) {
            month++;
            dayOfMonthIndex -= 30;
        }
        int dayOfMonthStart = dayOfWeekIndex(calculations.millisSinceEpoch - (long) dayOfMonthIndex * MILLIS_PER_DAY);
        int weekOfMonth = (dayOfMonthIndex + dayOfMonthStart) / DAYS_PER_WEEK;
        if (dayOfMonthStart < 4) {
            weekOfMonth++;
        }
        fields[MONTH] = month;
        isSet[MONTH] = true;
        fields[DAY_OF_MONTH] = dayOfMonthIndex + 1;
        isSet[DAY_OF_MONTH] = true;
        fields[WEEK_OF_MONTH] = weekOfMonth;
        isSet[WEEK_OF_MONTH] = true;
        fields[DAY_OF_WEEK_IN_MONTH] = dayOfMonthIndex / DAYS_PER_WEEK + 1;
        isSet[DAY_OF_WEEK_IN_MONTH] = true;
    }

    
    private void computeTimeFields(int millis) {
        if (millis < 0) {
            millis += MILLIS_PER_DAY;
        }
        int hourOfDay = millis % MILLIS_PER_DAY / MILLIS_PER_HOUR;
        fields[AM_PM] = (hourOfDay < 12) ? AM : PM;
        isSet[AM_PM] = true;
        fields[HOUR] = hourOfDay % 12;
        isSet[HOUR] = true;
        fields[HOUR_OF_DAY] = hourOfDay;
        isSet[HOUR_OF_DAY] = true;
        fields[MINUTE] = millis % MILLIS_PER_HOUR / MILLIS_PER_MINUTE;
        isSet[MINUTE] = true;
        fields[SECOND] = millis % MILLIS_PER_MINUTE / 1000;
        isSet[SECOND] = true;
        fields[MILLISECOND] = millis % 1000;
        isSet[MILLISECOND] = true;
    }
    
    
    private int weekOfYear(DateCalculations calculations) {
        int dayOfYearStart = dayOfWeekIndex(calculations.yearStart);
        if (dayOfYearStart < 4) {
            return weekOfYear(calculations.dayOfYear + dayOfYearStart - 1, calculations);
        }
        else if (calculations.dayOfYear + dayOfYearStart > DAYS_PER_WEEK) {
            return weekOfYear(calculations.dayOfYear + dayOfYearStart - DAYS_PER_WEEK - 1, calculations);
        }
        else {
            return weekOfYear(new DateCalculations(calculations.millisSinceEpoch - dayOfYearStart * MILLIS_PER_DAY));
        }
    }
    
    
    private int weekOfYear(int daysSinceWeek1, DateCalculations calculations) {
        int weekOfYear = daysSinceWeek1 / DAYS_PER_WEEK + 1;
        if (weekOfYear == 53 && dayCount(calculations.year) - calculations.dayOfYear + calculations.dayOfWeekIndex < 3) {
            weekOfYear = 1;
        }
        return weekOfYear;    
    }
    
    
    private int dayCount(int year) {
        int generationIndex =  year % YEARS_PER_GENERATION;
        if (generationIndex < 0) {
            generationIndex += YEARS_PER_GENERATION;
        }
        return (generationIndex % 4 == 2) ? 366 : 365;
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
    
    
    private int dayOfWeekIndex(long millisSinceEpoch) {
        int dayOfWeekIndex = (int) ((millisSinceEpoch / MILLIS_PER_DAY + 3) % DAYS_PER_WEEK);
        if (dayOfWeekIndex < 0) {
            dayOfWeekIndex += DAYS_PER_WEEK;
        }
        return dayOfWeekIndex;
    }
    
    
    private class DateCalculations {
        
        DateCalculations(long millisSinceEpoch) {
            this.millisSinceEpoch = millisSinceEpoch;
            generationIndex = millisSinceEpoch / MILLIS_PER_GENERATION;
            if (millisSinceEpoch < 0 && millisSinceEpoch % MILLIS_PER_GENERATION != 0) {
                generationIndex--;
            }
            generationStart = generationIndex * MILLIS_PER_GENERATION;
            yearIndex = yearIndex(millisSinceEpoch - generationStart);
            yearStart = generationStart + yearOffset(yearIndex);
            year = (int) (generationIndex * YEARS_PER_GENERATION + yearIndex(millisSinceEpoch - generationStart));
            dayOfYear = (int) ((millisSinceEpoch - yearStart) / MILLIS_PER_DAY) + 1;
            dayOfWeekIndex = dayOfWeekIndex(millisSinceEpoch);
        }
        
        long millisSinceEpoch;
        long generationStart;
        long generationIndex;
        long yearStart;
        int yearIndex;
        int year;
        int dayOfYear;
        int dayOfWeekIndex;
    }
        

    private static final long EPOCH = 1174435200000L; // March 21, 2007, Midnight UTC 
    
    private static final int MILLIS_PER_MINUTE = 60 * 1000;
    private static final int MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE; 
    private static final int MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    
    private static final long MILLIS_PER_BIMESTER = 61L * MILLIS_PER_DAY;
    private static final long MILLIS_PER_YEAR = 365L * MILLIS_PER_DAY;

    private static final int YEARS_PER_GENERATION = 33;
    private static final long LEAP_YEARS_PER_GENERATION = 8;
    private static final long MILLIS_PER_GENERATION = YEARS_PER_GENERATION * MILLIS_PER_YEAR + LEAP_YEARS_PER_GENERATION * MILLIS_PER_DAY;

    private static final int DAYS_PER_WEEK = 7;
    

}
