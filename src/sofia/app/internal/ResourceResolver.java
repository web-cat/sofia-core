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

import sofia.app.OptionsMenu;
import sofia.app.ScreenLayout;
import sofia.internal.NameUtils;

import android.content.Context;

//-------------------------------------------------------------------------
/**
 * Helper methods that resolve resource IDs for annotations that accept either
 * a string value (the name of the ID) or an id attribute (the numeric value).
 * 
 * @author Tony Allevato
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
