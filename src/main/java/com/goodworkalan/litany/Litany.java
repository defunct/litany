/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.litany;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

/**
 * A parser for Microsoft CSV formatted files.
 * 
 * @author Alan Gutierrez
 */
public class Litany implements Iterable<List<String>>
{
    /** The parse state when reading an unquoted field. */
    private final static int UNQUOTED = 1;

    /** The parse state for a quoted field. */
    private final static int QUOTED = 2;

    /** The parse state when reading the first character of a field. */
    private final static int FIRST_CHARACTER = 3;

    /** The parse state when reading the first field. */
    private final static int FIRST_FIELD = 4;

    /** The CSV source. */
    private final Reader reader;

    /**
     * Create a CSV lexer from the given CSV source reader.
     * 
     * @param reader
     *            The CSV source.
     */
    public Litany(Reader reader)
    {
        this.reader = reader;
    }

    /**
     * Return an iterator over the lines in the the CSV source.
     * 
     * @return An iterator over the lines in the CSV source.
     */
    public Iterator<List<String>> iterator()
    {
        return new LineIterator(reader);
    }

    /**
     * Accommodates a missing newline at end of file.
     * 
     * @param reader
     *            The CSV source.
     * @param lastChararacter
     *            The last character read.
     * 
     * @return A newline if at EOF and the last character was not a newline,
     *         otherwise the result of read.
     * 
     * @throws IOException
     *             For any I/O error.
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
    static boolean readLine(Reader reader, List<String> list)
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

    /**
     * Generate a line of Microsoft CSV formatted text from the given list of
     * fields.
     * 
     * @param fields
     *            The list of fields.
     * @return A line of Microsoft CSV formatted text.
     */
    public final static String line(Object...fields)
    {
        StringBuffer line = new StringBuffer();

        String separator = "";
        for (Object object : fields)
        {
            line.append(separator);
            if (object != null)
            {
                String field = object.toString();
                if (field.indexOf('"') != -1 || field.indexOf("\n") != -1)
                {
                    line.append('"');
                    for (int i = 0; i < field.length(); i++)
                    {
                        char ch = field.charAt(i);
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
                    line.append(field);
                }
            }
            separator = ",";
        }

        line.append("\r\n");

        return line.toString();
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */