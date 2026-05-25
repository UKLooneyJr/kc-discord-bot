package com.kelvinconnect.discord;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.junit.Test;

public class DiscordUtilsTest {

    @Test
    public void singleName() {
        Message message = mockMessage("Joe");
        String shortName = DiscordUtils.getAuthorShortUserName(message);
        assertEquals("Joe", shortName);
    }

    @Test
    public void multipleNames() {
        Message message = mockMessage("Joe Bloggs");
        String shortName = DiscordUtils.getAuthorShortUserName(message);
        assertEquals("Joe", shortName);
    }

    private Message mockMessage(String displayName) {
        Message message = mock(Message.class);
        Member member = mock(Member.class);
        when(message.getMember()).thenReturn(member);
        when(member.getEffectiveName()).thenReturn(displayName);
        return message;
    }
}
