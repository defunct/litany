/*
 * Copyright The Engine Room, LLC 2005. All Right Reserved.
 */
package com.goodworkalan.litany;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Alan Gutierrez
 */
public class Litany implements Iterable<List<String>>
{
    private final static int UNQUOTED = 1;
    
    private final static int QUOTED = 2;
    
    private final static int FIRST_CHARACTER = 3;
    
    private final static int FIRST_FIELD = 4;
    
    private final Reader reader;
    
    public Litany(Reader reader)
    {
        this.reader = reader;
    }

    public Iterator<List<String>> iterator()
    {
        return new LineIterator(reader);
    }
    
    /**
     * Accommodates a missing newline at end of file.
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
        
        line.append("\r\n");

        return line.toString();
    }
    
    private final static class Error extends RuntimeException
    {
        private static final long serialVersionUID = 20081128L;

        public Error(Throwable cause)
        {
            super(null, cause);
        }
    }
    
    private final static class LineIterator implements Iterator<List<String>>
    {
        private final Reader reader;
        
        private final List<String> line;

        private List<String> next;
        
        private boolean done;
        
        public LineIterator(Reader reader)
        {
            this.reader = reader;
            this.line = new ArrayList<String>();
        }
        
        private void advance()
        {
            try
            {
                line.clear();
                if (readLine(reader, line))
                {
                    next = new ArrayList<String>(line);
                }
                else
                {
                    done = true;
                }
            }
            catch (IOException e)
            {
                throw new Error(e);
            }
        }

        public boolean hasNext()
        {
            // Prime if this is the first time next or hasNext is called.
            if (next == null)
            {
                advance();
            }
            
            // Return our done state.
            return !done;
        }
        
        public List<String> next()
        {
            // Error if called after last line.
            if (done)
            {
                throw new NoSuchElementException();
            }
            
            // Prime if this is the first time next or hasNext is called.
            if (next == null)
            {
                advance();
            }
            
            // Return the next result and advance.
            List<String> result = next;
            advance();
            return result;
        }
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */