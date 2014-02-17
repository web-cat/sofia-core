/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An implementation of {@link Iterable} that traverses a {@link List} in
 * reverse order. Rather than instantiating this class directly, one should
 * statically import the {@link #reversed(List)} method and use it in a
 * for-each loop:
 * <pre>
 * for (Object obj : reversed(list)) { ... }
 * </pre>
 * 
 * @param <T> the element type
 * 
 * @author Tony Allevato
 */
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
