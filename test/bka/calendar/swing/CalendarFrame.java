/*
 * Copyright © Bart Kampers
 */

package bka.calendar.swing;


import bka.calendar.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;


public class CalendarFrame extends javax.swing.JFrame {

    
    public CalendarFrame() {
        gregorianPanel = new CalendarPanel(gregorianCalendar, getBehavior(gregorianCalendar));
        republicanPanel = new CalendarPanel(republicanCalendar, getBehavior(republicanCalendar));
        earthianPanel = new CalendarPanel(earthianCalendar, getBehavior(earthianCalendar));
        initComponents();
        calendarsPanel.add(gregorianPanel);
        calendarsPanel.add(republicanPanel);
        calendarsPanel.add(earthianPanel);
        updateTimer.schedule(new UpdateTask(), 1000 - System.currentTimeMillis() % 1000, 1000);
        gregorianPanel.addMouseListener(mouseListener);
        republicanPanel.addMouseListener(mouseListener);
        earthianPanel.addMouseListener(mouseListener);
        selectedPanel = gregorianPanel;
        selectedCalendar = gregorianCalendar;
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Calendars");
        setBounds(new java.awt.Rectangle(0, 23, 1170, 380));
        setName("calendarFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1170, 380));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(calendarsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(calendarsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CalendarFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalendarFrame().setVisible(true);
            }
        });
    }
    
    
    private void setIcon(Calendar calendar) {
        String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            setMacIcon(calendar);
        }
        else if (osName.contains("Windows")) {
            setWindowsIcons(calendar);
        }
        else {
            setIconImage(createDefaultImage(calendar, 1024, null));
        }
    }

    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setMacIcon(Calendar calendar) {
        try {
            Class applicationClass = Class.forName("com.apple.eawt.Application");
            Method getApplication = applicationClass.getMethod("getApplication");
            Object application = getApplication.invoke(null);
            Method setDockIconImage = applicationClass.getMethod("setDockIconImage", Image.class);
            Object[] imageParameter = new Object[] { createDefaultImage(calendar, 1024, null) };
            setDockIconImage.invoke(application, imageParameter);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(CalendarFrame.class.getName()).log(Level.INFO, "Mac dock icon set failed", ex);
        }
        setIconImage(createDefaultImage(calendar, 1024, Color.GRAY));
    }
    
    
    private void setWindowsIcons(Calendar calendar) {
        ArrayList<Image> images = new ArrayList<>();
        images.add(createWindowsImage(calendar, 32));
        images.add(createWindowsImage(calendar, 256));
        setIconImages(images);
    }

    
    private BufferedImage createDefaultImage(Calendar calendar, int size, Color textColor) {
        Behavior behavior = getBehavior(calendar);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        drawImageBackground(graphics, size);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size / 3));
        FontMetrics metrics = graphics.getFontMetrics();
        String date = Integer.toString(calendar.get(Calendar.DATE));
        int x = (size - metrics.stringWidth(date)) / 2;
        graphics.setColor((textColor == null) ? behavior.isSabbath(calendar) || behavior.isComplementaryDay(calendar) ? Style.HOLYDAY_FOREGROUND : Style.DEFAULT_FOREGROUND : textColor);
        graphics.drawString(date, x, size / 2);
        if (! behavior.isComplementaryDay(calendar)) {
            graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size / 8));        
        }
        else {
            graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size / 16));
        }
        metrics = graphics.getFontMetrics();
        Formatter formatter = new Formatter(calendar);
        String text = (! behavior.isComplementaryDay(calendar)) ? formatter.monthText(selectedPanel.getLocale()) : formatter.yearDayText();
        x = (size - metrics.stringWidth(text)) / 2;
        graphics.setColor((textColor == null) ? Style.DEFAULT_FOREGROUND : textColor);
        graphics.drawString(text, x, size - size / 4);
        return image;
    }
    
    
    private BufferedImage createWindowsImage(Calendar calendar, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        drawImageBackground(graphics, size);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size / 2));
        FontMetrics metrics = graphics.getFontMetrics();
        String date = Integer.toString(calendar.get(Calendar.DATE));
        int x = (size - metrics.stringWidth(date)) / 2;
        graphics.setColor(Style.DEFAULT_FOREGROUND);
        graphics.drawString(date, x, size / 16 * 10);
        return image;
    }

    
    private static void drawImageBackground(Graphics2D graphics, int size) {
        int margin = size / 16;
        int innerSize = size - 2 * margin;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(margin, margin, innerSize, innerSize);
        graphics.setColor(Color.GRAY);
        graphics.drawRect(margin, margin, innerSize, innerSize);
    }
    
    
    private static Behavior getBehavior(Calendar calendar) {
        if (calendar instanceof FrenchRepublicanCalendar) {
            return FRENCH_REPUBLICAN_BEHAVIOR;
        }
        else if (calendar instanceof EarthianCalendar) {
            return EARTHIAN_BEHAVIOR;
        }
        else {
            return DEFAULT_BEHAVIOR;
        }
    }
    
        
    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            gregorianPanel.repaint();
            republicanPanel.repaint();
            earthianPanel.repaint();
            int dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH);
            int month = selectedCalendar.get(Calendar.MONTH);
            if (iconDate != dayOfMonth || iconMonth != month) {
                setIcon(selectedCalendar);
                iconDate = dayOfMonth;
                iconMonth = month;
            }
        }
        
        private int iconDate;
        private int iconMonth;

    }
    
    
    private final MouseListener mouseListener =  new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent evt) {
        }

        @Override
        public void mousePressed(MouseEvent evt) {
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
        }

        @Override
        public void mouseEntered(MouseEvent evt) {
            selectedPanel = (javax.swing.JPanel) evt.getSource();
            if (selectedPanel == gregorianPanel) {
                selectedCalendar = gregorianCalendar;
            }
            else if (selectedPanel == republicanPanel) {
                selectedCalendar = republicanCalendar;
            }
            else if (selectedPanel == earthianPanel) {
                selectedCalendar = earthianCalendar;
            }
        }

        @Override
        public void mouseExited(MouseEvent evt) {
        }
        
    };
    
    
    private static final Behavior DEFAULT_BEHAVIOR = new Behavior() {
        
        @Override
        public boolean isSabbath(Calendar calendar) {
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
        }
        
        @Override
        public boolean isComplementaryDay(Calendar calendar) {
            return false;
        }

        @Override
        public boolean showDayNameOfWeek(Calendar calendar) {
            return true;
        }

        @Override
        public boolean showDayNameOfYear(Calendar calendar) {
            return false;
        }
        
        @Override
        public boolean showMonth(Calendar calendar) {
            return true;
        }

        @Override
        public boolean showNaturalDayClock() {
            return false;
        }

        @Override
        public boolean showMidnightAsZero() {
            return false;
        }

        @Override
        public String getDateFormat() {
            return DEFAULT_DATE_FORMAT;
        }

        @Override
        public String getTimeFormat() {
            return CLASSIC_TIME_FORMAT;
        }

    };
    

    private static final Behavior FRENCH_REPUBLICAN_BEHAVIOR = new Behavior() {
        
        @Override
        public boolean isSabbath(Calendar calendar) {
            return calendar.get(Calendar.DAY_OF_WEEK) == FrenchRepublicanCalendar.DÉCADI;
        }
        
        @Override
        public boolean isComplementaryDay(Calendar calendar) {
            return calendar.get(Calendar.MONTH) == FrenchRepublicanCalendar.JOURS_COMPLÉMENTAIRES;
        }
        
        @Override
        public boolean showDayNameOfWeek(Calendar calendar) {
            return ! isComplementaryDay(calendar);
        }

        @Override
        public boolean showDayNameOfYear(Calendar calendar) {
            return isComplementaryDay(calendar);
        }
        
        @Override
        public boolean showMonth(Calendar calendar) {
            return ! isComplementaryDay(calendar);
        }

        @Override
        public boolean showNaturalDayClock() {
            return true;
        }

        @Override
        public boolean showMidnightAsZero() {
            return false;
        }

        @Override
        public String getDateFormat() {
            return DEFAULT_DATE_FORMAT;
        }

        @Override
        public String getTimeFormat() {
            return REPUBLICAN_TIME_FORMAT;
        }

    };
    
    
    private static final Behavior EARTHIAN_BEHAVIOR = new Behavior() {
        
        @Override
        public boolean isSabbath(Calendar calendar) {
            return false;
        }
        
        @Override
        public boolean isComplementaryDay(Calendar calendar) {
            return false;
        }
        
        @Override
        public boolean showDayNameOfWeek(Calendar calendar) {
            return true;
        }

        @Override
        public boolean showDayNameOfYear(Calendar calendar) {
            return false;
        }
        
        @Override
        public boolean showMonth(Calendar calendar) {
            return true;
        }

        @Override
        public boolean showNaturalDayClock() {
            return true;
        }

        @Override
        public boolean showMidnightAsZero() {
            return true;
        }

        @Override
        public String getDateFormat() {
            return EARTHIAN_DATE_FORMAT;
        }

        @Override
        public String getTimeFormat() {
            return DEFAULT_TIME_FORMAT;
        }

    };
    

    private javax.swing.JPanel selectedPanel;
    private Calendar selectedCalendar;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JPanel calendarsPanel = new javax.swing.JPanel();
    // End of variables declaration//GEN-END:variables

    
    private final Calendar gregorianCalendar = new GregorianCalendar();
    private final Calendar republicanCalendar = new FrenchRepublicanCalendar();
    private final Calendar earthianCalendar = new EarthianCalendar();
    
    private final CalendarPanel gregorianPanel;
    private final CalendarPanel republicanPanel;
    private final CalendarPanel earthianPanel;
    
    private final Timer updateTimer = new Timer(UpdateTask.class.toString());


    private static final String DEFAULT_DATE_FORMAT = "%d/%02d/%02d";
    private static final String EARTHIAN_DATE_FORMAT = "%04d/%02d/%02d";

    private static final String DEFAULT_TIME_FORMAT = "%02d:%02d";
    private static final String CLASSIC_TIME_FORMAT = "%d:%02d %s";
    private static final String REPUBLICAN_TIME_FORMAT = "%d:%02d";


}
