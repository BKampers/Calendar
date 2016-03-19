/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bka.calendar;

import java.util.*;

/**
 *
 * @author bartk
 */
public class Analysis {
 
    
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        dumpIsSet(calendar);
        calendar.clear();
        dumpIsSet(calendar);
        int month = calendar.get(Calendar.MONTH);
        System.out.println("offset = " + month);
        dumpIsSet(calendar);
        calendar.add(Calendar.MONTH, 2);
        dumpIsSet(calendar);
        System.out.println("offset = " + month);
    }
    
    
    private static void dumpIsSet(Calendar calendar) {
        System.out.println("=== isSet ===");
        for (int field = 0; field < Calendar.FIELD_COUNT; ++field) {
            System.out.printf("%d: %b\n", field, calendar.isSet(field));
        }
    }
    
    
}
