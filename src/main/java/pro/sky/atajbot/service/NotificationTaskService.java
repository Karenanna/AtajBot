package pro.sky.atajbot.service;

import org.springframework.stereotype.Service;
import pro.sky.atajbot.entity.NotificationTask;
import pro.sky.atajbot.repository.NotificationTaskRepository;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public void save(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }
}
