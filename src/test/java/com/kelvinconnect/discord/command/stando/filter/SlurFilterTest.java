package com.kelvinconnect.discord.command.stando.filter;

import de.btobastian.javacord.entities.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SlurFilterTest {

    private static final String BEER_EMOJI = "\uD83C\uDF7A ";

    @Mock
    private Message message;
    private StandoFilter slurFilter;

    private static String repeat(String s, int n) {
        return new String(new char[n]).replace("\0", s);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        slurFilter = new SlurFilter(1, 1);
    }

    private void setMessage(String m) {
        when(message.getContent()).thenReturn(m);
    }

    @Test
    public void spaceNoSlur() {
        setMessage("no beer");
        String result = slurFilter.filterWithMessage("Hello there", message);
        assertEquals("Hello there", result);
    }

    @Test
    public void spaceSlurAfter7() {
        setMessage(repeat(BEER_EMOJI, 7));
        String result = slurFilter.filterWithMessage("Hello there", message);
        assertEquals("Hello ...hic! there", result);
    }

    @Test
    public void spaceSlurBefore7() {
        setMessage(repeat(BEER_EMOJI, 6));
        String result = slurFilter.filterWithMessage("Hello there", message);
        assertEquals("Hello there", result);
    }

    @Test
    public void sSlurAfter7() {
        setMessage(repeat(BEER_EMOJI, 6));
        String result = slurFilter.filterWithMessage("she_sells_seashells_by_the_seashore", message);
        assertEquals("she_sells_seashells_by_the_seashore", result);
    }
}