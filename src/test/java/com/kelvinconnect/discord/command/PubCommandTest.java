package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.VotingBooth;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAuthor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PubCommandTest {

    private Message mockMessage() {
        Message message = mock(Message.class);
        MessageAuthor author = mock(MessageAuthor.class);
        when(message.getAuthor()).thenReturn(author);
        when(author.getIdAsString()).thenReturn("1234");
        when(author.getDisplayName()).thenReturn("Roy Helm");
        return message;
    }

    @Test
    public void voteCommand() {
        PubCommand command = new PubCommand(new VotingBooth());

        String[] args = { "Brass", "Monkey" };
        Message message = mockMessage();

        String response = command.onVoteCommand(args, message);
        assertEquals("Thanks for voting Roy.", response);
    }

    @Test
    public void pubVoteCommand() {
        PubCommand command = new PubCommand(new VotingBooth());

        String[] args = { "vote", "Brass", "Monkey" };
        Message message = mockMessage();

        String response = command.onPubCommand(args, message);
        assertEquals("Thanks for voting Roy.", response);
    }

}