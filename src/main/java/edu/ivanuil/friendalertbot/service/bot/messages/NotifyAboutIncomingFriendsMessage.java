package edu.ivanuil.friendalertbot.service.bot.messages;

import edu.ivanuil.friendalertbot.entity.ChatEntity;
import edu.ivanuil.friendalertbot.entity.VisitorEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NotifyAboutIncomingFriendsMessage implements BotMessage {

    private final ChatEntity chatEntity;

    private final List<VisitorEntity> incoming;
    private final List<String> leaving;

    @Override
    public ChatEntity getChat() {
        return chatEntity;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        if (!incoming.isEmpty()) {
            message.append("Some of your friends just came to campus:\n");
            for (var visitor : incoming)
                message.append(String.format("%s %s-%s\n",
                        visitor.getLogin(),
                        visitor.getCluster().getName(),
                        visitor.getRow()+visitor.getNumber()));
        }
        if (!leaving.isEmpty()) {
            message.append("Some of your friends just left campus:\n");
            for (var visitor : leaving)
                message.append(String.format("%s\n", visitor));
        }

        return message.toString();
    }

}
