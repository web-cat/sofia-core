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

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

//-------------------------------------------------------------------------
/**
 * <p>
 * <em>This class is not intended for public use.</em>
 * </p><p>
 * Provides support to load additional types from Yaml-formatted streams, when
 * those types would not be supported natively (due to a lack of parameterless
 * constructor, for example).
 * </p>
 * 
 * @author Tony Allevato
 */
class SofiaConstructor extends Constructor
{
	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	public SofiaConstructor()
	{
		this.yamlConstructors.put(new Tag("!custom"),
				new ConstructCustomRepresentation());
	}


	//~ Inner classes .........................................................

	// ----------------------------------------------------------
	/**
	 * Reconstructs an object that was previously persisted with a custom
	 * persistor.
	 */
	private class ConstructCustomRepresentation extends AbstractConstruct
	{
		// ----------------------------------------------------------
		@SuppressWarnings("unchecked")
		public Object construct(Node node)
		{
			List<? extends Object> list =
					constructSequence((SequenceNode) node);

			String className = (String) list.get(0);
			Map<String, Object> rep = (Map<String, Object>) list.get(1);

			try
			{
				Class<?> klass = Class.forName(className);
				Method method = PersistenceManager.getConstructMethod(klass);
				
				if (method != null)
				{
					return method.invoke(null, rep);
				}
			}
			catch (Exception e)
			{
				// Do nothing.
			}

			return null;
		}
	}
}
