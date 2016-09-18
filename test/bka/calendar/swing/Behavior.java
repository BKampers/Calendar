/*
 * Copyright © Bart Kampers
 */
package bka.calendar.swing;

import java.util.Calendar;


interface Behavior {

    boolean isSabbath(Calendar calendar);

    boolean isComplementaryDay(Calendar calendar);

    boolean showDayNameOfWeek(Calendar calendar);
    
    boolean showDayNameOfYear(Calendar calendar);
    
    boolean showNaturalDayClock();

    String getDateFormat();

    String getTimeFormat();
    
}
