package pl.coderslab.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pl.coderslab.entity.Event;
import pl.coderslab.entity.Notification;
import pl.coderslab.repository.EventRepository;
import pl.coderslab.repository.NotificationRepository;

@Service
@Slf4j
public class NotificationService {

    private final EventRepository eventRepository;
    private final NotificationRepository notificationRepository;

    private List<Event> todayEventList = new ArrayList<>();

    @Autowired
    public NotificationService(EventRepository eventRepository, NotificationRepository notificationRepository) {
        this.eventRepository = eventRepository;
        this.notificationRepository = notificationRepository;
    }

    /** Checks if any notifications should be generated 
     * every 5 minutes from 9-17 Monday to Friday 
     * and generates them
     */
    @Scheduled(cron = "0 */5 9-17 * * MON-FRI")
    public void checkIfGenerateNotification() {
        LocalDateTime now = LocalDateTime.now();
        Iterator<Event> iterator = todayEventList.iterator();

        while (iterator.hasNext()) {
            Event event = iterator.next();

            // delete event from list if it's in the past
            if (now.isAfter(event.getTime())) {
                notificationRepository.findByEvent(event).forEach(notificationRepository::delete);
                iterator.remove();
            } 
            // generate notification if event is in 1 hour
            else if (now.plusHours(1).isAfter(event.getTime()) && shouldGenerateNotification(event, 3)) {
                generateNotification(event);
            } 
            // generate notification if event is in 2 hours
            else if (now.plusHours(2).isAfter(event.getTime()) && shouldGenerateNotification(event, 2)) {
                generateNotification(event);
            }
        }
    }

    /** Checks if the event has fewer notifications than the max limit
     * 
     * @param event
     * @param maxNotifications
     * @return true if notification should be generated, false otherwise
     */
    private boolean shouldGenerateNotification(Event event, int maxNotifications) {
        List<Notification> notificationList = notificationRepository.findByEvent(event);
        return notificationList.size() < maxNotifications;
    }

    /** Gets list of today's events at 8.30 from Monday to Friday 
     * and generates first notifications
     */
    @Scheduled(cron = "0 30 8 * * MON-FRI")
    public void generateMorningNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        todayEventList = eventRepository.findByTimeBetween(now, endOfDay);

        // generate notifications for all today's events
        todayEventList.forEach(this::generateNotification);
    }

    /** Generates and saves to db new Notification for given Event
     * 
     * @param event
     */
    private void generateNotification(Event event) {
        Notification notification = new Notification();
        notification.setCreated(LocalDateTime.now());
        notification.setEvent(event);
        notification.setWasRead(false);
        notification.setUser(event.getUser());
        notification.setContent(
                "You have " + event.getType() + " with " + event.getClient().getName() + " at " + event.getTime()
                        + ". Topic: " + event.getTitle());
        notificationRepository.save(notification);
    }
}
