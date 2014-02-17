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

// -------------------------------------------------------------------------
/**
 * A simple synchronized generic class that is intended to be used by two
 * threads, one which produces a value and another which consumes it. The
 * thread that wants to consume a value should call {@link #take()}, which
 * will block until another thread has produced the value by calling
 * {@link #offer(Object)} on the same object.
 *
 * @param <T> the type of the value being stored
 *
 * @author Tony Allevato
 */
public class ValueHolder<T>
{
    //~ Instance/static variables .............................................

    // The value being stored.
    private T storedValue;

    // A flag indicating whether the offer method has been called.
    private boolean offerCalled;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the value stored in the receiver, blocking if another thread has
     * not yet called {@link #offer(Object)}.
     *
     * @return the value stored in the receiver
     */
    public synchronized T take()
    {
        while (!offerCalled)
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
                // Do nothing.
            }
        }

        return storedValue;
    }


    // ----------------------------------------------------------
    /**
     * Sets the value stored in the receiver and notifies any waiting threads
     * that the value is ready.
     *
     * @param value the value to store in the receiver
     */
    public synchronized void offer(T value)
    {
        this.storedValue = value;
        this.offerCalled = true;

        notifyAll();
    }
}
