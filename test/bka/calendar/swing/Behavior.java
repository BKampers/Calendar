/*
 * Copyright © Bart Kampers
 */
package bka.calendar.swing;


import java.util.*;


interface Behavior {

    boolean isSabbath(Calendar calendar);

    boolean isComplementaryDay(Calendar calendar);

    boolean showDayNameOfWeek(Calendar calendar);
    
    boolean showDayNameOfYear(Calendar calendar);
    
    public boolean showMonth(Calendar calendar);

    boolean showNaturalDayClock();
    
    boolean showMidnightAsZero();

    String getDateFormat();

    String getTimeFormat();

}
