package sofia.app.internal;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

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
 * @version 2012.04.30
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
