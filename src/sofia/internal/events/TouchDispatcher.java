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

import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;

import java.util.List;

/**
 * Handles and dispatches the touch events.
 *
 * @author Brian Bowden
 */
public class TouchDispatcher
{
    private static final EventDispatcher onTouchDown =
        new PointDispatcher("onTouchDown");
    private static final EventDispatcher onScreenTouchDown =
        new PointDispatcher("onScreenTouchDown");
    private static final EventDispatcher onTouchMove =
        new PointDispatcher("onTouchMove");
    private static final EventDispatcher onScreenTouchMove =
        new PointDispatcher("onScreenTouchMove");
    private static final EventDispatcher onTouchUp =
        new PointDispatcher("onTouchUp");
    private static final EventDispatcher onScreenTouchUp =
        new PointDispatcher("onScreenTouchUp");
    private static final EventDispatcher onTap =
        new PointDispatcher("onTap");
    private static final EventDispatcher onScreenTap =
        new PointDispatcher("onScreenTap");
    private static final EventDispatcher onDoubleTap =
        new PointDispatcher("onDoubleTap");
    private static final EventDispatcher onScreenDoubleTap =
        new PointDispatcher("onScreenDoubleTap");

    private TouchDispatcher()
    {
        // Nothing to do
    }

    /**
     * Determines which of the dispatchers to dispatch the event to.
     *
     * @param target object that is associated with the key event
     * @param e MotionEvent that is being checked
     */
    public static void dispatchTo(Object target, MotionEvent e)
    {
        // FIXME
        PointF location = null;
        if (e != null)
        {
            // may need to fix later
            location = new PointF(e.getRawX() / 0.5f, e.getRawY() / 0.5f);
        }

        int action = e.getAction() & MotionEvent.ACTION_MASK;
        // Press
        if (action == MotionEvent.ACTION_DOWN)
        {
            onTouchDown.dispatch(target, location);
            onScreenTouchDown.dispatch(target, location);
        }

        // Move
        if (action == MotionEvent.ACTION_MOVE)
        {
            onTouchMove.dispatch(target, location);
            onScreenTouchMove.dispatch(target, location);
        }

        // Need to add support for double-tapping
        // Tap
        if (action == MotionEvent.ACTION_UP)
        {
            onTap.dispatch(target, location);
            //if (e.getClickCount() % 2 == 0)
            {
                onDoubleTap.dispatch(target, location);
            }
            onTouchUp.dispatch(target, location);
        }
        if (action == MotionEvent.ACTION_UP)
        {
            onScreenTap.dispatch(target, location);
            //if (e.getClickCount() % 2 == 0)
            {
                onScreenDoubleTap.dispatch(target, location);
            }
            onScreenTouchUp.dispatch(target, location);
        }
    }

    private static class PointDispatcher
        extends sofia.internal.events.EventDispatcher
    {
        //~ Constructors ......................................................

        // ----------------------------------------------------------
        public PointDispatcher(String method)
        {
            super(method);
        }


        //~ Protected methods .................................................

        // ----------------------------------------------------------
        @Override
        protected List<MethodTransformer> lookupTransformers(
            Object receiver, List<Class<?>> argTypes)
        {
            List<MethodTransformer> descriptors =
                super.lookupTransformers(receiver, argTypes);

            xyFloatTransformer.addIfSupportedBy(receiver, descriptors);
            xyIntTransformer.addIfSupportedBy(receiver, descriptors);
            pointTransformer.addIfSupportedBy(receiver, descriptors);
            emptyTransformer.addIfSupportedBy(receiver, descriptors);

            return descriptors;
        }


        // ----------------------------------------------------------
        /**
         * Transforms an event with signature (PointF point) to one with
         * signature (float x, float y).
         */
        private MethodTransformer xyFloatTransformer =
            new MethodTransformer(float.class, float.class)
        {
            // ----------------------------------------------------------
            protected Object[] transform(Object... args)
            {
                PointF p = (PointF) args[0];
                return new Object[] { p.x, p.y };
            }
        };


        // ----------------------------------------------------------
        /**
         * Transforms an event with signature (PointF point) to one with
         * signature (int x, int y).
         */
        private MethodTransformer xyIntTransformer =
            new MethodTransformer(int.class, int.class)
        {
            // ----------------------------------------------------------
            protected Object[] transform(Object... args)
            {
                PointF p = (PointF) args[0];
                return new Object[] { Math.round(p.x), Math.round(p.y) };
            }
        };


        // ----------------------------------------------------------
        /**
         * Transforms an event with signature (PointF point) to one with
         * signature (Point p).
         */
        private MethodTransformer pointTransformer =
            new MethodTransformer(Point.class)
        {
            // ----------------------------------------------------------
            protected Object[] transform(Object... args)
            {
                PointF p = (PointF) args[0];
                return new Object[] {
                    new Point(Math.round(p.x), Math.round(p.y))
                    };
            }
        };


        // ----------------------------------------------------------
        /**
         * Transforms an event with signature (PointF point) to one with
         * an empty signature.
         */
        private MethodTransformer emptyTransformer =
            new MethodTransformer()
        {
            // ----------------------------------------------------------
            protected Object[] transform(Object... args)
            {
                return NO_ARGS;
            }
        };

        private static Object[] NO_ARGS = {};
    }
}
