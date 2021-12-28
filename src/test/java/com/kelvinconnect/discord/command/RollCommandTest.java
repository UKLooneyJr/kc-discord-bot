package com.kelvinconnect.discord.command;

import static org.junit.Assert.*;

import org.junit.Test;

public class RollCommandTest {

    private static final int TEST_REPETITIONS = 10000;

    @Test
    public void rollSingle() {
        RollCommand cmd = new RollCommand();
        for (int i = 0; i < TEST_REPETITIONS; ++i) {
            String result = cmd.onRollCommand(new String[] {});
            int intResult = Integer.parseInt(result);
            assertTrue(intResult > 0 && intResult <= 6);
        }
    }

    @Test
    public void rollSingleSpecified() {
        RollCommand cmd = new RollCommand();
        // We're pretty much guaranteed to have at least one result be above 6, knowing this we can
        // assume as best we
        // can
        // that we are making use of the argument provided to the roll command
        boolean resultAboveSix = false;
        for (int i = 0; i < TEST_REPETITIONS; ++i) {
            String result = cmd.onRollCommand(new String[] {"1000"});
            int intResult = Integer.parseInt(result);
            if (intResult > 6) resultAboveSix = true;
            assertTrue(intResult > 0 && intResult <= 1000);
        }
        assertTrue(resultAboveSix);
    }

    @Test
    public void rollRange() {
        RollCommand cmd = new RollCommand();
        for (int i = 0; i < TEST_REPETITIONS; ++i) {
            String result = cmd.onRollCommand(new String[] {"10-20"});
            int intResult = Integer.parseInt(result);
            assertTrue(intResult >= 10 && intResult <= 20);
        }
    }

    @Test
    public void rollMultiple() {
        RollCommand cmd = new RollCommand();
        for (int i = 0; i < TEST_REPETITIONS; ++i) {
            String result = cmd.onRollCommand(new String[] {"10d20"});
            int intResult = Integer.parseInt(result.substring(0, result.indexOf(' ')));
            assertTrue(intResult >= 20 && intResult <= 10 * 20);
            // (10 - 1) commas means there are 10 comma separated values listed
            assertEquals(result.length() - result.replace(",", "").length(), 10 - 1);
        }
    }
}
