package edu.ivanuil.friendalertbot.service.bot.messages;

import edu.ivanuil.friendalertbot.entity.ChatEntity;
import edu.ivanuil.friendalertbot.entity.VisitorEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class WelcomeToCampusMessage implements BotMessage {

    private final ChatEntity chat;
    private final List<VisitorEntity> friends;

    @Override
    public ChatEntity getChat() {
        return chat;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Welcome to campus!\n");
        if (!friends.isEmpty())
            builder.append("Some of your friends are in campus now:\n");
        for (var friend : friends)
            builder.append(String.format("%s %s-%s%d", friend.getLogin(), friend.getCluster().getName(),
                    friend.getRow(), friend.getNumber()));
        return builder.toString();
    }

}
