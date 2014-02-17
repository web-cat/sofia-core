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

package sofia.internal.events;

import android.view.MotionEvent;

import java.util.List;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @version 2012.10.24
 */
public class MotionEventDispatcher extends EventDispatcher
{
    //~ Fields ................................................................

    private MethodTransformer xyTransformer;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public MotionEventDispatcher(String method)
    {
        super(method);
    }


    //~ Protected methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected List<MethodTransformer> lookupTransformers(
            Object receiver, List<Class<?>> argTypes)
    {
        List<MethodTransformer> descriptors =
                super.lookupTransformers(receiver, argTypes);

        getXYTransformer().addIfSupportedBy(receiver, descriptors);

        return descriptors;
    }


    // ----------------------------------------------------------
    /**
     * Transforms an event with signature (MouseEvent event) to one with
     * signature (float x, float y).
     */
    protected MethodTransformer getXYTransformer()
    {
        if (xyTransformer == null)
        {
            xyTransformer = new MethodTransformer(float.class, float.class)
            {
                // ----------------------------------------------------------
                protected Object[] transform(Object... args)
                {
                    MotionEvent e = (MotionEvent) args[0];
                    return new Object[] { (float) e.getX(), (float) e.getY() };
                }
            };
        }

        return xyTransformer;
    }
}
