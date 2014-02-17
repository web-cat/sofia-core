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

package sofia.app.internal;

import sofia.app.Persistent;
import sofia.app.Persistor;

import android.app.Activity;
import android.content.Context;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//-------------------------------------------------------------------------
/**
 * <p>
 * <em>This class is not intended for public use.</em>
 * </p><p>
 * Handles the storing and reconstructing of persistent fields in a
 * {@link Screen}.
 * </p>
 * 
 * @see Persistent
 * @see Persistor
 * 
 * @author Tony Allevato
 */
public class PersistenceManager
{
	//~ Instance/static fields ................................................

	private static ThreadLocal<PersistenceManager> instance =
			new ThreadLocal<PersistenceManager>();
	
	private Yaml yaml;


	//~ Constructors ..........................................................

    // ----------------------------------------------------------
	private PersistenceManager()
	{
		yaml = new Yaml(new SofiaConstructor(), new SofiaRepresenter());
	}


	//~ Methods ...............................................................

    // ----------------------------------------------------------
	public static PersistenceManager getInstance()
	{
		if (instance.get() == null)
		{
			instance.set(new PersistenceManager());
		}
		
		return instance.get();
	}
	
	
    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void loadPersistentContext(Context activity)
    {
    	String filename = activity.getClass().getCanonicalName() + ".yaml";

    	Map<String, Object> persistMap = new HashMap<String, Object>();
    	FileInputStream stream = null;
    	
    	try
    	{
	    	stream = activity.openFileInput(filename);
	    	persistMap = (Map<String, Object>) yaml.load(stream);
    	}
    	catch (IOException e)
    	{
    		// Do nothing; will initialize objects fresh below.
    	}
    	finally
    	{
    		if (stream != null)
    		{
    			try
    			{
        			stream.close();
    			}
    			catch (IOException e)
    			{
    				// Do nothing.
    			}
    		}
    	}
    	
    	HashMap<Field, Persistent> persistFields =
    			new HashMap<Field, Persistent>();
    	findPersistentFields(activity.getClass(), persistFields);

    	for (Field field : persistFields.keySet())
    	{
    		Persistent annotation = persistFields.get(field);

    		Object value = persistMap.get(field.getName());
    		boolean valueSet = false;

    		if (value != null)
    		{
    			try
    			{
    				field.set(activity, value);
    				valueSet = true;
    			}
    			catch (Exception e)
    			{
    				// Do nothing.
    			}
    		}
    		
    		if (!valueSet && annotation.create())
    		{
    			try
    			{
    				value = field.getType().newInstance();
    				field.set(activity, value);
    			}
    			catch (Exception e)
    			{
    				throw new IllegalStateException("Could not create a new "
    						+ "instance of "
    						+ field.getType().getCanonicalName(), e);
    			}
    		}
    	}
    }


    // ----------------------------------------------------------
    public void savePersistentContext(Context activity)
    {
    	HashMap<String, Object> persistMap = new HashMap<String, Object>();

    	HashMap<Field, Persistent> persistFields =
    			new HashMap<Field, Persistent>();
    	findPersistentFields(activity.getClass(), persistFields);

    	for (Field field : persistFields.keySet())
    	{
    		try
    		{
    			persistMap.put(field.getName(), field.get(activity));
    		}
    		catch (Exception e)
    		{
    			// Do nothing.
    		}
    	}

    	String filename = activity.getClass().getCanonicalName() + ".yaml";

    	if (persistMap.isEmpty())
    	{
    		activity.deleteFile(filename);
    	}
    	else
    	{
	    	OutputStreamWriter writer = null;
	
	    	try
	    	{
		    	FileOutputStream stream = activity.openFileOutput(
		    			filename, Activity.MODE_PRIVATE);
		    	writer = new OutputStreamWriter(stream);

		    	yaml.dump(persistMap, writer);
	    	}
	    	catch (IOException e)
	    	{
	    		// FIXME handle error somehow
	    	}
	    	finally
	    	{
	    		if (writer != null)
	    		{
	    			try
	    			{
	        			writer.close();
	    			}
	    			catch (IOException e)
	    			{
	    				// Do nothing.
	    			}
	    		}
	    	}
    	}
    }


    // ----------------------------------------------------------
    private void findPersistentFields(Class<?> klass,
    		Map<Field, Persistent> fields)
    {
    	for (Field field : klass.getDeclaredFields())
    	{
    		Persistent annotation = field.getAnnotation(Persistent.class);
    		if (annotation != null)
    		{
    			field.setAccessible(true);
    			fields.put(field, annotation);
    		}
    	}

    	if (!klass.getSuperclass().equals(Object.class))
    	{
    		findPersistentFields(klass.getSuperclass(), fields);
    	}
    }
    
    
    // ----------------------------------------------------------
    private static Class<?> getPersistor(Class<?> klass)
    {
		Persistor annotation = klass.getAnnotation(Persistor.class);
		Class<?> persistor = null;

		try
		{
			if (annotation != null)
			{
				persistor = annotation.value();
			}
			else
			{
				persistor = Class.forName(
						klass.getCanonicalName() + "Persistor");
			}
		}
		catch (Exception e)
		{
			// Do nothing.
		}
		
		return persistor;
    }


    // ----------------------------------------------------------
    public static Method getRepresentMethod(Class<?> klass)
    {
    	Method method = null;

		try
		{
			Class<?> persistor = getPersistor(klass);

			if (persistor != null)
			{
				// TODO replace with "best match" for parameter
				method = persistor.getMethod("represent",
						Object.class, Map.class);
			}

			return method;
		}
		catch (Exception e)
		{
			// Do nothing.
		}

		return method;    	
    }


    // ----------------------------------------------------------
    public static Method getConstructMethod(Class<?> klass)
    {
    	Method method = null;

		try
		{
			Class<?> persistor = getPersistor(klass);

			if (persistor != null)
			{
				method = persistor.getMethod("construct", Map.class);
			}

			return method;
		}
		catch (Exception e)
		{
			// Do nothing.
		}

		return method;    	
    }
}
