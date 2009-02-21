package com.goodworkalan.litany;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// TODO Document.
final class LineIterator implements Iterator<List<String>>
{
    // TODO Document.
    private final Reader reader;
    
    // TODO Document.
    private final List<String> line;

    // TODO Document.
    private List<String> next;
    
    // TODO Document.
    private boolean done;
    
    // TODO Document.
    public LineIterator(Reader reader)
    {
        this.reader = reader;
        this.line = new ArrayList<String>();
    }
    
    // TODO Document.
    private void advance()
    {
        try
        {
            line.clear();
            if (Litany.readLine(reader, line))
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
            throw new LitanyException(e);
        }
    }

    // TODO Document.
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
    
    // TODO Document.
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
    
    // TODO Document.
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}