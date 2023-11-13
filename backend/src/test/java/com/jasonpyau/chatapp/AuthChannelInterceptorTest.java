package com.jasonpyau.chatapp;
import static org.mockito.Mockito.*;

import com.jasonpyau.chatapp.config.AuthChannelInterceptor;
import com.jasonpyau.chatapp.service.GroupChatService;
import com.jasonpyau.chatapp.service.UserService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class AuthChannelInterceptorTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private GroupChatService groupChatServiceMock;

    // The class under test, with mocked dependencies injected
    @InjectMocks
    private AuthChannelInterceptor interceptor;

    @Test
    public void testPreSendSubscribeToValidGroupChat() {
        AuthChannelInterceptor interceptor = new AuthChannelInterceptor(userServiceMock, groupChatServiceMock);

        StompHeaderAccessor accessorMock = mock(StompHeaderAccessor.class);
        when(accessorMock.getCommand()).thenReturn(StompCommand.SUBSCRIBE);
        when(accessorMock.getDestination()).thenReturn("/topic/groupchat/1");

        Message<?> messageMock = mock(Message.class);
        MessageChannel channelMock = mock(MessageChannel.class);

        interceptor.preSend(messageMock, channelMock);

        // Add your assertions based on the expected behavior
        // For example, verify that userService and groupChatService methods are called as expected
        verify(userServiceMock, times(1)).getUserFromWebSocket(any());
        verify(groupChatServiceMock, times(1)).findById(anyLong());
    }

    @Test
    public void testPreSendSubscribeToInvalidGroupChat() {
        AuthChannelInterceptor interceptor = new AuthChannelInterceptor(userServiceMock, groupChatServiceMock);
        StompHeaderAccessor accessorMock = mock(StompHeaderAccessor.class);
        when(accessorMock.getCommand()).thenReturn(StompCommand.SUBSCRIBE);
        when(accessorMock.getDestination()).thenReturn("/topic/groupchat/2");

        Message<?> messageMock = mock(Message.class);
        MessageChannel channelMock = mock(MessageChannel.class);

        // Simulate the case where accessor is null
        when(MessageHeaderAccessor.getAccessor(messageMock, StompHeaderAccessor.class)).thenReturn(null);

        interceptor.preSend(messageMock, channelMock);

        // Add assertions for the specific behavior you expect in this case
        // For example, verify that userService and groupChatService methods are not called
        verify(userServiceMock, never()).getUserFromWebSocket(any());
        verify(groupChatServiceMock, never()).findById(anyLong());
    }
}
