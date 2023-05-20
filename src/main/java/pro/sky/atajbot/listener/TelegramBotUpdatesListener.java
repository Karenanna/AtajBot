package pro.sky.atajbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pro.sky.atajbot.entity.NotificationTask;
import pro.sky.atajbot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.parse;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\А-я+\\s]+)");

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
   private final TelegramBot telegramBot;
   private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      NotificationTaskService notificationTaskService) {

        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    @Override
    public int process(List<Update> updates) {
        try {
            updates.stream()
                    .filter(update -> update.message() != null)
                    .forEach(update -> {
                logger.info("Handles update: {}", update);
            Message message = update.message();
           Long chatId = message.chat().id();
           String text = message.text();

           if ("/start".equals(text)) {
             sendMessage(chatId, "Hello! I can help you");
           } else if (text != null) {
               Matcher matcher = pattern.matcher(text);
               if (matcher.find()) {
                   LocalDateTime dateTime = parse(matcher.group(1), dateTimeFormatter);
                   if (Objects.isNull(dateTime)) {
                       sendMessage(chatId, "Not find the date or time");
                       return;
                   }
                   String txt = matcher.group(2);
                   NotificationTask notificationTask = new NotificationTask();
                   notificationTask.setChatId(chatId);
                   notificationTask.setMessage(txt);
                   notificationTask.setNotificationDateTime(dateTime);
                   notificationTaskService.save(notificationTask);
                   sendMessage(chatId, "Very good!!!");
               }else {
                  sendMessage(chatId, "Format is not found");
               }
           }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
    @Nullable
    private LocalDateTime parse(String dateTime, DateTimeFormatter dateTimeFormatter) {
        try {
            return parse(dateTime, this.dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error: {}", sendResponse.description());
        }
    }
}
