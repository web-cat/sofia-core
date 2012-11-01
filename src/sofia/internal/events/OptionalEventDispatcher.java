package sofia.internal.events;

import java.lang.reflect.Array;
import java.util.List;

//-------------------------------------------------------------------------
/**
 * An event dispatcher that allows some of the rightmost arguments to be
 * optional. For example, if the event dispatcher is created with a minimum
 * argument count of 1 and it is invoked with the arguments (arg1, arg2, arg3),
 * then it will attempt to dispatch the event using any of the following three
 * signatures:
 * <ul>
 * <li>arg1, arg2, arg3</li>
 * <li>arg1, arg2</li>
 * <li>arg1</li>
 * </ul>
 * <p>
 * If you require more permutability of the arguments than this simple
 * rightmost exclusion provides, or to combine it with other kinds of
 * transformations, then you should create your own subclass of
 * {@link EventDispatcher} to contain that logic.
 * </p>
 * 
 * @author  Tony Allevato
 * @version 2012.11.01
 */
public class OptionalEventDispatcher extends EventDispatcher
{
	//~ Fields ................................................................

	private int minimumArgCount;


	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	/**
	 * Creates an {@code OptionalEventDispatcher} where all of the arguments
	 * are optional.
	 * 
	 * @param method the name of the method to invoke
	 */
	public OptionalEventDispatcher(String method)
	{
		this(method, 0);
	}

	
	// ----------------------------------------------------------
	/**
	 * Creates an {@code OptionalEventDispatcher} that must have at least the
	 * specified number of arguments, but where those after that are optional.
	 * 
	 * @param method the name of the method to invoke
	 * @param minimumArgCount the minimum number of arguments that a handler
	 *     method must take in order to match
	 */
	public OptionalEventDispatcher(String method, int minimumArgCount)
	{
		super(method);
		
		this.minimumArgCount = minimumArgCount;
	}


	//~ Protected methods .....................................................

	// ----------------------------------------------------------
	@Override
	protected List<MethodTransformer> lookupTransformers(
			Object receiver, List<Class<?>> argTypes)
	{
		List<MethodTransformer> descriptors =
				super.lookupTransformers(receiver, argTypes);

		Class<?>[] argTypeArray = new Class<?>[argTypes.size()];
		argTypes.toArray(argTypeArray);

		for (int i = argTypes.size(); i >= minimumArgCount; i--)
		{
			final int thisCount = i;

			MethodTransformer reverseTransformer =
					new MethodTransformer(
							firstN(argTypeArray, thisCount, Class.class))
			{
				protected Object[] transform(Object... args)
				{
					return firstN(args, thisCount, Object.class);
				}			
			};

			reverseTransformer.addIfSupportedBy(receiver, descriptors);
		}

		return descriptors;
	}

	
	//~ Private methods .......................................................

	// ----------------------------------------------------------
	@SuppressWarnings("unchecked")
	private static <T> T[] firstN(T[] array, int n, Class<T> klass)
	{
		T[] firstN = (T[]) Array.newInstance(klass, n);
		System.arraycopy(array, 0, firstN, 0, n);
		return firstN;
	}
}
