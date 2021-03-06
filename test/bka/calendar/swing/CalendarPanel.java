/*
** Copyright © Bart Kampers
*/

package bka.calendar.swing;

import bka.swing.clock.*;
import java.awt.*;
import java.util.*;


class CalendarPanel extends javax.swing.JPanel {


    CalendarPanel(Calendar calendar, Behavior behavior) {
        this.calendar = calendar;
        this.behavior = behavior;
        this.hourField = behavior.showNaturalDayClock() ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
        formatter = new Formatter(calendar);
        name = formatter.nameText();
        hourMaximum = calendar.getMaximum(hourField) + 1;
        minuteMaximum = calendar.getMaximum(Calendar.MINUTE) + 1;
        secondMaximum = calendar.getMaximum(Calendar.SECOND) + 1;
        initComponents();
        setNameSize();
        clock.setDiameter(2 * CLOCK_RADIUS);
        clock.setBackground(Color.WHITE);
        Point center = new java.awt.Point(CLOCK_RADIUS, CLOCK_RADIUS);
        Scale hourHandScale = new Scale();
        hourHandScale.setValueRange(0, hourMaximum);
        hourHand.setLength(25);
        hourHand.setStroke(new BasicStroke(3, CLOCK_HAND_CAP, CLOCK_HAND_JOIN));
        hourHand.setTurningPoint(center);
        hourHand.setScale(hourHandScale);
        Scale minuteHandScale = new Scale();
        minuteHandScale.setValueRange(0, minuteMaximum);
        minuteHand.setLength(40);
        minuteHand.setStroke(new BasicStroke(2, CLOCK_HAND_CAP, CLOCK_HAND_JOIN));
        minuteHand.setTurningPoint(center);
        minuteHand.setScale(minuteHandScale);
        Scale secondHandScale = new Scale();
        secondHandScale.setValueRange(0, secondMaximum);
        secondHand.setLength(45);
        secondHand.setTurningPoint(center);
        secondHand.setScale(secondHandScale);
        Scale hourValueScale = new Scale();
        double interval = (hourMaximum <= 12) ? 1.0 : 2.0;
        if (behavior.showMidnightAsZero()) {
            hourValueScale.setValueRange(0.0, hourMaximum - interval);
            hourValueScale.setAngleRange(0.0, (hourMaximum - interval) / hourMaximum);
        }
        else {
            hourValueScale.setValueRange(interval, hourMaximum);
            hourValueScale.setAngleRange(interval / hourMaximum, 1.0);
        }
        SimpleValueRing hourRing = new SimpleValueRing();
        hourRing.setInterval(interval);
        hourRing.setScale(hourValueScale);
        hourRing.setRadius(37);
        hourRing.setCenter(center);
        hourRing.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        hourRing.setColor(Color.BLUE);
        clock.addNeedle(hourHand);
        clock.addNeedle(minuteHand);
        clock.addNeedle(secondHand);
        clock.addRing(hourRing);
        clockPanel.add(clock);
        yearProgressBar.setStringPainted(true);
    }


    
    @Override
    public void repaint() {
        super.repaint();
        if (calendar != null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DATE);
            int hour = calendar.get(hourField);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String dateString = Integer.toString(dayOfMonth);
            dateLabel.setText(dateString);
            dateLabel.setForeground(behavior.isComplementaryDay(calendar) ? Style.HOLYDAY_FOREGROUND : Style.DEFAULT_FOREGROUND);
            yearDayLabel.setText(behavior.showDayNameOfYear(calendar) ? formatter.yearDayText() : "");
            weekdayLabel.setText(behavior.showDayNameOfWeek(calendar) ? formatter.weekdayText(getLocale()) : "");
            weekdayLabel.setForeground((behavior.isSabbath(calendar)) ? Style.HOLYDAY_FOREGROUND : Style.DEFAULT_FOREGROUND);
            monthLabel.setText(behavior.showMonth(calendar) ? formatter.monthText(getLocale()): "");
            yearLabel.setText(formatter.yearText());
            weekLabel.setText(formatter.weekText());
            yearProgressBar.setForeground(Color.red);
            yearProgressBar.setMaximum(calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
            yearProgressBar.setValue(calendar.get(Calendar.DAY_OF_YEAR));
            yearProgressBar.setString(formatter.dayOfYearText());
            double minuteValue = minute + (double) second / secondMaximum;
            secondHand.setValue(second);
            minuteHand.setValue(minuteValue);
            hourHand.setValue(hour + minuteValue / minuteMaximum);
            clock.repaint();
            datePanel.setToolTipText(String.format(behavior.getDateFormat(), calendar.get(Calendar.YEAR), month + 1, dayOfMonth));
            if (behavior.showNaturalDayClock()) {
                clockPanel.setToolTipText(String.format(behavior.getTimeFormat(), hour, minute));
            }
            else {
                String ampm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
                clockPanel.setToolTipText(String.format(behavior.getTimeFormat(), (hour == 0) ? 12 : hour, minute, ampm));
            }
        }
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        yearLabel = new javax.swing.JLabel();
        weekLabel = new javax.swing.JLabel();
        clockPanel = new javax.swing.JPanel();
        datePanel = new javax.swing.JPanel();
        weekdayLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        monthLabel = new javax.swing.JLabel();
        yearDayLabel = new javax.swing.JLabel();
        yearProgressBar = new javax.swing.JProgressBar();

        setBorder(javax.swing.BorderFactory.createTitledBorder(name));
        setBounds(new java.awt.Rectangle(0, 0, 380, 340));
        setMinimumSize(new java.awt.Dimension(380, 340));
        setPreferredSize(new java.awt.Dimension(380, 340));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.BorderLayout());

        topPanel.setPreferredSize(new java.awt.Dimension(300, 50));

        yearLabel.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        yearLabel.setForeground(Style.DEFAULT_FOREGROUND);
        yearLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        yearLabel.setBounds(new java.awt.Rectangle(0, 0, 160, 40));
        yearLabel.setPreferredSize(new java.awt.Dimension(160, 40));

        weekLabel.setFont(getDefaultFont(13));
        weekLabel.setForeground(Style.DEFAULT_FOREGROUND);
        weekLabel.setPreferredSize(new java.awt.Dimension(160, 40));
        weekLabel.setSize(new java.awt.Dimension(160, 40));

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(yearLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(weekLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(weekLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        add(topPanel, java.awt.BorderLayout.NORTH);

        clockPanel.setOpaque(false);
        clockPanel.setPreferredSize(new java.awt.Dimension(115, 115));
        add(clockPanel, java.awt.BorderLayout.CENTER);

        datePanel.setPreferredSize(new java.awt.Dimension(330, 100));
        datePanel.setRequestFocusEnabled(false);

        weekdayLabel.setFont(getDefaultFont(24));
        weekdayLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        weekdayLabel.setSize(new java.awt.Dimension(100, 50));

        dateLabel.setFont(getDefaultFont(48));
        dateLabel.setForeground(Style.DEFAULT_FOREGROUND);
        dateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateLabel.setSize(new java.awt.Dimension(50, 50));

        monthLabel.setFont(getDefaultFont(24));
        monthLabel.setForeground(Style.DEFAULT_FOREGROUND);
        monthLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        monthLabel.setSize(new java.awt.Dimension(100, 50));

        yearDayLabel.setFont(getDefaultFont(18));
        yearDayLabel.setForeground(Style.DEFAULT_FOREGROUND);
        yearDayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        yearProgressBar.setFont(getDefaultFont(9));
        yearProgressBar.setForeground(Style.DEFAULT_FOREGROUND);
        yearProgressBar.setBounds(new java.awt.Rectangle(0, 0, 146, 30));
        yearProgressBar.setMaximumSize(new java.awt.Dimension(32767, 30));
        yearProgressBar.setMinimumSize(new java.awt.Dimension(10, 30));
        yearProgressBar.setPreferredSize(new java.awt.Dimension(146, 30));
        yearProgressBar.setRequestFocusEnabled(false);
        yearProgressBar.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout datePanelLayout = new javax.swing.GroupLayout(datePanel);
        datePanel.setLayout(datePanelLayout);
        datePanelLayout.setHorizontalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datePanelLayout.createSequentialGroup()
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(datePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yearDayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(datePanelLayout.createSequentialGroup()
                                .addComponent(weekdayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(monthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))))
                    .addComponent(yearProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        datePanelLayout.setVerticalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(weekdayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yearDayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yearProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(datePanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
   

    private void setNameSize() {
        if (name.isEmpty()) {
            Dimension size = getPreferredSize();
            size.height += 18;
            setPreferredSize(size);
        }
    }

    
    private Font getDefaultFont(int size) {
        return new Font(Style.DEFAULT_FONT_NAME, Font.PLAIN, size);
    }

    
    private final Calendar calendar;
    private final Behavior behavior;
    private final Formatter formatter;
    private final String name;
    
    private final int hourField;

    private final int hourMaximum;
    private final int minuteMaximum;
    private final int secondMaximum;

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel clockPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JPanel datePanel;
    private javax.swing.JLabel monthLabel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel weekLabel;
    private javax.swing.JLabel weekdayLabel;
    private javax.swing.JLabel yearDayLabel;
    private javax.swing.JLabel yearLabel;
    private javax.swing.JProgressBar yearProgressBar;
    // End of variables declaration//GEN-END:variables

    private final SimpleClock clock = new SimpleClock();
    private final SimpleNeedle hourHand = new SimpleNeedle();
    private final SimpleNeedle minuteHand = new SimpleNeedle();
    private final SimpleNeedle secondHand = new SimpleNeedle();
    
    private static final int CLOCK_RADIUS = 50;
    private static final int CLOCK_HAND_CAP = BasicStroke.CAP_ROUND;
    private static final int CLOCK_HAND_JOIN = BasicStroke.JOIN_ROUND;
        
}
