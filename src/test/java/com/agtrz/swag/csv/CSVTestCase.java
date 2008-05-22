/* Copyright Alan Gutierrez 2006 */
package com.agtrz.swag.csv;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class CSVTestCase
extends TestCase
{
    private final static class ToArray
    implements CSV.Consumer
    {
        public final List<List<String>> listOfRecords = new ArrayList<List<String>>();
        
        public void line(List<String> listOfFields)
        {
            listOfRecords.add(new ArrayList<String>(listOfFields));
        }
    }

    public void testOneLine()
    throws IOException
    {
        ToArray toArray = new ToArray();
        String csv = "Hello,World!";
        new CSV(toArray).read(new StringReader(csv));
        assertEquals(1, toArray.listOfRecords.size());
        String[][] records = (String[][]) toArray.listOfRecords.toArray(new String[1][]);
        assertEquals("Hello", records[0][0]);
        assertEquals("World!", records[0][1]);
    }

    public void testTwoLines()
    throws IOException
    {
        ToArray toArray = new ToArray();
        String csv = "Hello,World!\nOne,Two,Three";
        new CSV(toArray).read(new StringReader(csv));
        assertEquals(2, toArray.listOfRecords.size());
        String[][] records = (String[][]) toArray.listOfRecords.toArray(new String[2][]);
        assertEquals("Hello", records[0][0]);
        assertEquals("World!", records[0][1]);
        assertEquals("One", records[1][0]);
        assertEquals("Two", records[1][1]);
        assertEquals("Three", records[1][2]);
    }

    public void testCommaAtEndOfLine()
    throws IOException
    {
        ToArray toArray = new ToArray();
        String csv = "Barbara_Foundas,Algeirs_Council_of_Neighborhood_Presidents,representative,4,,,,,,,,,,,,,,,,\nOne,Two,Three";
        new CSV(toArray).read(new StringReader(csv));
        assertEquals(2, toArray.listOfRecords.size());
        String[][] records = (String[][]) toArray.listOfRecords.toArray(new String[2][]);
        assertEquals("Barbara_Foundas", records[0][0]);
        assertEquals("Algeirs_Council_of_Neighborhood_Presidents", records[0][1]);
        assertEquals("One", records[1][0]);
        assertEquals("Two", records[1][1]);
        assertEquals("Three", records[1][2]);
    }
    
    public void testLine()
    {
        List<String> listOfStrings = new ArrayList<String>();
        
        listOfStrings.add("Hello");
        listOfStrings.add("I said, \"Parsley.\"");
        listOfStrings.add("I don't like to complain\nbut...");
        listOfStrings.add("C:\\WINNT");
        
        System.out.print(CSV.line(listOfStrings));
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */