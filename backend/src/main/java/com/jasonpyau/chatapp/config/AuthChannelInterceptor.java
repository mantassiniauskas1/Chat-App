package com.jasonpyau.chatapp.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.jasonpyau.chatapp.entity.GroupChat;
import com.jasonpyau.chatapp.entity.User;
import com.jasonpyau.chatapp.service.GroupChatService;
import com.jasonpyau.chatapp.service.RateLimitService;
import com.jasonpyau.chatapp.service.UserService;
import com.jasonpyau.chatapp.service.RateLimitService.Token;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    public UserService userService;

    @Autowired
    public GroupChatService groupChatService;

    public AuthChannelInterceptor(UserService userService, GroupChatService groupChatService) {

    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Check if accessor is null
        if (accessor == null) {
            // Handle the case where accessor is null (log an error, return null, etc.)
            return null;
        }

        StompCommand cmd = accessor.getCommand();

        if (cmd == StompCommand.SUBSCRIBE) {
            Long id = null;
            String destination = accessor.getDestination();

            // Check if destination is null
            if (destination == null) {
                // Handle the case where destination is null (log an error, return null, etc.)
                return null;
            }

            if (destination.startsWith("/topic/groupchat/")) {
                id = parseGroupId(destination);
            }

            if (id != null) {
                User user = userService.getUserFromWebSocket(accessor.getUser());
                Optional<GroupChat> optional = groupChatService.findById(id);

                if (optional.isEmpty() || !optional.get().getUsers().contains(user)) {
                    return null;
                }

                RateLimitService.RateLimiter.rateLimit(user, Token.BIG_TOKEN);
            }
        }

        return message;
    }

    private Long parseGroupId(String destination) {
        try {
            return Long.valueOf(destination.split("/")[3]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle the case where parsing fails (log an error, return null, etc.)
            return null;
        }
    }
}
