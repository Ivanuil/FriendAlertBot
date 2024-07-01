package edu.ivanuil.friendalertbot.scheduling;

import edu.ivanuil.friendalertbot.entity.SubscriptionEntity;
import edu.ivanuil.friendalertbot.entity.VisitorEntity;
import edu.ivanuil.friendalertbot.repository.SubscriptionRepository;
import edu.ivanuil.friendalertbot.service.bot.TelegramBotService;
import edu.ivanuil.friendalertbot.service.VisitorService;
import edu.ivanuil.friendalertbot.service.bot.messages.BotMessage;
import edu.ivanuil.friendalertbot.service.bot.messages.NotifyAboutIncomingFriendsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshVisitorsSchedule {

    private final VisitorService visitorService;
    private final TelegramBotService botService;

    private final SubscriptionRepository subscriptionRepository;

    @Value("${logging.entering-leaving-visitors-list}")
    private boolean logIncomingLeavingList;

    @Scheduled(fixedRate = 10000)
    public void refreshVisitorsAndSendNotifications() {
        var arr = visitorService.getIncomingAndLeavingVisitors();
        List<VisitorEntity> incomingVisitors = arr[0];
        List<VisitorEntity> leavingVisitors = arr[1];

        if (logIncomingLeavingList) {
            log.info("Incoming visitors: {}", incomingVisitors);
            log.info("Leaving visitors: {}", leavingVisitors);
        }

        // Getting all triggered subscriptions
        List<SubscriptionEntity> subscriptions = new LinkedList<>();
        for (VisitorEntity visitor : incomingVisitors)
            subscriptions.addAll(subscriptionRepository.findAllBySubscriptionUsername(visitor.getLogin()));
        for (VisitorEntity visitor : leavingVisitors)
            subscriptions.addAll(subscriptionRepository.findAllBySubscriptionUsername(visitor.getLogin()));

        // Creating a list of messages for subscriptions from entering and leaving visitors
        List<BotMessage> messages = new LinkedList<>();
        while (!subscriptions.isEmpty()) {
            var chat = subscriptions.get(0).getSubscriberChat();
            var subsForChat = subscriptions.stream().filter(s -> s.getSubscriberChat() == chat).toList();
            subscriptions.removeAll(subsForChat);
            List<VisitorEntity> incomingSubs = incomingVisitors.stream()
                    .filter(v -> subsForChat.stream()
                            .anyMatch(s -> s.getSubscriptionUsername().equals(v.getLogin()))).toList();
            List<String> leavingSubs = leavingVisitors.stream()
                    .map(VisitorEntity::getLogin)
                    .filter(login -> subsForChat.stream()
                            .anyMatch(s -> s.getSubscriptionUsername().equals(login))).toList();
            messages.add(new NotifyAboutIncomingFriendsMessage(chat, incomingSubs, leavingSubs));
        }

        log.info("Sending notifications to {} users", messages.size());
        botService.sendMessages(messages);
    }

}
