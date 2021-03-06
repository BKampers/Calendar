package bka.calendar;

/*
** Copyright © Bart Kampers
*/

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;


public class EarthianCalendarUnitTest {
    
    public EarthianCalendarUnitTest() {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTimeZone(UTC);
        gregorian.setTimeInMillis(0);
        gregorian.set(Calendar.YEAR, 2007);
        gregorian.set(Calendar.MONTH, Calendar.MARCH);
        gregorian.set(Calendar.DAY_OF_MONTH, 21);
        epoch = gregorian.getTime();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        calendar = new EarthianCalendar();
        calendar.setTimeZone(UTC);
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void epochTest() {
        calendar.setTime(epoch);
        assertEquals(0, calendar.get(Calendar.YEAR));
        assertEquals(EarthianCalendar.ARIES, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(EarthianCalendar.MERCURIUS, calendar.get(Calendar.DAY_OF_WEEK));
    }
    
    
    @Test
    public void afterEpochTest() {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTimeZone(UTC);
        gregorian.setTime(epoch);
        for (int i = 0; i < 200; ++i) {
            int increment = ((i % 33) % 4 == 2) ? 366 : 365;
            gregorian.add(Calendar.DATE, increment);
            calendar.setTime(gregorian.getTime());
            assertEquals(i+1, calendar.get(Calendar.YEAR));
        }
    }
    
    
    @Test
    public void beforeEpochTest() {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTimeZone(UTC);
        gregorian.setTime(epoch);
        for (int i = 0; i > -200; --i) {
            int increment = (((i-1) % 33) % 4 == -3) ? -366 : -365;
            gregorian.add(Calendar.DATE, increment);
            calendar.setTime(gregorian.getTime());
            assertEquals(i-1, calendar.get(Calendar.YEAR));
        }
    }
    
    
   @Test
   public void longTermTest() {
        Calendar gregorian = GregorianCalendar.getInstance();
        gregorian.setTimeZone(UTC);
        gregorian.clear();
        gregorian.set(Calendar.YEAR, 1900);
        gregorian.set(Calendar.MONTH, Calendar.JANUARY);
        gregorian.set(Calendar.DATE, 1);
        long millis = gregorian.getTimeInMillis();
        int expectedYear = -108;
        int expectedMonth = EarthianCalendar.CAPRICORNUS;
        int expectedDate = 13;
        int expectedDayOfYear = 287;
        int expectedDayOfWeek = EarthianCalendar.LUNA;
        int expectedWeekOfMonth = 3;
        int expectedDayOfWeekInMonth = 2;
        int expectedWeekOfYear = 42;
        int count = 1;
        while (expectedYear < 108)  {
            calendar.setTimeInMillis(millis);
            String message = "! [" + count + "] " + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.DATE);
            assertEquals("Year" + message, expectedYear, calendar.get(Calendar.YEAR));
            assertEquals("Month" + message, expectedMonth, calendar.get(Calendar.MONTH));
            assertEquals("Date" + message, expectedDate, calendar.get(Calendar.DATE));
            assertEquals("Day of Year" + message, expectedDayOfYear, calendar.get(Calendar.DAY_OF_YEAR));
            assertEquals("Day of Week" + message, expectedDayOfWeek, calendar.get(Calendar.DAY_OF_WEEK));
            assertEquals("Week of Month" + message, expectedWeekOfMonth, calendar.get(Calendar.WEEK_OF_MONTH));
            assertEquals("Day of Week in Month" + message, expectedDayOfWeekInMonth, calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
            assertEquals("Week of Year" + message, expectedWeekOfYear, calendar.get(Calendar.WEEK_OF_YEAR));
            millis += 1000 * 60 * 60 * 24;
            expectedDayOfWeek = (expectedDayOfWeek % 7) + 1;
            if (expectedDayOfYear == dayCount(expectedYear)) {
                expectedYear++;
                expectedMonth = EarthianCalendar.ARIES;
                expectedDate = 1;
                expectedDayOfYear = 1;
                expectedWeekOfMonth = (1 < expectedDayOfWeek && expectedDayOfWeek < 5) ? 1 : 0;
                expectedDayOfWeekInMonth = 1;
            }
            else {
                if (expectedMonth % 2 == 0 && expectedDate == 30 || expectedDate == 31) {
                    expectedMonth++;
                    expectedDate = 1;
                    expectedWeekOfMonth = (1 < expectedDayOfWeek && expectedDayOfWeek < 5) ? 1 : 0;
                    expectedDayOfWeekInMonth = 1;
                }
                else {
                    if (expectedDate % 7 == 0) {
                        expectedDayOfWeekInMonth++;
                    }
                    expectedDate++;
                }
                expectedDayOfYear++;
            }
            if (expectedDayOfWeek == 1) {
                if (expectedDayOfYear <= 4 || dayCount(expectedYear) - expectedDayOfYear <= 2) {
                    expectedWeekOfYear = 1;
                }
                else {
                    expectedWeekOfYear++;
                }
                expectedWeekOfMonth++;
            }
            count++;
        }
    }
    
    
    @Test
    public void dateTest() {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTimeZone(UTC);
        gregorian.setTime(epoch);
        gregorian.add(Calendar.YEAR, -2);
        int date = 1;
        int month = EarthianCalendar.ARIES;
        int year = -2;
        int dayOfYear = 1;
        for (int i = 0; i < 4 * (365 * 33 + 8); ++i) {
            gregorian.add(Calendar.DATE, 1);
            boolean leap = (year % 33) % 4 == 2;
            boolean monthTurn = ((month % 2 == 0 || (month == 11 && ! leap)) && date == 30) || date == 31;
            calendar.setTimeInMillis(gregorian.getTimeInMillis());
            date = (monthTurn) ? 1 : date + 1;
            dayOfYear++;
            if (monthTurn) {
                month = (month + 1) % 12;
                if (month == 0) {
                    year++;
                    dayOfYear = 1;
                }
            }
            assertEquals(date, calendar.get(Calendar.DATE));
            assertEquals(month, calendar.get(Calendar.MONTH));
            assertEquals(year, calendar.get(Calendar.YEAR));
            assertEquals(dayOfYear, calendar.get(Calendar.DAY_OF_YEAR));
            assertEquals(gregorian.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.DAY_OF_WEEK));
        }
    }
    
    
    @Test
    public void timeTest() {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTimeZone(TimeZone.getTimeZone("UTC"));
        for (long millis = -10000000000L; millis < 10000000000L; millis += 123456789L) {
            gregorian.setTimeInMillis(millis);
            calendar.setTimeInMillis(millis);
            for (int field = Calendar.AM_PM; field <= Calendar.MILLISECOND; ++field) {
               assertEquals(gregorian.get(field), calendar.get(field));
            }
        }
    }
    
    
    @Test
    public void timeZoneTest() {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTime(epoch);
        calendar.setTime(epoch);
        for (String zoneId : TimeZone.getAvailableIDs()) {
            TimeZone zone = TimeZone.getTimeZone(zoneId);
            calendar.setTimeZone(zone);
            gregorian.setTimeZone(zone);
            for (int field = Calendar.AM_PM; field <= Calendar.MILLISECOND; ++field) {
               assertEquals(gregorian.get(field), calendar.get(field));
            }        
        }   
    }
    
    
    @Test
    public void fieldSetTest() {
        calendar.set(Calendar.YEAR, 0);
        calendar.set(Calendar.MONTH, EarthianCalendar.ARIES);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(epoch.getTime(), calendar.getTimeInMillis());
        calendar.set(Calendar.YEAR, -38);
        calendar.set(Calendar.MONTH, EarthianCalendar.CAPRICORNUS);
        calendar.set(Calendar.DATE, 13);
        assertEquals(0L, calendar.getTimeInMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(-60 * 60 * 1000L, calendar.getTimeInMillis());        
    }

    
    @Test
    public void fieldAddTest() {
        calendar.setTime(epoch);
        int milli = calendar.get(Calendar.MILLISECOND); 
        calendar.add(Calendar.MILLISECOND, 1);
        assertEquals((milli + 1) % 1000, calendar.get(Calendar.MILLISECOND));
        int second = calendar.get(Calendar.SECOND);
        calendar.add(Calendar.MILLISECOND, 1000);
        assertEquals((second + 1) % 60, calendar.get(Calendar.SECOND));
        int minute = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.SECOND, 60);
        assertEquals((minute + 1) % 60, calendar.get(Calendar.MINUTE));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.add(Calendar.MINUTE, 60);
        assertEquals((hour + 1) % 24, calendar.get(Calendar.HOUR));
        int month = calendar.get(Calendar.MONTH);
        calendar.add(Calendar.MONTH, 1);
        assertEquals(month + 1, calendar.get(Calendar.MONTH));
        int year = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.YEAR, 1);
        assertEquals(year + 1, calendar.get(Calendar.YEAR));
    }
    
    
    @Test
    public void monthAddTest() {
        calendar.setTime(epoch);
        calendar.add(Calendar.MONTH, 5);
        assertEquals(5, calendar.get(Calendar.MONTH));
        assertEquals(0, calendar.get(Calendar.YEAR));
        calendar.add(Calendar.MONTH, 12);
        assertEquals(5, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.YEAR));
        calendar.setTime(epoch);
        calendar.add(Calendar.MONTH, -1);
        assertEquals(11, calendar.get(Calendar.MONTH));
        assertEquals(-1, calendar.get(Calendar.YEAR));
        calendar.add(Calendar.MONTH, -12);
        assertEquals(11, calendar.get(Calendar.MONTH));
        assertEquals(-2, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.add(Calendar.MONTH, 1);
        assertEquals(2, calendar.get(Calendar.MONTH));
        assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
    }
    
    
    @Test
    public void fieldRollTest() {
        calendar.setTime(epoch);
        int month = calendar.get(Calendar.MONTH);
        for (int i = 1; i <= 12; ++i) {
            calendar.roll(Calendar.MONTH, 1);
            assertEquals((month + i) % 12, calendar.get(Calendar.MONTH));
        }
    }
    
    
    @Test
    public void millisTest() {
        calendar.set(Calendar.YEAR, -2);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        long millis = calendar.getTimeInMillis();
        calendar.setTimeInMillis(millis);
        assertEquals(-2, calendar.get(Calendar.YEAR));
        assertEquals(1, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH));
    }
   
   
   private int dayCount(int year) {
        return (isLeapYear(year)) ? 366 : 365;
    }
   
   
   private boolean isLeapYear(int year) {
        int generationIndex =  (int) (year % 33);
        if (generationIndex < 0) {
            generationIndex += 33;
        }
        return generationIndex % 4 == 2;
    }
   
   
    private EarthianCalendar calendar;
    
    private final Date epoch;
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    
}
