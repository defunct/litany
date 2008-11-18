/* Copyright Alan Gutierrez 2006 */
package com.agtrz.litany;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.agtrz.litany.Litany;

import junit.framework.TestCase;

public class CSVTestCase
extends TestCase
{
    private final static class Gather
    implements Litany.Consumer
    {
        public final List<List<String>> listOfRecords = new ArrayList<List<String>>();
        
        public void line(List<String> listOfFields)
        {
            listOfRecords.add(new ArrayList<String>(listOfFields));
        }
        
        public String get(int i, int j)
        {
            return listOfRecords.get(i).get(j);
        }
    }

    public void testOneLine()
    throws IOException
    {
        Gather gather = new Gather();
        String csv = "Hello,World!";
        new Litany(gather).read(new StringReader(csv));
        assertEquals(1, gather.listOfRecords.size());
        assertEquals("Hello", gather.get(0, 0));
        assertEquals("World!", gather.get(0, 1));
    }

    public void testTwoLines()
    throws IOException
    {
        Gather gather = new Gather();
        String csv = "Hello,World!\nOne,Two,Three";
        new Litany(gather).read(new StringReader(csv));
        assertEquals(2, gather.listOfRecords.size());
        assertEquals("Hello", gather.get(0, 0));
        assertEquals("World!", gather.get(0, 1));
        assertEquals("One", gather.get(1, 0));
        assertEquals("Two", gather.get(1, 1));
        assertEquals("Three", gather.get(1, 2));
    }

    public void testCommaAtEndOfLine()
    throws IOException
    {
        Gather gather = new Gather();
        String csv = "Barbara_Foundas,Algeirs_Council_of_Neighborhood_Presidents,representative,4,,,,,,,,,,,,,,,,\nOne,Two,Three";
        new Litany(gather).read(new StringReader(csv));
        assertEquals(2, gather.listOfRecords.size());
        assertEquals("Barbara_Foundas", gather.get(0, 0));
        assertEquals("Algeirs_Council_of_Neighborhood_Presidents", gather.get(0, 1));
        assertEquals("One", gather.get(1, 0));
        assertEquals("Two", gather.get(1, 1));
        assertEquals("Three", gather.get(1, 2));
    }
    
    public void testLine()
    {
        List<String> listOfStrings = new ArrayList<String>();
        
        listOfStrings.add("Hello");
        listOfStrings.add("I said, \"Parsley.\"");
        listOfStrings.add("I don't like to complain\nbut...");
        listOfStrings.add("C:\\WINNT");
        
        System.out.print(Litany.line(listOfStrings));
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */