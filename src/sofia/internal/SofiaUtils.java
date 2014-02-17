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

import android.os.Looper;
import android.os.MessageQueue;

import java.lang.reflect.Field;

// -------------------------------------------------------------------------
/**
 * Various helper methods used in the Sofia library. Not intended to be used by
 * most users of the library.
 *
 * @author Tony Allevato
 */
public class SofiaUtils
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public static void quitLoop()
    {
        // This is a major hack, which makes the main GUI message loop
        // quitable, sends a quit message to it that is then processed by
        // the "nested" loop to end it, then clears its "quit" flag so that
        // the "outer" loop can continue normally.
        //
        // Our hope is that the implementation of MessageQueue in Android
        // will remain mostly unchanged, but we will need to test this
        // thoroughly on all API versions that we plan to use, now and in
        // the future.

        try
        {
            Field mQuitAllowed =
                MessageQueue.class.getDeclaredField("mQuitAllowed");
            mQuitAllowed.setAccessible(true);

            mQuitAllowed.set(Looper.myQueue(), true);

            Looper.myLooper().quit();

            Field mQuiting =
                MessageQueue.class.getDeclaredField("mQuiting");
            mQuiting.setAccessible(true);

            mQuiting.set(Looper.myQueue(), false);
            mQuitAllowed.set(Looper.myQueue(), false);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Sofia modal runnables are not "
                + "supported on this version of the Android API because the "
                + "MessageQueue.mQuitAllowed field could not be found or "
                + "there was a problem changing it.");
        }
    }
}
