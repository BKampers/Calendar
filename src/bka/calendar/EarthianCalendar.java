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
    

    protected void computeTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected void computeFields() {
        Generation generation = new Generation(time);
        int yearIndex = generation.yearIndex(time - generation.start);
        fields[YEAR] = (int) generation.index * 33 + generation.yearIndex(time - generation.start);
        int duoMonthIndex = (int) ((time - generation.yearStart(yearIndex)) / (61 * ONE_DAY));
        int dayOfDuoMonth = (int) ((time - generation.yearStart(yearIndex)) % (61 * ONE_DAY) / ONE_DAY);
        if (dayOfDuoMonth < 30) {
            fields[MONTH] = duoMonthIndex * 2;
            fields[DAY_OF_MONTH] = dayOfDuoMonth + 1;
        }
        else {
            fields[MONTH] = duoMonthIndex * 2 + 1;
            fields[DAY_OF_MONTH] = dayOfDuoMonth - 29;            
        }
    }


    public void add(int field, int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void roll(int field, boolean up) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public int getMinimum(int field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public int getMaximum(int field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public int getGreatestMinimum(int field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public int getLeastMaximum(int field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    private class Generation {
        
        Generation(long millis) {
            long normal = millis - EPOCH;
            index = normal / ONE_GENERATION;
            if (normal < 0 && normal % ONE_GENERATION != 0) {
                index--;
            }
            start = EPOCH + index * ONE_GENERATION;
        }
        
        int yearIndex(long offset) {
//            double calc = (offset - 0.25*ONE_DAY)/(0.25*ONE_DAY+ONE_YEAR);
//            int yearIndex = (int) calc;
//            return yearIndex % 33;
            int i = 1;
            while (yearOffset(i) <= offset) {
                i++;
            }
            return i - 1;
        }
        
        long yearStart(int yearIndex) {
            assert 0 <= yearIndex && yearIndex <= 32;
            return start + yearOffset(yearIndex);
        }
        
        long yearOffset(int yearIndex) {
//            switch (yearIndex) {
//                case  0: return  0 * ONE_YEAR;
//                case  1: return  1 * ONE_YEAR;
//                case  2: return  2 * ONE_YEAR;
//                case  3: return  3 * ONE_YEAR + 1 * ONE_DAY;
//                case  4: return  4 * ONE_YEAR + 1 * ONE_DAY;
//                case  5: return  5 * ONE_YEAR + 1 * ONE_DAY;
//                case  6: return  6 * ONE_YEAR + 1 * ONE_DAY;
//                case  7: return  7 * ONE_YEAR + 2 * ONE_DAY;
//                case  8: return  8 * ONE_YEAR + 2 * ONE_DAY;
//                case  9: return  9 * ONE_YEAR + 2 * ONE_DAY;
//                case 10: return 10 * ONE_YEAR + 2 * ONE_DAY;
//                case 11: return 11 * ONE_YEAR + 3 * ONE_DAY;
//            }
            return yearIndex * ONE_YEAR + ((yearIndex + 1) / 4) * ONE_DAY;
        }
        
        long start;
        long index;
    }
    
    private static final long EPOCH = 1174435200000L; // March 21, 2007 0h00"00' UTC 
    
    // A day has 1000 * 60 * 60 * 24 milliseconds
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;
    private static final long ONE_YEAR = 365 * ONE_DAY;
    private static final long TWO_YEARS = 2 * ONE_YEAR;
    private static final long FOUR_YEARS = 4 * ONE_YEAR + ONE_DAY;
    private static final long ONE_GENERATION = 33 * ONE_YEAR + 8 * ONE_DAY;

    private static final int DAYS_PER_GENERATION = 33 * 365 + 8;
 
    private static final long serialVersionUID = 1L;
   
}
