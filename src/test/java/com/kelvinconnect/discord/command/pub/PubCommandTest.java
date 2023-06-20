package com.kelvinconnect.discord.command.pub;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.junit.Test;

public class PubCommandTest {

    private Message mockMessage() {
        Message message = mock(Message.class);
        MessageAuthor author = mock(MessageAuthor.class);
        when(message.getAuthor()).thenReturn(author);
        when(author.getIdAsString()).thenReturn("1234");
        when(author.getDisplayName()).thenReturn("Roy Helm");
        return message;
    }

    private String invokeVote(PubCommand command, String... args) {
        return command.onVoteCommand(args, mockMessage());
    }

    private String invokePub(PubCommand command, String... args) {
        return command.onPubCommand(args, mockMessage());
    }

    @Test
    public void voteCommand() {
        PubCommand command = new PubCommand();

        String response = invokeVote(command, "Brass", "Monkey");
        assertEquals("Thanks for voting Roy.", response);

        String resultResponse = invokePub(command, "results");
        assertEquals("The results are in!\n\nBrass Monkey has 1 vote\n", resultResponse);
    }

    @Test
    public void pubVoteCommand() {
        PubCommand command = new PubCommand();

        String response = invokePub(command, "vote", "Brass", "Monkey");
        assertEquals("Thanks for voting Roy.", response);

        String resultResponse = invokePub(command, "results");
        assertEquals("The results are in!\n\nBrass Monkey has 1 vote\n", resultResponse);
    }
}
