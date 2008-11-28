/*
 * Copyright The Engine Room, LLC 2005. All Right Reserved.
 */
package com.goodworkalan.litany;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan Gutierrez
 */
public class Litany
{
    private final static int UNQUOTED = 1;
    
    private final static int QUOTED = 2;
    
    private final static int FIRST_CHARACTER = 3;
    
    private final static int FIRST_FIELD = 4;
    
    private final Consumer _consumer;
    
    public interface Consumer
    {
        public void line(List<String> listOfFields);
    }
    
    public Litany(Consumer consumer)
    {
        _consumer = consumer;
    }
    
    public void read(Reader reader)
    throws IOException
    {
        List<String> listOfFields = new ArrayList<String>();
        while (readLine(reader, listOfFields))
        {
            _consumer.line(listOfFields);
            listOfFields.clear();
        }
    }
    
    
    public static List<String> readLine(Reader reader) throws IOException
    {
        List<String> listOfFields = new ArrayList<String>();
        if (readLine(reader, listOfFields))
        {
            return listOfFields;
        }
        return null;
    }
    
    /**
     * Accomodates a missing newline at end of file.
     *
     * @param reader A reader for the CSV file.
     * @param lastChararacter The last character read.
     *
     * @return A newline if at eof and the last character was not a newline, otherwise
     *         the result of read.
     *
     * @throws IOException For any I/O error.
     */
    private static int nextCharacter(Reader reader, char lastChararacter)
    throws IOException
    {
        int character = reader.read();
        if (character == -1
            && !(lastChararacter == '\n' || lastChararacter == (char) 0))
        {
            return '\n';
        }
        return character;
    }
    
    /**
     * @todo Breaks where there is no newline after the last character.
     */
    private static boolean readLine(Reader reader, List<String> list)
    throws IOException
    {
        StringBuffer field = new StringBuffer();
        
        boolean hasFields = false;
        boolean firstQuote = false;
        int mode = FIRST_FIELD;
        int character = 0;
        char ch = (char) character;
        while (ch != '\n' && (character = nextCharacter(reader, ch)) != -1)
        {
            ch = (char) character;
            switch (mode)
            {
            case FIRST_FIELD:
                hasFields = true;
            case FIRST_CHARACTER:
                switch (ch)
                {
                case '"':
                    mode = QUOTED;
                    break;
                case '\r':
                    break;
                case '\n':
                case ',':
                    list.add(null);
                    break;
                default:
                    mode = UNQUOTED;
                    field.append(ch);
                    break;
                }
                break;
            case QUOTED:
                switch (ch)
                {
                case '"':
                    if (firstQuote)
                    {
                        firstQuote = false;
                        field.append(ch);
                    }
                    else
                    {
                        firstQuote = true;
                    }
                    break;
                case '\r':
                    break;
                case '\n':
                case ',':
                    if (firstQuote)
                    {
                        firstQuote = false;
                        list.add(field.toString());
                        field.setLength(0);
                        mode = FIRST_CHARACTER;
                    }
                    else
                    {
                        field.append(ch);
                    }
                    break;
                default:
                    field.append(ch);
                }
                break;
            case UNQUOTED:
                switch (ch)
                {
                case '\r':
                    break;
                case ',':
                case '\n':
                    list.add(field.toString());
                    field.setLength(0);
                    mode = FIRST_CHARACTER;
                    break;
                default:
                    field.append(ch);
                    break;
                }
                break;
            default:
                throw new IllegalStateException();
            }
        }
        
        return hasFields;
    }
    
    public final static String line(List<String> listOfStrings)
    {
        StringBuffer line = new StringBuffer();
        
        String separator = "";
        for (String string : listOfStrings)
        {
            line.append(separator);
            if (string.indexOf('"') != -1 || string.indexOf("\n") != -1)
            {
                line.append('"');
                for (int i = 0; i < string.length(); i++)
                {
                    char ch = string.charAt(i);
                    if (ch != '\r')
                    {
                        if (ch == '"')
                        {
                            line.append(ch);
                        }
                        else if (ch == '\n')
                        {
                            line.append('\r');
                        }
                        line.append(ch);
                    }
                }
                line.append('"');
            }
            else
            {
                line.append(string);
            }
            separator = ",";
        }
        
        line.append("\n");

        return line.toString();
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */