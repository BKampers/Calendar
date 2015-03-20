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


/**
 *
 * @author BartK
 */
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
        assertEquals(EarthianCalendar.MERCURY, calendar.get(Calendar.DAY_OF_WEEK));
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
        
    
    
    private EarthianCalendar calendar;
    
    private final Date epoch;
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    
}
