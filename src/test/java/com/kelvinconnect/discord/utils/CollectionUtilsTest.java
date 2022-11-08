package com.kelvinconnect.discord.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.kelvinconnect.discord.utils.CollectionUtils.listStartsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionUtilsTest {


    @Test
    public void test_listStartsWith() {
        List<String> list = new ArrayList<>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");

        assertTrue(listStartsWith(list, "apple"));
        assertTrue(listStartsWith(list, "apple", "banana"));
        assertTrue(listStartsWith(list, "apple", "banana", "cherry"));

        assertFalse(listStartsWith(list, "a"));
        assertFalse(listStartsWith(list, "banana"));
        assertFalse(listStartsWith(list, "apple", "cherry"));
        assertFalse(listStartsWith(list, "apple", "banana", "cherry", "deer"));
    }
}
