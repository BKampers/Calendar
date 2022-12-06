/*
** Copyright © Bart Kampers
*/

package bka.calendar.swing;

import bka.swing.clock.*;
import java.awt.*;
import java.time.*;
import java.util.*;


class CalendarPanel extends javax.swing.JPanel {

    CalendarPanel(Calendar calendar, Behavior behavior) {
        this.calendar = Objects.requireNonNull(calendar);
        this.behavior = Objects.requireNonNull(behavior);
        this.hourField = behavior.showNaturalDayClock() ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
        formatter = new CalendarFormatter(calendar);
        name = formatter.nameText();
        hourMaximum = calendar.getMaximum(hourField) + 1;
        minuteMaximum = calendar.getMaximum(Calendar.MINUTE) + 1;
        secondMaximum = calendar.getMaximum(Calendar.SECOND) + 1;
        hourHand = new SimpleNeedle(getCenter(), createHourScale());
        minuteHand = new SimpleNeedle(getCenter(), new Scale(0, minuteMaximum));
        secondHand = new SimpleNeedle(getCenter(), new Scale(0, secondMaximum));
        initComponents();
        setNameSize();
        clock.setDiameter(2 * CLOCK_RADIUS);
        hourHand.setLength(25);
        hourHand.setStroke(new BasicStroke(3, CLOCK_HAND_CAP, CLOCK_HAND_JOIN));
        minuteHand.setLength(40);
        minuteHand.setStroke(new BasicStroke(2, CLOCK_HAND_CAP, CLOCK_HAND_JOIN));
        secondHand.setLength(45);
        double interval = (hourMaximum <= 12) ? 1.0 : 2.0;
        Scale hourValueScale = getHourValueScale(interval);
        hourRing = new SimpleValueRing(getCenter(), HOUR_RING_RADIUS, hourValueScale, interval);
        hourRing.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        clock.addNeedle(hourHand);
        clock.addNeedle(minuteHand);
        clock.addNeedle(secondHand);
        clock.addRing(hourRing);
        clockPanel.add(clock);
        yearProgressBar.setStringPainted(true);
        componentsInitialized = true;
    }

    private Scale getHourValueScale(double interval) {
        if (behavior.showMidnightAsZero()) {
            return new Scale(0.0, hourMaximum - interval, 0.0, (hourMaximum - interval) / hourMaximum);
        }
        return new Scale(interval, hourMaximum, interval / hourMaximum, 1.0);
    }

    public void setSolarDecorator(SolarDecorator solarDecorator) {
        this.solarDecorator = solarDecorator;
        if (solarDecorator != null) {
            solarDecorator.initialize(calendarLocalDateTime());
            clock.addRing(new ArcRing(getCenter(), DAYLIGHT_RING_RADIUS, createHourScale(), solarDecorator.getArcs()));
        }
    }

    private Scale createHourScale() {
        return new Scale(0, hourMaximum);
    }

    private Point getCenter() {
        return new java.awt.Point(CLOCK_RADIUS, CLOCK_RADIUS);
    }

    @Override
    public void repaint() {
        super.repaint();
        if (componentsInitialized) {
            repaintComponents();
        }
    }

    private void repaintComponents() {
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
        monthLabel.setText(behavior.showMonth(calendar) ? formatter.monthText(getLocale()) : "");
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
        setClockColors();
        if (solarDecorator != null) {
            solarDecorator.update(calendarLocalDateTime());
        }
        clock.repaint();
        setDateToolTipText(month, dayOfMonth);
        setTimeToolTipText(hour, minute);
    }

    private void setDateToolTipText(int month, int dayOfMonth) {
        datePanel.setToolTipText(String.format(behavior.getDateFormat(), calendar.get(Calendar.YEAR), month + 1, dayOfMonth));
    }

    private void setTimeToolTipText(int hour, int minute) {
        if (behavior.showNaturalDayClock()) {
            clockPanel.setToolTipText(String.format(behavior.getTimeFormat(), hour, minute));
        }
        else {
            String ampm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
            clockPanel.setToolTipText(String.format(behavior.getTimeFormat(), (hour == 0) ? 12 : hour, minute, ampm));
        }
    }

    private void setClockColors() {
        if (solarDecorator != null) {
            SolarDecorator.Event event = solarDecorator.upcomingEvent();
            clock.setBackground(faceColor(event));
            hourRing.setPaint(markerColor(event));
            hourHand.setPaint(handColor(event));
            minuteHand.setPaint(handColor(event));
            secondHand.setPaint(handColor(event));
        }
        else {
            clock.setBackground(Color.WHITE);
            hourRing.setPaint(Color.BLUE);
        }
    }

    private static Color faceColor(SolarDecorator.Event event) {
        switch (event) {
            case ASTRONIMICAL_SUNRISE:
                return SolarDecorator.NIGHTTIME_COLOR;
            case ASTRONOMICAL_SUNSET:
            case NAUTICAL_SUNRISE:
                return SolarDecorator.ASTRONOMICAL_TWILIGHT_COLOR;
            case NAUTICAL_SUNSET:
            case CIVIL_SUNRISE:
                return SolarDecorator.NAUTICAL_TWILIGHT_COLOR;
            case CIVIL_SUNSET:
            case OFFICIAL_SUNRISE:
                return SolarDecorator.CIVIL_TWILIGHT_COLOR;
            case OFFICIAL_SUNSET:
                return SolarDecorator.DAYTIME_COLOR;
            default:
                throw new IllegalStateException(event.name());
        }
    }

    private static Color markerColor(SolarDecorator.Event event) {
        switch (event) {
            case ASTRONIMICAL_SUNRISE:
                return SolarDecorator.NAUTICAL_TWILIGHT_COLOR;
            case ASTRONOMICAL_SUNSET:
            case NAUTICAL_SUNRISE:
            case NAUTICAL_SUNSET:
            case CIVIL_SUNRISE:
            case CIVIL_SUNSET:
            case OFFICIAL_SUNRISE:
            case OFFICIAL_SUNSET:
                return SolarDecorator.NIGHTTIME_COLOR;
            default:
                throw new IllegalStateException(event.name());
        }
    }

    private static Color handColor(SolarDecorator.Event event) {
        switch (event) {
            case ASTRONIMICAL_SUNRISE:
                return BRIGHT_HAND_COLOR;
            case ASTRONOMICAL_SUNSET:
            case NAUTICAL_SUNRISE:
            case NAUTICAL_SUNSET:
            case CIVIL_SUNRISE:
            case CIVIL_SUNSET:
            case OFFICIAL_SUNRISE:
            case OFFICIAL_SUNSET:
                return DARK_HAND_COLOR;
            default:
                throw new IllegalStateException(event.name());
        }
    }

    private LocalDateTime calendarLocalDateTime() {
        return Instant.ofEpochMilli(calendar.getTimeInMillis()).atZone(calendar.getTimeZone().toZoneId()).toLocalDateTime();
    }

    /**
     * This method is called from within the constructor to     * initialize the form.
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
    private final CalendarFormatter formatter;
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
    private final SimpleValueRing hourRing;
    private final SimpleNeedle hourHand;
    private final SimpleNeedle minuteHand;
    private final SimpleNeedle secondHand;
    private final boolean componentsInitialized;

    private SolarDecorator solarDecorator;

    private static final int CLOCK_RADIUS = 50;
    private static final int HOUR_RING_RADIUS = 37;
    private static final int DAYLIGHT_RING_RADIUS = 25;
    private static final int CLOCK_HAND_CAP = BasicStroke.CAP_ROUND;
    private static final int CLOCK_HAND_JOIN = BasicStroke.JOIN_ROUND;

    private static final Color DARK_HAND_COLOR = Color.BLACK;
    private static final Color BRIGHT_HAND_COLOR = Color.YELLOW.darker();

}
