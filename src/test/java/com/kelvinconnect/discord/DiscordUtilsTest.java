package com.kelvinconnect.discord;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
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
        MessageAuthor messageAuthor = mock(MessageAuthor.class);
        when(message.getAuthor()).thenReturn(messageAuthor);
        when(messageAuthor.getDisplayName()).thenReturn(displayName);
        return message;
    }

    @Test
    public void listStartsWith() {
        List<String> list = new ArrayList<>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");

        assertTrue(DiscordUtils.listStartsWith(list, "apple"));
        assertTrue(DiscordUtils.listStartsWith(list, "apple", "banana"));
        assertTrue(DiscordUtils.listStartsWith(list, "apple", "banana", "cherry"));

        assertFalse(DiscordUtils.listStartsWith(list, "a"));
        assertFalse(DiscordUtils.listStartsWith(list, "banana"));
        assertFalse(DiscordUtils.listStartsWith(list, "apple", "cherry"));
        assertFalse(DiscordUtils.listStartsWith(list, "apple", "banana", "cherry", "deer"));
    }
}
