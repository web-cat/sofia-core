package sofia.internal.events;

import java.util.HashMap;
import android.view.MotionEvent;
import java.util.List;
import android.graphics.Point;
import android.graphics.PointF;
import sofia.internal.events.EventDispatcher;

/**
 * Handles and dispatches the touch events.
 *
 * @author  Brian Bowden
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
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

    private static HashMap<String, Boolean> cachedDispatchableClasses =
        new HashMap<String, Boolean>();

    private TouchDispatcher()
    {
        // Nothing to do
    }

    /**
     * Returns true if the class of the target has any of the touch listener
     * methods, returns false otherwise.
     *
     * @param target object to test if it has any listeners
     * @return true if target has any touch listeners
     */
    public static boolean hasTouchListeners(Object target)
    {
        String key = target.getClass().toString();
        if (cachedDispatchableClasses.containsKey(key))
        {
            return cachedDispatchableClasses.get(key);
        }

        PointF location = new PointF();
        boolean hasDispatchableMethods =
               onTouchDown.isSupportedBy(target, location)
            || onScreenTouchDown.isSupportedBy(target, location)
            || onTouchMove.isSupportedBy(target, location)
            || onScreenTouchMove.isSupportedBy(target, location)
            || onTouchUp.isSupportedBy(target, location)
            || onScreenTouchUp.isSupportedBy(target, location)
            || onTap.isSupportedBy(target, location)
            || onScreenTap.isSupportedBy(target, location)
            || onDoubleTap.isSupportedBy(target, location)
            || onScreenDoubleTap.isSupportedBy(target, location);

        cachedDispatchableClasses.put(key, hasDispatchableMethods);
        return hasDispatchableMethods;
    }

    /**
     * Determines which of the dispatchers to dispatch the event to.
     *
     * @param target object that is associated with the key event
     * @param e MotionEvent that is being checked
     * @param action action of the corresponding motion event
     * @param location (x, y) cell coordinate pair where the event occurred
     */
    public static void dispatchTo(Object target, MotionEvent e, int action, PointF location)
    {
        // using -1 to indicate double-taps, probably need to change later
        if (action != -1)
        {
            action &= MotionEvent.ACTION_MASK;
        }

        // Press
        if (action == MotionEvent.ACTION_DOWN)
        {
            onTouchDown.dispatch(target, location);
            onScreenTouchDown.dispatch(target, location);
        }
        // Move
        else if (action == MotionEvent.ACTION_MOVE)
        {
            onTouchMove.dispatch(target, location);
            onScreenTouchMove.dispatch(target, location);
        }
        // Tap
        else if (action == MotionEvent.ACTION_UP)
        {
            onTap.dispatch(target, location);
            onScreenTap.dispatch(target, location);

            onTouchUp.dispatch(target, location);
            onScreenTouchUp.dispatch(target, location);
        }
        // Double Tap
        else if (action == -1)
        {
            onDoubleTap.dispatch(target, location);
            onScreenDoubleTap.dispatch(target, location);
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
