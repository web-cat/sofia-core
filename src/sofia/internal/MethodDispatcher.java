package sofia.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//-------------------------------------------------------------------------
/**
 * Represents a reflective dispatcher with an internal cache of looked-up
 * Method objects.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class MethodDispatcher
{
    //~ Fields ................................................................

    private String methodName;
    private int    paramCount;
    private Map<Class<?>, Map<List<Class<?>>, MethodDescriptor>> methodMap;

    private static final MethodDescriptor NOT_FOUND;
    static {
        try
        {
            NOT_FOUND = new MethodDescriptor(
                Object.class.getDeclaredMethod("getClass"), false);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static final Map<Class<?>, Class<?>> wrapperEquivalent =
        new HashMap<Class<?>, Class<?>>();
    static {
        wrapperEquivalent.put(Boolean.class,   boolean.class);
        wrapperEquivalent.put(Byte.class,      byte.class);
        wrapperEquivalent.put(Character.class, char.class);
        wrapperEquivalent.put(Double.class,    double.class);
        wrapperEquivalent.put(Float.class,     float.class);
        wrapperEquivalent.put(Integer.class,   int.class);
        wrapperEquivalent.put(Short.class,     short.class);

        wrapperEquivalent.put(boolean.class,   Boolean.class);
        wrapperEquivalent.put(byte.class,      Byte.class);
        wrapperEquivalent.put(char.class,      Character.class);
        wrapperEquivalent.put(double.class,    Double.class);
        wrapperEquivalent.put(float.class,     Float.class);
        wrapperEquivalent.put(int.class,       Integer.class);
        wrapperEquivalent.put(short.class,     Short.class);
    }


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new dispatcher.
     * @param methodName The name of the method this dispatcher calls
     * @param numberOfParameters The arity of the method to be called
     */
    public MethodDispatcher(String methodName, int numberOfParameters)
    {
        this.methodName = methodName;
        this.paramCount = numberOfParameters;
        methodMap =
            new HashMap<Class<?>, Map<List<Class<?>>, MethodDescriptor>>();
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Get the method name this dispatcher looks for.
     * @return This dispatcher's method name.
     */
    public String methodName()
    {
        return methodName;
    }


    // ----------------------------------------------------------
    /**
     * Get the number of parameters the target method takes.
     * @return The target method's number of parameters (its arity).
     */
    public int numberOfParameters()
    {
        return paramCount;
    }


    // ----------------------------------------------------------
    /**
     * Invoke the named method on the receiver, passing the specified
     * parameters.
     * @param receiver The object to invoke the method on.
     * @param parameters The parameters to pass to the method, which
     *    must match the arity specified in the dispatcher's constructor.
     * @return The return value of the invoked method if it returns a boolean
     * value, or false if it does not.  Invoked methods declared as void
     * will result in a return value of false.
     */
    public boolean callMethodOn(Object receiver, Object... parameters)
    {
        if (parameters.length != paramCount)
        {
            throw new IllegalArgumentException(parameters.length
                + " parameters provided in call to "
                + methodName
                + Arrays.toString(parameters)
                + ", but "
                + paramCount
                + " are required.");
        }

        Class<?> clazz = receiver.getClass();
        List<Class<?>> argTypes = argumentProfile(parameters);

        Map<List<Class<?>>, MethodDescriptor> methods = methodMap.get(clazz);
        if (methods == null)
        {
            methods = new HashMap<List<Class<?>>, MethodDescriptor>();
            methodMap.put(clazz, methods);
        }

        MethodDescriptor m = methods.get(argTypes);
        if (m == null)
        {
            m = findMethodOn(clazz, argTypes);
            if (m == null)
            {
                m = NOT_FOUND;
            }
            methods.put(argTypes, m);
        }

        if (m == NOT_FOUND)
        {
            return false;
        }
        else
        {
            return m.invoke(receiver, parameters);
        }
    }


    // ----------------------------------------------------------
    public boolean supportedBy(Object receiver, Object... parameters)
    {
    	// FIXME Need to merge this with the code above, possibly factor out
    	// into a helper method

        if (parameters.length != paramCount)
        {
            throw new IllegalArgumentException(parameters.length
                + " parameters provided in call to "
                + methodName
                + Arrays.toString(parameters)
                + ", but "
                + paramCount
                + " are required.");
        }

        Class<?> clazz = receiver.getClass();
        List<Class<?>> argTypes = argumentProfile(parameters);

        Map<List<Class<?>>, MethodDescriptor> methods = methodMap.get(clazz);
        if (methods == null)
        {
            methods = new HashMap<List<Class<?>>, MethodDescriptor>();
            methodMap.put(clazz, methods);
        }

        MethodDescriptor m = methods.get(argTypes);
        if (m == null)
        {
            m = findMethodOn(clazz, argTypes);
            if (m == null)
            {
                m = NOT_FOUND;
            }
            methods.put(argTypes, m);
        }

        if (m == NOT_FOUND)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    // ----------------------------------------------------------
    private List<Class<?>> argumentProfile(Object... parameters)
    {
        List<Class<?>> result = new ArrayList<Class<?>>(parameters.length);
        for (Object param : parameters)
        {
            result.add((param == null) ? Object.class : param.getClass());
        }
        return result;
    }


    // ----------------------------------------------------------
    private boolean isBetter(int[] oldScore, int[] newScore)
    {
        int oldTotal = 0;
        for (int i : oldScore)
        {
            oldTotal += i;
        }

        int newTotal = 0;
        for (int i : newScore)
        {
            newTotal += i;
        }

        if (oldTotal != newTotal)
        {
            return newTotal < oldTotal;
        }
        else
        {
            for (int i = 0; i < oldScore.length; i++)
            {
                if (oldScore[i] != newScore[i])
                {
                    return newScore[i] < oldScore[i];
                }
            }
            return false;
        }
    }


    // ----------------------------------------------------------
    private int argConversionCost(
        Class<?> actualParamType, Class<?> formalParamType)
        throws IllegalArgumentException
    {
        if (formalParamType.equals(actualParamType))
        {
            // Identical types
            return 0;
        }

        if (formalParamType.equals(wrapperEquivalent.get(actualParamType)))
        {
            // Treat auto-boxing/unboxing as free
            return 0;
        }

        if (formalParamType.isAssignableFrom(actualParamType))
        {
            // Calculate distance
            int distance = 1;

            outerLoop:
            while (actualParamType != null)
            {
                if (formalParamType.equals(actualParamType.getSuperclass()))
                {
                    return distance;
                }

                for (Class<?> iface : actualParamType.getInterfaces())
                {
                    if (iface.equals(formalParamType))
                    {
                        return distance;
                    }
                }

                for (Class<?> iface : actualParamType.getInterfaces())
                {
                    if (formalParamType.isAssignableFrom(iface))
                    {
                        actualParamType = iface;
                        distance++;
                        continue outerLoop;
                    }
                }

                actualParamType = actualParamType.getSuperclass();
                distance++;
            }

            return distance;
        }

        throw new IllegalArgumentException("incompatible types");
    }


    // ----------------------------------------------------------
    private void scoreMethod(
        Method m, List<Class<?>> actualArgTypes, int[] scores)
        throws IllegalArgumentException
    {
        Class<?>[] formals = m.getParameterTypes();
        if (formals.length != actualArgTypes.size())
        {
            throw new IllegalArgumentException(
                "incompatible number of arguments");
        }

        for (int i = 0; i < formals.length; i++)
        {
            scores[i] = argConversionCost(actualArgTypes.get(i), formals[i]);
        }
    }


    // ----------------------------------------------------------
    private MethodDescriptor findMethodOn(
        Class<?> clazz, List<Class<?>> argTypes)
    {
        Method bestMatch     = null;
        boolean argsReversed = false;
        int[]  bestScore     = new int[argTypes.size()];

        int[]  nextScore = new int[argTypes.size()];
        List<Class<?>> argTypesInReverse =
            new ArrayList<Class<?>>(argTypes.size());
        for (int i = argTypes.size() -1; i >= 0; i--)
        {
            argTypesInReverse.add(argTypes.get(i));
        }

        while (clazz != null)
        {
            for (Method candidate : clazz.getDeclaredMethods())
            {
                if (methodName.equals(candidate.getName()))
                {
                    try
                    {
                        // Check this method and leave results in nextScore
                        scoreMethod(candidate, argTypes, nextScore);

                        if (bestMatch == null
                            || isBetter(bestScore, nextScore))
                        {
                            bestMatch = candidate;
                            argsReversed = false;

                            // Rotate nextScore into the bestScore position
                            // then reuse the old bestScore array next iter.
                            int[] tmp = bestScore;
                            bestScore = nextScore;
                            nextScore = tmp;
                        }
                    }
                    catch (IllegalArgumentException e)
                    {
                        // This method isn't compatible with the
                        // given arguments, so ignore it.
                    }

                    // Try args in reverse order as well
                    try
                    {
                        // Check this method and leave results in nextScore
                        scoreMethod(candidate, argTypesInReverse, nextScore);

                        if (bestMatch == null
                            || isBetter(bestScore, nextScore))
                        {
                            bestMatch = candidate;
                            argsReversed = true;

                            // Rotate nextScore into the bestScore position
                            // then reuse the old bestScore array next iter.
                            int[] tmp = bestScore;
                            bestScore = nextScore;
                            nextScore = tmp;
                        }
                    }
                    catch (IllegalArgumentException e)
                    {
                        // This method isn't compatible with the
                        // given arguments, so ignore it.
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        return (bestMatch == null)
            ? null
            : new MethodDescriptor(bestMatch, argsReversed);
    }


    // ----------------------------------------------------------
    private static class MethodDescriptor
    {
        private Method m;
        private boolean argsReversed;


        // ----------------------------------------------------------
        /**
         * Create a new descriptor.
         * @param m The method to use
         * @param argsReversed true if arguments should be passed in reverse
         * order.
         */
        public MethodDescriptor(Method m, boolean argsReversed)
        {
            this.m = m;
            this.argsReversed = argsReversed;
        }


        // ----------------------------------------------------------
        public boolean invoke(Object receiver, Object[] parameters)
        {
            if (argsReversed && parameters.length > 1)
            {
                int limit = parameters.length / 2;
                for (int i = 0; i < limit; i++)
                {
                    Object temp = parameters[i];
                    parameters[i] = parameters[parameters.length - i - 1];
                    parameters[parameters.length - i - 1] = temp;
                }
            }

            try
            {
                Object result = m.invoke(receiver, parameters);
                return Boolean.TRUE.equals(result);
            }
            catch (Exception e)
            {
                if (e instanceof RuntimeException)
                {
                    throw (RuntimeException)e;
                }
                else
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
