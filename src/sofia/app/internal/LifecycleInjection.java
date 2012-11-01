package sofia.app.internal;

//-------------------------------------------------------------------------
/**
 * Subclasses of this class can be "injected" into a screen in order to execute
 * code at key points during the screen's lifecycle. Mainly intended for
 * internal use.
 * 
 * @author  Tony Allevato
 * @version 2012.11.01
 */
public abstract class LifecycleInjection
{
	//~ Methods ...............................................................

	// ----------------------------------------------------------
	/**
	 * Called when the screen is paused.
	 */
	public void pause()
	{
		// Do nothing by default.
	}


	// ----------------------------------------------------------
	/**
	 * Called when the screen is resumed.
	 */
	public void resume()
	{
		// Do nothing by default.		
	}


	// ----------------------------------------------------------
	/**
	 * Called when the screen is destroyed.
	 */
	public void destroy()
	{
		// Do nothing by default.
	}
}
