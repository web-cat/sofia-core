package sofia.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Reversed<T> implements Iterable<T>
{
    //~ Instance/static variables .............................................

    private final List<T> original;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public Reversed(List<T> original)
    {
        this.original = original;
    }


    // ----------------------------------------------------------
    public Iterator<T> iterator()
    {
        final ListIterator<T> it = original.listIterator(original.size());

        return new Iterator<T>()
        {
            // ----------------------------------------------------------
            public boolean hasNext()
            {
                return it.hasPrevious();
            }


            // ----------------------------------------------------------
            public T next()
            {
                return it.previous();
            }


            // ----------------------------------------------------------
            public void remove()
            {
                it.remove();
            }
        };
    }


    // ----------------------------------------------------------
    public static <T> Reversed<T> reversed(List<T> original)
    {
        return new Reversed<T>(original);
    }


    // ----------------------------------------------------------
    public static <T> Reversed<T> reversed(T[] original)
    {
        return new Reversed<T>(Arrays.asList(original));
    }
}
