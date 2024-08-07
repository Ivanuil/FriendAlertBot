package edu.ivanuil.friendalertbot.service.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import edu.ivanuil.friendalertbot.entity.ChatEntity;
import edu.ivanuil.friendalertbot.entity.SubscriptionEntity;
import edu.ivanuil.friendalertbot.entity.VisitorEntity;
import edu.ivanuil.friendalertbot.repository.ChatRepository;
import edu.ivanuil.friendalertbot.repository.SubscriptionRepository;
import edu.ivanuil.friendalertbot.entity.ChatState;
import edu.ivanuil.friendalertbot.repository.VisitorRepository;
import edu.ivanuil.friendalertbot.service.School21PlatformBinding;
import edu.ivanuil.friendalertbot.service.bot.messages.BotMessage;
import edu.ivanuil.friendalertbot.service.bot.messages.ConfirmSubscriptionMessage;
import edu.ivanuil.friendalertbot.service.bot.messages.WelcomeToCampusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import static edu.ivanuil.friendalertbot.util.TimeFormatUtils.formatInterval;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotService {

    private final ChatRepository chatRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final School21PlatformBinding school21PlatformBinding;
    private final VisitorRepository visitorRepository;

    @Value("${telegram.bot.token}")
    private String token;
    private static int lastReadUpdateId = 0;
    private Timestamp lastMessageCheckTime = new Timestamp(System.currentTimeMillis());

    private static final String COMMANDS_LIST_MESSAGE = """
            Commands:
                /subscribe [username1] [username2] ... - subscribe to user entering and leaving campus
                /subscribeAll - subscribe to all users currently in campus entering and leaving campus
                (experimental feature)
            """;

    private TelegramBot bot;

    public void refreshUpdates() {
        if (bot == null)
            bot = new TelegramBot(token);

        Timestamp messageCheckTime = new Timestamp(System.currentTimeMillis());
        log.info("Checking new messages, time since last check {}",
                formatInterval(messageCheckTime.getTime() - lastMessageCheckTime.getTime()));
        lastMessageCheckTime = messageCheckTime;

        var updatesResponse = bot.execute(new GetUpdates().offset(lastReadUpdateId + 1));
        var updates = updatesResponse.updates();
        for (var update : updates) {
            lastReadUpdateId = update.updateId();
            processMessage(update.message());
        }
    }

    private void processMessage(final Message message) {
        var chatId = message.chat().id();
        if (chatRepository.existsById(chatId)) {
            var chat = chatRepository.findById(chatId).get();

            if (chat.getState() == ChatState.AWAITING_PLATFORM_USERNAME)
                processPlatformUsername(chat, message);
            else if (chat.getState() == ChatState.RUNNING)
                processCommand(chat, message);
        } else {
            processFirstMessage(chatId, message);
        }
    }

    private void processFirstMessage(final Long chatId, final Message message) {
        if (!message.text().equals("/start"))
            return;
        bot.execute(new SendMessage(chatId,
                "What's you username on the platform (abc@student.21-school.ru format)?"));
        var chat = new ChatEntity(chatId, message.chat().username(),
                null, ChatState.AWAITING_PLATFORM_USERNAME, null);
        chatRepository.save(chat);
    }

    private void processPlatformUsername(final ChatEntity chat, final Message message) {
        if (checkIfMatchesUsername(message.text())) {
            if (school21PlatformBinding.checkIfUserExists(message.text())) {
                chat.setState(ChatState.RUNNING);
                chat.setPlatformUsername(message.text());
                chatRepository.save(chat);
                bot.execute(new SendMessage(chat.getId(),
                        "All good!"));
                bot.execute(new SendMessage(chat.getId(),
                        COMMANDS_LIST_MESSAGE));
            } else {
                bot.execute(new SendMessage(chat.getId(),
                        "User doesn't exist. Try again"));
            }
        } else {
            bot.execute(new SendMessage(chat.getId(),
                    "Wrong format. Try again"));
        }
    }

    private boolean checkIfMatchesUsername(final String username) {
        if (!username.endsWith("@student.21-school.ru"))
            return false;
        if (username.split("@").length != 2)
            return false;
        String login = username.split("@")[0];
        return login.length() == 8 && login.matches("[a-zA-Z]+");
    }

    private void processCommand(final ChatEntity chat, final Message message) {
        if (message.text().startsWith("/subscribe")) {
            var tokens = message.text().split(" ");
            subscribeToUsers(chat, new LinkedList<>(Arrays.asList(tokens).subList(1, tokens.length)));
        }
    }

    private void subscribeToUsers(final ChatEntity chat, final List<String> users) {
        for (var user : users)
            subscribeToUser(chat, user);
    }

    private void subscribeToUser(final ChatEntity chat, final String user) {
        if (!checkIfMatchesUsername(user)) {
            bot.execute(new SendMessage(chat.getId(),
                    String.format("Invalid username format, should be abc@student.21-school.ru (%s)", user)));
            return;
        }
        if (!school21PlatformBinding.checkIfUserExists(user)) {
            bot.execute(new SendMessage(chat.getId(),
                    String.format("User doesn't exist. Try again (%s)", user)));
            return;
        }
        var subscription = new SubscriptionEntity(null, chat, user);
        subscriptionRepository.save(subscription);
        sendMessage(new ConfirmSubscriptionMessage(chat, subscription));
    }

    public void sendMessage(final BotMessage message) {
        if (bot == null)
            bot = new TelegramBot(token);
        bot.execute(new SendMessage(message.getChat().getId(), message.toString()));
    }

    public void sendMessages(final List<BotMessage> messages) {
        for (var message : messages)
            sendMessage(message);
    }

    public void sendGreetings(final List<VisitorEntity> visitors) {
        List<BotMessage> messages = new LinkedList<>();
        for (var visitor : visitors) {
            var chatOpt = chatRepository.findByPlatformUsername(visitor.getLogin());
            if (chatOpt.isEmpty())
                continue;

            messages.add(new WelcomeToCampusMessage(chatOpt.get(),
                    getFriendsInCampusList(chatOpt.get().getTelegramUsername())));
        }

        log.info("Sending greetings for {} visitors", messages.size());
        sendMessages(messages);
    }

    private List<VisitorEntity> getFriendsInCampusList(final String telegramUsername) {
        List<VisitorEntity> res = new LinkedList<>();
        var subs = subscriptionRepository.findAllBySubscriberChat_TelegramUsername(telegramUsername);
        for (var sub : subs) {
            var friendOpt = visitorRepository.findByLogin(sub.getSubscriptionUsername());
            if (friendOpt.isEmpty())
                continue;
            res.add(friendOpt.get());
        }
        return res;
    }

}
