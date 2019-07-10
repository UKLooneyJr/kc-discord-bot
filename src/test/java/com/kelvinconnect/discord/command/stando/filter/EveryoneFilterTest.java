package com.kelvinconnect.discord.command.stando.filter;

import de.btobastian.javacord.entities.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EveryoneFilterTest {

    private static final String MOCK_ID = "123456789012345678";
    private static final String MOCK_ID_WRAPPED = "<@!" + MOCK_ID + ">";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Message message;
    private StandoFilter everyoneFilter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        everyoneFilter = new EveryoneFilter();
        when(message.getAuthor().getIdAsString()).thenReturn(MOCK_ID);
    }

    @Test
    public void testHere() {
        String result = everyoneFilter.filterWithMessage("prefix @here suffix", message);
        assertEquals("prefix " + MOCK_ID_WRAPPED + " suffix", result);
    }

    @Test
    public void testEveryone() {
        String result = everyoneFilter.filterWithMessage("prefix @everyone suffix", message);
        assertEquals("prefix " + MOCK_ID_WRAPPED + " suffix", result);
    }

    @Test
    public void testUser() {
        String result = everyoneFilter.filterWithMessage("prefix <@!098765432112345678> suffix", message);
        assertEquals("prefix " + MOCK_ID_WRAPPED + " suffix", result);
    }

    @Test
    public void testMulti() {
        String result = everyoneFilter.filterWithMessage(
                "prefix @here @everyone <@!098765432112345678> suffix", message);
        assertEquals("prefix " + MOCK_ID_WRAPPED + " " + MOCK_ID_WRAPPED +
                " " + MOCK_ID_WRAPPED + " suffix", result);
    }

    @Test
    public void testHereNoMessage() {
        String result = everyoneFilter.filter("prefix @here suffix");
        assertEquals("prefix here suffix", result);
    }

    @Test
    public void testEveryoneNoMessage() {
        String result = everyoneFilter.filter("prefix @everyone suffix");
        assertEquals("prefix everyone suffix", result);
    }

}