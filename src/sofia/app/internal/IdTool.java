package sofia.app.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class IdTool
{
	private boolean triedToFindIdClass;
    private Class<?> cachedIdClass;
    private Context context;

    private Map<Integer, String> idsToNames;

    
    // ----------------------------------------------------------
    public IdTool(Context context)
    {
    	this.context = context;
    	
    	idsToNames = new HashMap<Integer, String>();
    }


    // ----------------------------------------------------------
    /**
     * A helper method to retrieve the {@code R.id} class in the application's
     * package, so that the names of numeric IDs can be looked up. If such a
     * class cannot be found, an exception is thrown.
     *
     * @return the {@code Class} object corresponding to the R.id class in the
     *     application's package.
     */
    public Class<?> idClass()
    {
        // TODO Replace this with new reflection introspection code instead.

        if (!triedToFindIdClass)
        {
        	triedToFindIdClass = true;

            String packageName = context.getPackageName();

            try
            {
                cachedIdClass = Class.forName(packageName + ".R$id");
            }
            catch (ClassNotFoundException e)
            {
            	// Do nothing.
            }

            if (cachedIdClass != null)
            {
                for (Field idField : cachedIdClass.getFields())
                {
                    int modifiers = Modifier.PUBLIC | Modifier.STATIC
                    		| Modifier.FINAL;

                    if (idField.getType().equals(int.class)
                    		&& (idField.getModifiers() & modifiers)
                    			== modifiers)
                    {
                    	String name = idField.getName();
                    	
                    	try
                    	{
                    		int value = idField.getInt(null);
                    		idsToNames.put(value, name);
                    	}
                    	catch (Exception e)
                    	{
                    		// Do nothing.
                    	}
                    }
                }
            }
        }

        return cachedIdClass;
    }


    // ----------------------------------------------------------
    /**
     * A helper method to retrieve the field name in the {@code R.id} class
     * of the ID that has the specified value. This is used to provide
     * human-readable messages when searches fail. If no field matches the ID,
     * then null is returned.
     *
     * @param id The numeric ID of a view, defined in the {@code R.id} class.
     * @return The field name of the specified ID.
     */
    public String getFieldNameForId(int id)
    {
    	idClass();
    	return idsToNames.get(id);
    }
}
