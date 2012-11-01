package sofia.app.internal;

import sofia.app.OptionsMenu;
import sofia.app.ScreenLayout;
import sofia.internal.NameUtils;
import android.content.Context;

//-------------------------------------------------------------------------
/**
 * TODO
 * 
 * @author  Tony Allevato
 * @version 2012.09.01
 */
public class ResourceResolver
{
	//~ Public methods ........................................................

	// ----------------------------------------------------------
	/**
	 * Gets the {@code R.layout.*} resource ID represented by the specified
	 * {@link ScreenLayout} annotation.
	 * 
	 * @param context the context
	 * @param annotation the annotation
	 * @return the ID
	 */
	public static int resolve(Context context, ScreenLayout annotation)
	{
		if (annotation == null)
		{
			return select(context, "layout", 0, null);
		}
		else
		{
			return select(
					context, "layout", annotation.id(), annotation.value());
		}
	}


	// ----------------------------------------------------------
	/**
	 * Gets the {@code R.menu.*} resource ID represented by the specified
	 * {@link OptionsMenu} annotation.
	 * 
	 * @param context the context
	 * @param annotation the annotation
	 * @return the ID
	 */
	public static int resolve(Context context, OptionsMenu annotation)
	{
		return select(context, "menu", annotation.id(), annotation.value());
	}


	// ----------------------------------------------------------
	/**
	 * If {@code id} is non-zero, then this method returns the ID; otherwise,
	 * it looks up a resource with the specified type and name and returns the
	 * corresponding numeric ID.
	 * 
	 * @param context the context
	 * @param type the type of the resource ("menu", "layout", etc.)
	 * @param id a numeric ID
	 * @param name the name of a resource
	 * @return the ID of the resource
	 */
	private static int select(Context context, String type, int id, String name)
	{
		if (id != 0)
		{
			return id;
		}
		else if (name != null && name.length() > 0)
		{
        	// TODO In what ways can we make this search more flexible? Support
        	// different packages?

			return context.getResources().getIdentifier(
					name, type, context.getPackageName());
		}
		else
		{
			int result = 0;

			for (String possibleResource :
				NameUtils.classToResources(context.getClass()))
			{
				result = context.getResources().getIdentifier(
						possibleResource, type, context.getPackageName());
				
				if (result != 0)
				{
					break;
				}
			}

			return result;
		}
	}
}
