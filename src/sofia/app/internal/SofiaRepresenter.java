package sofia.app.internal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

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
 * @version 2012.04.30
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
