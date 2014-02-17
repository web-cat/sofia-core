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

import java.util.ArrayList;
import java.util.List;

//-------------------------------------------------------------------------
/**
 * TODO document
 * 
 * @author Tony Allevato
 */
public class ReversibleEventDispatcher extends EventDispatcher
{
	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	public ReversibleEventDispatcher(String method)
	{
		super(method);
	}
	
	
	//~ Protected methods .....................................................

	// ----------------------------------------------------------
	@Override
	protected List<MethodTransformer> lookupTransformers(
			Object receiver, List<Class<?>> argTypes)
	{
		List<MethodTransformer> descriptors =
				super.lookupTransformers(receiver, argTypes);

		MethodTransformer reverseTransformer =
				new MethodTransformer(reverse(argTypes))
		{
			protected Object[] transform(Object... args)
			{
				return reverse(args);
			}			
		};

		reverseTransformer.addIfSupportedBy(receiver, descriptors);

		return descriptors;
	}

	
	//~ Private methods .......................................................

	// ----------------------------------------------------------
	private static <T> List<T> reverse(List<T> list)
	{
		ArrayList<T> reversed = new ArrayList<T>(list.size());
		
		for (int i = list.size() - 1; i >= 0; i--)
		{
			reversed.add(list.get(i));
		}
		
		return reversed;
	}


	// ----------------------------------------------------------
	private static Object[] reverse(Object... objects)
	{
		Object[] reversed = new Object[objects.length];
		
		for (int i = 0; i < objects.length; i++)
		{
			reversed[objects.length - i - 1] = objects[i];
		}
		
		return reversed;
	}
}
