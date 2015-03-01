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
        gregorian.setTimeZone(TimeZone.getTimeZone("UTC"));
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
    }
    
    
    @Test
    public void afterEpochTest() {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        gregorian.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        gregorian.setTimeZone(TimeZone.getTimeZone("UTC"));
        gregorian.setTime(epoch);
        gregorian.add(Calendar.YEAR, -2);
        int date = 1;
        int month = EarthianCalendar.ARIES;
        int year = -2;
        for (int i = 0; i < 4 * (365 * 33 + 8); ++i) {
            gregorian.add(Calendar.DATE, 1);
            calendar.setTimeInMillis(gregorian.getTimeInMillis());
            boolean leap = (year % 33) % 4 == 2;
            boolean monthTurn = ((month % 2 == 0 || (month == 11 && ! leap)) && date == 30) || date == 31;
            date = (monthTurn) ? 1 : date + 1;
            if (monthTurn) {
                month = (month + 1) % 12;
                if (month == 0) {
                    year++;
                }
            }
            assertEquals(date, calendar.get(Calendar.DATE));
            assertEquals(month, calendar.get(Calendar.MONTH));
            assertEquals(year, calendar.get(Calendar.YEAR));
        }
    }
    
    
    @Test
    public void nowTest() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        System.out.printf("%04d/%02d/%02d\n", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
    }
        
    
    
    private EarthianCalendar calendar;
    
    private final Date epoch;
    
}
