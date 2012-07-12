package sofia.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ReflectiveMap implements Map<String, Object>
{
    private Object referent;


    public ReflectiveMap(Object referent)
    {
        this.referent = referent;
    }


    private Object evaluate(String keyPath)
    {
        ReflectiveMap current = this;
        Object result = null;

        String remainder = keyPath;

        do
        {
            int index = remainder.indexOf('.');
            String key;

            if (index == -1)
            {
                key = remainder;
                remainder = null;
            }
            else
            {
                key = remainder.substring(0, index);
                remainder = remainder.substring(index + 1);
            }

            result = current.getKeyValue(key);

            if (remainder != null)
            {
                current = new ReflectiveMap(result);
            }
        }
        while (remainder != null);

        return result;
    }


    private Object getKeyValue(String key)
    {
        try
        {
            Method method;
            Field field;

            method = findMethod("get" + upper(key));
            if (method != null)
            {
                return method.invoke(referent);
            }

            method = findMethod("is" + upper(key));
            if (method != null)
            {
                return method.invoke(referent);
            }

            method = findMethod(key);
            if (method != null)
            {
                return method.invoke(referent);
            }

            field = findField("_" + key);
            if (field != null)
            {
                return field.get(referent);
            }

            field = findField(key);
            if (field != null)
            {
                return field.get(referent);
            }

            throw new IllegalArgumentException("Cannot find the property '" + key
                + "' on class " + referent.getClass().getCanonicalName());
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getTargetException());
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }


    private Method findMethod(String name)
    {
        try
        {
            return referent.getClass().getMethod(name);
        }
        catch (NoSuchMethodException e)
        {
            return null;
        }
    }


    private Field findField(String name)
    {
        try
        {
            return referent.getClass().getField(name);
        }
        catch (NoSuchFieldException e)
        {
            return null;
        }
    }


    private static String upper(String key)
    {
        return Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }


    public void clear()
    {
        throw new UnsupportedOperationException(
            "clear() is not supported on ReflectiveFieldMap.");
    }


    public boolean containsKey(Object key)
    {
        try
        {
            evaluate(key.toString());
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }


    public boolean containsValue(Object value)
    {
        throw new UnsupportedOperationException(
            "containsValue() is not supported on ReflectiveFieldMap.");
    }

    public Set<java.util.Map.Entry<String, Object>> entrySet()
    {
        throw new UnsupportedOperationException(
            "entrySet() is not supported on ReflectiveFieldMap.");
    }

    public Object get(Object key)
    {
        return evaluate(key.toString());
    }


    public boolean isEmpty()
    {
        throw new UnsupportedOperationException(
            "isEmpty() is not supported on ReflectiveFieldMap.");
    }

    public Set<String> keySet()
    {
        throw new UnsupportedOperationException(
            "keySet() is not supported on ReflectiveFieldMap.");
    }

    public Object put(String key, Object value)
    {
        // TODO implement
        throw new UnsupportedOperationException(
            "put() is not supported on ReflectiveFieldMap.");
    }

    public void putAll(Map<? extends String, ? extends Object> otherMap)
    {
        for (Map.Entry<? extends String, ? extends Object> entry
            : otherMap.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key)
    {
        throw new UnsupportedOperationException(
            "remove() is not supported on ReflectiveFieldMap.");
    }

    public int size()
    {
        throw new UnsupportedOperationException(
            "size() is not supported on ReflectiveFieldMap.");
    }

    public Collection<Object> values()
    {
        throw new UnsupportedOperationException(
            "values() is not supported on ReflectiveFieldMap.");
    }
}
