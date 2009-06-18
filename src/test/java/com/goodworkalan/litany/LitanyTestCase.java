/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.litany;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.goodworkalan.litany.Litany;

import junit.framework.TestCase;

public class LitanyTestCase
extends TestCase
{
    public void testOneLine()
    throws IOException
    {
        String csv = "Hello,World!";
        Iterator<List<String>> iterator = new Litany(new StringReader(csv)).iterator();
        assertTrue(iterator.hasNext());
        List<String> line = iterator.next();
        assertEquals("Hello", line.get(0));
        assertEquals("World!", line.get(1));
        assertFalse(iterator.hasNext());
    }

    public void testTwoLines()
    throws IOException
    {
        String csv = "Hello,World!\nOne,Two,Three";
        Iterator<List<String>> iterator = new Litany(new StringReader(csv)).iterator();
        assertTrue(iterator.hasNext());
        List<String> line = iterator.next();
        assertEquals("Hello", line.get(0));
        assertEquals("World!", line.get(1));
        assertTrue(iterator.hasNext());
        line = iterator.next();
        assertEquals("One", line.get(0));
        assertEquals("Two", line.get(1));
        assertEquals("Three", line.get(2));
        assertFalse(iterator.hasNext());
    }

    public void testCommaAtEndOfLine()
    throws IOException
    {
        String csv = "Barbara_Foundas,Algeirs_Council_of_Neighborhood_Presidents,representative,4,,,,,,,,,,,,,,,,\nOne,Two,Three";
        Iterator<List<String>> iterator = new Litany(new StringReader(csv)).iterator();
        assertTrue(iterator.hasNext());
        List<String> line = iterator.next();
        assertEquals("Barbara_Foundas", line.get(0));
        assertEquals("Algeirs_Council_of_Neighborhood_Presidents", line.get(1));
        assertTrue(iterator.hasNext());
        line = iterator.next();
        assertEquals("One", line.get(0));
        assertEquals("Two", line.get(1));
        assertEquals("Three", line.get(2));
        assertFalse(iterator.hasNext());
    }
    
    public void testLine()
    {
        List<String> listOfStrings = new ArrayList<String>();
        
        listOfStrings.add("Hello");
        listOfStrings.add("I said, \"Parsley.\"");
        listOfStrings.add("I don't like to complain\nbut...");
        listOfStrings.add("C:\\WINNT");
        
        assertEquals("Hello,\"I said, \"\"Parsley.\"\"\",\"I don't like to complain\r\nbut...\",C:\\WINNT\r\n", Litany.line(listOfStrings.toArray()));
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */