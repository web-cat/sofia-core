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

package sofia.internal;

import java.util.Arrays;
import java.util.List;

//-------------------------------------------------------------------------
/**
 * Helper methods to perform string conversions between various naming
 * conventions.
 * 
 * @author Tony Allevato
 */
public class NameUtils
{
	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	/**
	 * Prevent instantiation.
	 */
	private NameUtils()
	{
		// Do nothing.
	}


	//~ Public methods ........................................................

	// ----------------------------------------------------------
	/**
	 * <p>
	 * Converts a class name to a method name prefix (for example, when the
	 * screen class "FooScreen" sends a result back to the screen that started
	 * it, it should call a method named "fooScreenReturned"). This conversion
	 * is done as follows:
	 * </p>
	 * <ol>
	 * <li>If the class name starts with two or more uppercase letters, it is
	 * left alone (e.g., "HTMLScreen" would remain "HTMLScreen").</li>
	 * <li>Otherwise, if the class name only begins with one uppercase letter
	 * (or the first is already lowercase), the first letter is made to be
	 * lowercase (e.g., "FooScreen" becomes "fooScreen").</li>
	 * </ol>
	 * 
	 * @param klass the name of the class
	 * @return a method name prefix corresponding to the class
	 */
	public static String classToMethod(Class<?> klass)
	{
		String name = klass.getSimpleName();
		
		if (name.length() > 1
				&& Character.isUpperCase(name.charAt(0))
				&& Character.isUpperCase(name.charAt(1)))
		{
			return name;
		}
		else
		{
			return Character.toLowerCase(name.charAt(0)) + name.substring(1);
		}
	}

	
	// ----------------------------------------------------------
	/**
	 * <p>
	 * Gets a list of possible resource names for a given class, in the order
	 * that they should be searched. Currently there are two possibilities
	 * (using "FooScreen" as an example):
	 * </p>
	 * <ol>
	 * <li>lowercase ("fooscreen")</li>
	 * <li>lowercase with underscores separating words ("foo_screen"), where
	 * words start with uppercase letters</li>
	 * </ol>
	 * 
	 * @param klass the name of the class
	 * @return a {@link List} of strings representing possible resource names
	 */
	public static List<String> classToResources(Class<?> klass)
	{
		String name = klass.getSimpleName();

		return Arrays.asList(
				name.toLowerCase(),
				camelCaseToUnderscore(name));
	}	


	// ----------------------------------------------------------
	/**
	 * Converts a camel-cased name (like "MyCoolScreen") to a lowercase name
	 * separated by underscores ("my_cool_screen").
	 * 
	 * TODO support sequences of uppercase letters; maybe we can borrow some
	 * logic from Ruby to make this robust.
	 * 
	 * @param name the camel-cased name
	 * @return the lowercase and underscored name
	 */
	public static String camelCaseToUnderscore(String name)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < name.length(); i++)
		{
			char ch = name.charAt(i);
			
			if (Character.isUpperCase(ch))
			{
				if (i > 0)
				{
					buffer.append('_');
				}
				
				buffer.append(Character.toLowerCase(ch));
			}
			else
			{
				buffer.append(ch);
			}
		}

		return buffer.toString();
	}
}
