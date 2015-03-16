/*
** Copyright © Bart Kampers
*/

package bka.calendar;


import java.util.Calendar;


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
    
    public static final int SUN = Calendar.SUNDAY;
    public static final int LUNA = Calendar.MONDAY;
    public static final int MARS = Calendar.TUESDAY;
    public static final int MERCURY = Calendar.WEDNESDAY;
    public static final int JUPITER = Calendar.THURSDAY;
    public static final int VENUS = Calendar.FRIDAY;
    public static final int SATURN = Calendar.SATURDAY;
    

    @Override
    protected void computeTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected void computeFields() {
        long normal = time - EPOCH;
        long generationIndex = normal / MILLIS_PER_GENERATION;
        if (normal < 0 && normal % MILLIS_PER_GENERATION != 0) {
            generationIndex--;
        }
        long generationStart = EPOCH + generationIndex * MILLIS_PER_GENERATION;
        int yearIndex = yearIndex(time - generationStart);
        long yearStart = generationStart + yearOffset(yearIndex);
        fields[ERA] = 0;
        fields[YEAR] = (int) generationIndex * 33 + yearIndex(time - generationStart);
        int duoMonthIndex = (int) ((time - yearStart) / (61 * MILLIS_PER_DAY));
        int dayOfDuoMonth = (int) ((time - yearStart) % (61 * MILLIS_PER_DAY) / MILLIS_PER_DAY);
        if (dayOfDuoMonth < 30) {
            fields[MONTH] = duoMonthIndex * 2;
            fields[DAY_OF_MONTH] = dayOfDuoMonth + 1;
        }
        else {
            fields[MONTH] = duoMonthIndex * 2 + 1;
            fields[DAY_OF_MONTH] = dayOfDuoMonth - 29;            
        }
        fields[DAY_OF_YEAR] = (int) ((time - yearStart) / MILLIS_PER_DAY) + 1;
        int dayOfWeek = (int) ((normal / MILLIS_PER_DAY + (MERCURY-1)) % 7);
        if (dayOfWeek < 0) {
            dayOfWeek += 7;
        }
        fields[DAY_OF_WEEK] = dayOfWeek + 1;
        int weekOfYear = (fields[DAY_OF_YEAR] - fields[DAY_OF_WEEK] + 10) / 7;
        if (weekOfYear < 1) {
            
        }
        computeTimeFields();
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
            case DAY_OF_WEEK: return 6;
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
        assert 1 <= i && i <= 33; 
        return i - 1;
    }


    private long yearOffset(int yearIndex) {
        return yearIndex * MILLIS_PER_YEAR + ((yearIndex + 1) / 4) * MILLIS_PER_DAY;
    }
    
    
    private void computeTimeFields() {
        int millis = (int) (time % MILLIS_PER_DAY);
        if (millis < 0) {
            millis += MILLIS_PER_DAY;
        }
        int hourOfDay = millis % (24 * 60 * 60 * 1000) / (60 * 60 * 1000);
        fields[AM_PM] = (hourOfDay < 12) ? AM : PM;
        fields[HOUR] = hourOfDay % 12;
        fields[HOUR_OF_DAY] = hourOfDay;
        fields[MINUTE] = millis % (60 * 60 * 1000) / (60 * 1000);
        fields[SECOND] = millis % (60 * 1000) / 1000;
        fields[MILLISECOND] = millis % 1000;
    }
        

    private static final long EPOCH = 1174435200000L; // March 21, 2007 0h00"00' UTC 
    
    // A day has 1000 * 60 * 60 * 24 milliseconds
    private static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
    private static final long MILLIS_PER_YEAR = 365 * MILLIS_PER_DAY;
//    private static final long TWO_YEARS = 2 * MILLIS_PER_YEAR;
//    private static final long FOUR_YEARS = 4 * MILLIS_PER_YEAR + MILLIS_PER_DAY;
    private static final long MILLIS_PER_GENERATION = 33 * MILLIS_PER_YEAR + 8 * MILLIS_PER_DAY;

//    private static final int DAYS_PER_GENERATION = 33 * 365 + 8;
 
//    private static final long serialVersionUID = 1L;
   
}
