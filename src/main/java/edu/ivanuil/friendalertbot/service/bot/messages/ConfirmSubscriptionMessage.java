package edu.ivanuil.friendalertbot.service.bot.messages;

import edu.ivanuil.friendalertbot.entity.ChatEntity;
import edu.ivanuil.friendalertbot.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmSubscriptionMessage implements BotMessage {

    private final ChatEntity chat;
    private final SubscriptionEntity sub;

    @Override
    public ChatEntity getChat() {
        return chat;
    }

    @Override
    public String toString() {
        return String.format("Confirmed subscription to: %s", sub.getSubscriptionUsername());
    }
}
