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

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

//-------------------------------------------------------------------------
/**
 * <p>
 * <em>This class is not intended for public use.</em>
 * </p><p>
 * Provides support to read additional types from Yaml-formatted streams, when
 * those types would not be supported natively (due to a lack of parameterless
 * constructor, for example).
 * </p>
 * 
 * @author Tony Allevato
 */
class SofiaRepresenter extends Representer
{
	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	public SofiaRepresenter()
	{
		this.representers.put(null, new RepresentAnything());
	}
	

	//~ Inner classes .........................................................

	// ----------------------------------------------------------
	/**
	 * Checks to see if a custom persistor is available for an object and uses
	 * that to persist it if found; otherwise, the default field-scraping
	 * approach is used.
	 */
	private class RepresentAnything extends Representer.RepresentJavaBean
	{
		// ----------------------------------------------------------
		@Override
		public Node representData(Object data)
		{
			Method method =
					PersistenceManager.getRepresentMethod(data.getClass());

			if (method != null)
			{
				try
				{
					HashMap<String, Object> rep = new HashMap<String, Object>();
					method.invoke(null, data, rep);
	
					return representSequence(
							new Tag("!custom"),
							Arrays.asList(new Object[] {
								data.getClass().getCanonicalName(), rep
							}), true);
				}
				catch (Exception e)
				{
					// Do nothing.
				}
			}

			return super.representData(data);
		}
	}
}
