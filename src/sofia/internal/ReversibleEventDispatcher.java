package sofia.internal;

import java.util.ArrayList;
import java.util.List;

//-------------------------------------------------------------------------
/**
 * TODO document
 * 
 * @author  Tony Allevato
 * @version 2012.10.24
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
