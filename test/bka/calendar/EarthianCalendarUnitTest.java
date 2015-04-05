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
            System.out.println("Year = " + i);
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
    public void weekTest() {
        Calendar gregorian = GregorianCalendar.getInstance();
        gregorian.setTimeZone(UTC);
        gregorian.clear();
        gregorian.set(Calendar.YEAR, 1974);
        gregorian.set(Calendar.MONTH, Calendar.MARCH);
        gregorian.set(Calendar.DATE, 21);
        calendar.setTimeInMillis(gregorian.getTimeInMillis());
        int year = calendar.get(Calendar.YEAR);
        int expectedWeek = 53;
        int i = 1;
        while (year < 33) {
            //assertEquals(expectedWeek, calendar.get(Calendar.WEEK_OF_YEAR));
            System.out.printf(
                "%3d): %4d-%02d-%02d = %04d/%02d/%02d (%2d)\n",
                i,
                gregorian.get(Calendar.YEAR), gregorian.get(Calendar.MONTH) + 1, gregorian.get(Calendar.DATE),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE),
                calendar.get(Calendar.WEEK_OF_YEAR)); 
            if (expectedWeek != calendar.get(Calendar.WEEK_OF_YEAR)) {
                System.out.printf(" %d != %d\n", expectedWeek, calendar.get(Calendar.WEEK_OF_YEAR));
                calendar.clear();
                calendar.setTimeInMillis(gregorian.getTimeInMillis());
            }
            i++;
            gregorian.add(Calendar.DAY_OF_YEAR, 7);
            calendar.setTimeInMillis(gregorian.getTimeInMillis());
            expectedWeek++;
            if (expectedWeek > 52) {
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                int daysToNewYear = dayCount(year) - dayOfYear;
                if (3 < daysToNewYear) {
                    expectedWeek = 1;
                }
            }
            year = calendar.get(Calendar.YEAR);
        }
//        calendar.setTime(epoch);
//        assertEquals(1, calendar.get(Calendar.WEEK_OF_YEAR));
//        GregorianCalendar gregorian = new GregorianCalendar();
//        gregorian.setTimeZone(UTC);
//        gregorian.set(Calendar.YEAR, 2007);        
//        gregorian.set(Calendar.MONTH, Calendar.MARCH);
//        gregorian.set(Calendar.DATE, 17);
//        calendar.setTimeInMillis(gregorian.getTimeInMillis());
//        assertEquals(52, calendar.get(Calendar.WEEK_OF_YEAR));
//        gregorian.set(Calendar.YEAR, 2014);        
//        gregorian.set(Calendar.MONTH, Calendar.MARCH);
//        gregorian.set(Calendar.DATE, 21);
//        calendar.setTimeInMillis(gregorian.getTimeInMillis());
//        assertEquals(53, calendar.get(Calendar.WEEK_OF_YEAR));
//        gregorian.set(Calendar.YEAR, 2014);        
//        gregorian.set(Calendar.MONTH, Calendar.MARCH);
//        gregorian.set(Calendar.DATE, 22);
//        calendar.setTimeInMillis(gregorian.getTimeInMillis());
//        assertEquals(53, calendar.get(Calendar.WEEK_OF_YEAR));
    }
    
    
   @Test
   public void longTermTest() {
        Calendar gregorian = GregorianCalendar.getInstance();
        gregorian.setTimeZone(UTC);
        gregorian.clear();
        gregorian.set(Calendar.YEAR, 1941);
        gregorian.set(Calendar.MONTH, Calendar.MARCH);
        gregorian.set(Calendar.DATE, 21);
        long millis = gregorian.getTimeInMillis();
        int expectedYear = -66;
        int expectedMonth = EarthianCalendar.ARIES;
        int expectedDate = 1;
        int expectedDayOfYear = 1;
        int expectedDayOfWeek = EarthianCalendar.VENUS;
        int expectedWeekOfYear = 52;
        int count = 1;
        while (expectedYear < 66)  {
            calendar.setTimeInMillis(millis);
            String message = "! [" + count + "] " + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.DATE);
            assertEquals("Year" + message, expectedYear, calendar.get(Calendar.YEAR));
            assertEquals("Month" + message, expectedMonth, calendar.get(Calendar.MONTH));
            assertEquals("Date" + message, expectedDate, calendar.get(Calendar.DATE));
            assertEquals("Day of Year" + message, expectedDayOfYear, calendar.get(Calendar.DAY_OF_YEAR));
            assertEquals("Day of Week" + message, expectedDayOfWeek, calendar.get(Calendar.DAY_OF_WEEK));
            assertEquals("Week of Year" + message, expectedWeekOfYear, calendar.get(Calendar.WEEK_OF_YEAR));
            millis += 1000 * 60 * 60 * 24;
            if (expectedDayOfYear == dayCount(expectedYear)) {
                expectedYear++;
                expectedMonth = EarthianCalendar.ARIES;
                expectedDate = 1;
                expectedDayOfYear = 1;
            }
            else {
                if (expectedMonth % 2 == 0 && expectedDate == 30 || expectedDate == 31) {
                    expectedMonth++;
                    expectedDate = 1;  
                }
                else {
                    expectedDate++;
                }
                expectedDayOfYear++;
            }
            expectedDayOfWeek = (expectedDayOfWeek % 7) + 1;
            if (expectedDayOfWeek == 1) {
                if (expectedDayOfYear <= 4 || dayCount(expectedYear) - expectedDayOfYear <= 2) {
                    expectedWeekOfYear = 1;
                }
                else {
                    expectedWeekOfYear++;
                }
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
            System.out.printf("Zone %s\n", zoneId);
            TimeZone zone = TimeZone.getTimeZone(zoneId);
            calendar.setTimeZone(zone);
            gregorian.setTimeZone(zone);
            for (int field = Calendar.AM_PM; field <= Calendar.MILLISECOND; ++field) {
               assertEquals(gregorian.get(field), calendar.get(field));
            }
        }        
    }
    
    
    @Test
    public void nowTest() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());
        System.out.printf(
            "%04d/%02d/%02d %d:%02d:%02d.%03d %s\n",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DATE),
            ((calendar.get(Calendar.HOUR) != 0) ? calendar.get(Calendar.HOUR) : 12),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND),
            calendar.get(Calendar.MILLISECOND),
            ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM")
        );
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
