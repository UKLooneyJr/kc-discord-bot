package com.kelvinconnect.discord.command.pub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PubCommandTest {

    @Mock private MessageReceivedEvent event;
    @Mock private Message message;
    @Mock private User author;
    @Mock private Member member;
    @Mock private MessageChannelUnion channel;
    @Mock private MessageCreateAction messageCreateAction;

    private PubCommand command;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);
        when(event.getChannel()).thenReturn(channel);
        when(message.getAuthor()).thenReturn(author);
        when(message.getMember()).thenReturn(member);
        when(author.getId()).thenReturn("1234");
        when(member.getEffectiveName()).thenReturn("Roy Helm");
        when(channel.sendMessage(anyString())).thenReturn(messageCreateAction);
        command = new PubCommand();
    }

    private String executeAndCapture(String contentRaw, String... args) {
        when(message.getContentRaw()).thenReturn(contentRaw);
        command.execute(event, args);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(channel, atLeastOnce()).sendMessage(captor.capture());
        clearInvocations(channel);
        return captor.getValue();
    }

    @Test
    public void voteCommand() {
        String response = executeAndCapture("!vote Brass Monkey", "Brass", "Monkey");
        assertEquals("Thanks for voting Roy.", response);

        String resultResponse = executeAndCapture("!pub results", "results");
        assertEquals("The results are in!\n\nBrass Monkey has 1 vote\n", resultResponse);
    }

    @Test
    public void pubVoteCommand() {
        String response = executeAndCapture("!pub vote Brass Monkey", "vote", "Brass", "Monkey");
        assertEquals("Thanks for voting Roy.", response);

        String resultResponse = executeAndCapture("!pub results", "results");
        assertEquals("The results are in!\n\nBrass Monkey has 1 vote\n", resultResponse);
    }
}
