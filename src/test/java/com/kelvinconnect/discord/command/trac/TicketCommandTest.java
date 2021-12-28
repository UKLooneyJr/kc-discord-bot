package com.kelvinconnect.discord.command.trac;

import static org.junit.Assert.*;

import org.junit.Test;

/** Created by Adam on 15/03/2017. */
public class TicketCommandTest {

    // @Test
    // public void standardTicketNumber() {
    // TicketCommand command = new TicketCommand();
    // String input = "1234";
    // String output = command.onTicketCommand(new String[] { input });
    // assertEquals(output, "http://trac/KC/ticket/1234");
    // }

    // @Test
    // public void ticketNumberWithHash() {
    // TicketCommand command = new TicketCommand();
    // String input = "#1234";
    // String output = command.onTicketCommand(new String[] { input });
    // assertEquals(output, "http://trac/KC/ticket/1234");
    // }

    @Test
    public void tooManyArguments() {
        TicketCommand command = new TicketCommand();
        String input1 = "1234";
        String input2 = "5678";
        String output = command.onTicketCommand(new String[] {input1, input2}, null);
        assertEquals(output, "Incorrect number of arguments!");
    }

    @Test
    public void noArguments() {
        TicketCommand command = new TicketCommand();
        String output = command.onTicketCommand(new String[] {}, null);
        assertEquals(output, "Incorrect number of arguments!");
    }

    @Test
    public void invalidArgument() {
        TicketCommand command = new TicketCommand();
        String input = "one,two,three,four";
        String output = command.onTicketCommand(new String[] {input}, null);
        assertEquals(output, input + " is not a valid ticket number.");
    }
}
