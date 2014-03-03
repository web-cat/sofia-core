package sofia.internal.events;

import java.util.HashMap;
import android.view.KeyEvent;
import sofia.internal.events.EventDispatcher;

/**
 * Represents the dispatcher for the dpad.
 *
 * @author  Brian Bowden
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class DpadDispatcher
{
    private static final EventDispatcher dpadNorthIsDown =
        new EventDispatcher("dpadNorthIsDown");
    private static final EventDispatcher dpadNortheastIsDown =
        new EventDispatcher("dpadNortheastIsDown");
    private static final EventDispatcher dpadEastIsDown =
        new EventDispatcher("dpadEastIsDown");
    private static final EventDispatcher dpadSoutheastIsDown =
        new EventDispatcher("dpadSoutheastIsDown");
    private static final EventDispatcher dpadSouthIsDown =
        new EventDispatcher("dpadSouthIsDown");
    private static final EventDispatcher dpadSouthwestIsDown =
        new EventDispatcher("dpadSouthwestIsDown");
    private static final EventDispatcher dpadWestIsDown =
        new EventDispatcher("dpadWestIsDown");
    private static final EventDispatcher dpadNorthwestIsDown =
        new EventDispatcher("dpadNorthwestIsDown");
    private static final EventDispatcher dpadCenterIsDown =
        new EventDispatcher("dpadCenterIsDown");

    private static HashMap<String, Boolean> cachedDispatchableClasses =
        new HashMap<String, Boolean>();

    /**
     * Nothing to do for dispatcher.
     */
    private DpadDispatcher()
    {
        // No initialization
    }

    /**
     * Returns true if the class of the target has any of the dpad listener
     * methods, returns false otherwise.
     *
     * @param target object to test if it has any listeners
     * @return true if target has any touch listeners
     */
    public static boolean hasDPadListeners(Object target)
    {
        String key = target.getClass().toString();
        if (cachedDispatchableClasses.containsKey(key))
        {
            return cachedDispatchableClasses.get(key);
        }

        boolean hasDispatchableMethods =
               dpadNorthIsDown.isSupportedBy(target)
            || dpadNortheastIsDown.isSupportedBy(target)
            || dpadEastIsDown.isSupportedBy(target)
            || dpadSoutheastIsDown.isSupportedBy(target)
            || dpadSouthIsDown.isSupportedBy(target)
            || dpadSouthwestIsDown.isSupportedBy(target)
            || dpadWestIsDown.isSupportedBy(target)
            || dpadNorthwestIsDown.isSupportedBy(target);

        cachedDispatchableClasses.put(key, hasDispatchableMethods);
        return hasDispatchableMethods;
    }

    /**
     * Determines which of the dispatchers to dispatch the event to.
     *
     * @param target object that is associated with the key event
     * @param keyCode corresponding keyCode(s) for the key event
     */
    public static void dispatchTo(Object target, int keyCode)
    {
        boolean north = checkDirection(keyCode, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_W);
        boolean east = checkDirection(keyCode, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_D);
        boolean south = checkDirection(keyCode, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_S);
        boolean west = checkDirection(keyCode, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_A);

        if (north && !(east || west))
        {
            dpadNorthIsDown.dispatch(target);
        }
        if (north && east)
        {
            dpadNortheastIsDown.dispatch(target);
            if (!dpadNortheastIsDown.isSupportedBy(target))
            {
                dpadNorthIsDown.dispatch(target);
                dpadEastIsDown.dispatch(target);
            }
        }
        if (east && !(north || south))
        {
            dpadEastIsDown.dispatch(target);
        }
        if (south && east)
        {
            dpadSoutheastIsDown.dispatch(target);
            if (!dpadSoutheastIsDown.isSupportedBy(target))
            {
                dpadSouthIsDown.dispatch(target);
                dpadEastIsDown.dispatch(target);
            }
        }
        if (south && !(east || west))
        {
            dpadSouthIsDown.dispatch(target);
        }
        if (south && west)
        {
            dpadSouthwestIsDown.dispatch(target);
            if (!dpadSouthwestIsDown.isSupportedBy(target))
            {
                dpadSouthIsDown.dispatch(target);
                dpadWestIsDown.dispatch(target);
            }
        }
        if (west && !(north || south))
        {
            dpadWestIsDown.dispatch(target);
        }
        if (north && west)
        {
            dpadNorthwestIsDown.dispatch(target);
            if (!dpadNorthwestIsDown.isSupportedBy(target))
            {
                dpadNorthIsDown.dispatch(target);
                dpadWestIsDown.dispatch(target);
            }
        }
        if ((keyCode & KeyEvent.KEYCODE_SPACE) == KeyEvent.KEYCODE_SPACE)
        {
            dpadCenterIsDown.dispatch(target);
        }
    }

    /**
     * Checks if the key code matches one of the key constants or dpad constants.
     *
     * @param keyCode masked key code that can contain one or more key codes
     * @param dpadConstant constant for dpap UP, LEFT, RIGHT, DOWN directions
     * @param keyConstant constant for W, A, S, D
     */
    private static boolean checkDirection(int keyCode, int dpadConstant, int keyConstant)
    {
        boolean direction = ((keyCode & 0xff) == dpadConstant
            || (keyCode & keyConstant)== keyConstant);
        keyCode >>= 8;
        direction |= ((keyCode & 0xff) == dpadConstant
            || (keyCode & 0xff)== keyConstant);
        return direction;
    }
}

