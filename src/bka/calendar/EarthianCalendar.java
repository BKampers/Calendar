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
        time = 
            EPOCH +
            (fields[YEAR] / YEARS_PER_GENERATION) * MILLIS_PER_GENERATION + yearOffset(fields[YEAR] % YEARS_PER_GENERATION) +
            (fields[MONTH] / 2) * MILLIS_PER_BIMESTER + (fields[MONTH] % 2) * MILLIS_PER_SHORT_MONTH +
            (fields[DAY_OF_MONTH] - 1) * MILLIS_PER_DAY +
            fields[HOUR_OF_DAY] * MILLIS_PER_HOUR +
            fields[MINUTE] * MILLIS_PER_MINUTE +
            fields[SECOND] * 1000 +
            fields[MILLISECOND];
        TimeZone timeZone = getTimeZone();
        if (timeZone != null) {
            time -= timeZone.getOffset(time);
        }
    }


    @Override
    protected void computeFields() {
        long millisSinceEpoch = time - EPOCH + computeTimeZoneFields();
        DateCalculations calculations = new DateCalculations(millisSinceEpoch);
        computeYearFields(calculations);
        computeMonthFields(calculations);        
        computeTimeFields((int) (millisSinceEpoch % MILLIS_PER_DAY));
    }

    
    @Override
    public void add(int field, int amount) {
        switch (field) {
            case MILLISECOND:
                time += amount;
                break;
            case SECOND:
                time += amount * 1000;
                break;
            case MINUTE:
                time += amount * MILLIS_PER_MINUTE;
                break;
            case HOUR_OF_DAY:
            case HOUR:
                time += amount * MILLIS_PER_HOUR;
                field = HOUR_OF_DAY;
                break;
            case AM_PM:
                time += amount * 12 * MILLIS_PER_HOUR;
                break;
            case DAY_OF_WEEK_IN_MONTH:
            case DAY_OF_WEEK:
            case DAY_OF_YEAR:
            case DAY_OF_MONTH:
                time += amount * MILLIS_PER_DAY;
                field = HOUR_OF_DAY;
                break;
            case WEEK_OF_MONTH:
            case WEEK_OF_YEAR:
                time += amount * MILLIS_PER_WEEK;
                field = WEEK_OF_MONTH;
                break;
            case MONTH:
                set(MONTH, fields[MONTH] + amount);
                field = DAY_OF_WEEK_IN_MONTH;
                break;
            case YEAR:
                set(YEAR, fields[YEAR] + amount);
                field = MONTH;
                break;
            default:
                throw new IllegalArgumentException();
        }
        unsetFieldsFrom(field);
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
    
    
    private int computeTimeZoneFields() {
        TimeZone timeZone = getTimeZone();
        if (timeZone != null) {
            setField(ZONE_OFFSET, timeZone.getRawOffset());
            setField(DST_OFFSET, (timeZone.inDaylightTime(getTime())) ? timeZone.getDSTSavings() : 0);
            return timeZone.getOffset(time);
        }
        else {
            setField(ZONE_OFFSET, 0);
            setField(DST_OFFSET, 0);
            return 0;
        }
    }

    
    private void computeYearFields(DateCalculations calculations) {
        setField(ERA, 0);
        setField(YEAR, calculations.year);
        setField(DAY_OF_YEAR, calculations.dayOfYear);
        setField(DAY_OF_WEEK, calculations.dayOfWeekIndex + 1);
        setField(WEEK_OF_YEAR, weekOfYear(calculations));
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
        setField(MONTH, month);
        setField(DAY_OF_MONTH, dayOfMonthIndex + 1);
        setField(WEEK_OF_MONTH, weekOfMonth);
        setField(DAY_OF_WEEK_IN_MONTH, dayOfMonthIndex / DAYS_PER_WEEK + 1);
    }

    
    private void computeTimeFields(int millis) {
        if (millis < 0) {
            millis += MILLIS_PER_DAY;
        }
        int hourOfDay = millis % MILLIS_PER_DAY / MILLIS_PER_HOUR;
        setField(AM_PM, (hourOfDay < 12) ? AM : PM);
        setField(HOUR, hourOfDay % 12);
        setField(HOUR_OF_DAY, hourOfDay);
        setField(MINUTE, millis % MILLIS_PER_HOUR / MILLIS_PER_MINUTE);
        setField(SECOND, millis % MILLIS_PER_MINUTE / 1000);
        setField(MILLISECOND, millis % 1000);
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
    
    
    private void setField(int fieldIndex, int value) {
        fields[fieldIndex] = value;
        isSet[fieldIndex] = true;
    }
    
    
    private void unsetFieldsFrom(int fromField) {
        for (int field = fromField; field > ERA; --field) {
            isSet[field] = false;
        }
        areFieldsSet = false;
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
    private static final int MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;
    
    private static final long MILLIS_PER_SHORT_MONTH = 30L * MILLIS_PER_DAY;
    private static final long MILLIS_PER_BIMESTER = 61L * MILLIS_PER_DAY;
    private static final long MILLIS_PER_YEAR = 365L * MILLIS_PER_DAY;

    private static final int YEARS_PER_GENERATION = 33;
    private static final int LEAP_YEARS_PER_GENERATION = 8;
    private static final long MILLIS_PER_GENERATION = YEARS_PER_GENERATION * MILLIS_PER_YEAR + LEAP_YEARS_PER_GENERATION * MILLIS_PER_DAY;

    private static final int DAYS_PER_WEEK = 7;
    

}
