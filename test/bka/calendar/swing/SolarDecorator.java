/*
** © Bart Kampers
*/

package bka.calendar.swing;

import bka.calendar.events.*;
import bka.swing.clock.*;
import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;


public class SolarDecorator {

    public static final Color DAYTIME_COLOR = new Color(0xb1d0fd);
    public static final Color CIVIL_TWILIGHT_COLOR = new Color(0x99b3e4);
    public static final Color NAUTICAL_TWILIGHT_COLOR = new Color(0x7c96c9);
    public static final Color ASTRONOMICAL_TWILIGHT_COLOR = new Color(0x6c82ba);
    public static final Color NIGHTTIME_COLOR = new Color(0x4b5b97);

    public enum Event {
        ASTRONIMICAL_SUNRISE(Zenith.ASTRONOMICAL),
        NAUTICAL_SUNRISE(Zenith.NAUTICAL),
        CIVIL_SUNRISE(Zenith.CIVIL),
        OFFICIAL_SUNRISE(Zenith.OFFICIAL),
        OFFICIAL_SUNSET(Zenith.OFFICIAL),
        CIVIL_SUNSET(Zenith.CIVIL),
        NAUTICAL_SUNSET(Zenith.NAUTICAL),
        ASTRONOMICAL_SUNSET(Zenith.ASTRONOMICAL);

        private Event(double zenith) {
            this.zenith = zenith;
        }

        public boolean isSunrise() {
            switch (this) {
                case ASTRONIMICAL_SUNRISE:
                case NAUTICAL_SUNRISE:
                case CIVIL_SUNRISE:
                case OFFICIAL_SUNRISE:
                    return true;
                case OFFICIAL_SUNSET:
                case CIVIL_SUNSET:
                case NAUTICAL_SUNSET:
                case ASTRONOMICAL_SUNSET:
                    return false;
            }
            throw new IllegalStateException(name());
        }

        private final double zenith;
    }

    public SolarDecorator(SolarEventCalculator solarEventCalculator) {
        this.solarEventCalculator = Objects.requireNonNull(solarEventCalculator);
    }

    public void initialize(LocalDateTime dateTime) {
        updateEvents(dateTime);
        initializeArcs();
        updateArcs();
        removePastEvents(dateTime);
        logUpcomingEvent();
    }

    private void initializeArcs() {
        Arrays.stream(Event.values()).forEach(event -> arcs.put(event, new ArcRing.Arc(0.0, 0.0, arcColor(event), ARC_STROKE)));
    }

    private void removePastEvents(LocalDateTime dateTime) {
        while (!events.isEmpty() && events.firstKey().isBefore(dateTime)) {
            events.remove(events.firstKey());
        }
        ensureEventsAvailable(dateTime);
    }

    public void update(LocalDateTime dateTime) {
        if (arcDate == null) {
            throw new IllegalStateException("Not initialized");
        }
        if (events.firstKey().isBefore(dateTime)) {
            events.remove(events.firstKey());
            ensureEventsAvailable(dateTime);
            logUpcomingEvent();
        }
        LocalDate firstEventDate = events.firstKey().toLocalDate();
        if (arcDate.isBefore(firstEventDate) && firstEventDate.equals(dateTime.toLocalDate())) {
            updateArcs();
        }
    }

    private void ensureEventsAvailable(LocalDateTime dateTime) {
        if (events.isEmpty()) {
            updateEvents(dateTime.plusDays(1));
        }
    }

    private void logUpcomingEvent() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Next event: {0} @ {1}", new Object[]{ events.get(events.firstKey()), events.firstKey() });
    }

    private void updateEvents(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        while (events.isEmpty()) {
            for (Event event : Event.values()) {
                LocalDateTime eventDateTime = solarEventDateTime(event, date);
                if (eventDateTime != null) {
                    events.put(eventDateTime, event);
                }
            }
            date = date.plusDays(1);
        }
    }

    private void updateArcs() {
        if (events.size() > 1) {
            updateMultipleArcs();
        }
        else {
            updateSingleArc();
        }
        hideNoncurrentArcs();
        arcDate = events.firstKey().toLocalDate();
    }

    private void updateMultipleArcs() {
        LocalDateTime previousKey = events.lastKey();
        for (Map.Entry<LocalDateTime, Event> entry : events.entrySet()) {
            ArcRing.Arc arc = arcs.get(entry.getValue());
            arc.setStart(decimalHour(previousKey));
            arc.setEnd(decimalHour(entry.getKey()));
            Logger.getLogger(getClass().getName()).log(Level.INFO, "{0}: {1} - {2}", new Object[]{ entry.getValue(), previousKey, entry.getKey() });
            previousKey = entry.getKey();
        }
    }

    private static double decimalHour(LocalDateTime dateTime) {
        return dateTime.getHour() + dateTime.getMinute() / 60.0;
    }

    private void updateSingleArc() {
        Event event = events.get(events.firstKey());
        ArcRing.Arc arc = arcs.get(event);
        arc.setStart(0.0);
        arc.setEnd(24.0);
        Logger.getLogger(getClass().getName()).log(Level.INFO, "{0}: All day", event);
    }

    private void hideNoncurrentArcs() {
        Arrays.stream(Event.values()).filter(event -> !events.containsValue(event)).forEach(event -> {
            ArcRing.Arc arc = arcs.get(event);
            arc.setStart(0.0);
            arc.setEnd(0.0);
        });
    }

    private static Color arcColor(Event event) {
        switch (event) {
            case ASTRONIMICAL_SUNRISE:
                return NIGHTTIME_COLOR;
            case ASTRONOMICAL_SUNSET:
            case NAUTICAL_SUNRISE:
                return ASTRONOMICAL_TWILIGHT_COLOR;
            case NAUTICAL_SUNSET:
            case CIVIL_SUNRISE:
                return NAUTICAL_TWILIGHT_COLOR;
            case CIVIL_SUNSET:
            case OFFICIAL_SUNRISE:
                return CIVIL_TWILIGHT_COLOR;
            case OFFICIAL_SUNSET:
                return DAYTIME_COLOR;
            default:
                throw new IllegalStateException(event.name());
        }
    }

    private LocalDateTime solarEventDateTime(Event event, final LocalDate date) {
        if (event.isSunrise()) {
            return solarEventCalculator.sunrise(event.zenith, date);
        }
        return solarEventCalculator.sunset(event.zenith, date);
    }

    public Collection<ArcRing.Arc> getArcs() {
        return arcs.values();
    }

    public Event upcomingEvent() {
        return events.firstEntry().getValue();
    }


    private final SolarEventCalculator solarEventCalculator;

    private final TreeMap<LocalDateTime, Event> events = new TreeMap<>();
    private final TreeMap<Event, ArcRing.Arc> arcs = new TreeMap<>();
    private LocalDate arcDate;

    private static final BasicStroke ARC_STROKE = new BasicStroke(4f);
}
