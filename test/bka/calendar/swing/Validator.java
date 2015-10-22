/*
 * Copyright © Bart Kampers
 */
package bka.calendar.swing;

import java.util.Calendar;


interface Validator {

    boolean isSabbath(Calendar calendar);

    boolean isComplementaryDay(Calendar calendar);
    
}
