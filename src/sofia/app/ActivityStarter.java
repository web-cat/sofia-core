package sofia.app;

import sofia.app.internal.ScreenMixin;
import sofia.internal.MethodDispatcher;
import android.app.Activity;
import android.content.Intent;

//-------------------------------------------------------------------------
/**
 * An abstract class that helps manage the presentation of built-in activities
 * (such as the media chooser or camera), handling the communication of the
 * results from those activities back to the caller.
 * 
 * @author Tony Allevato
 */
public abstract class ActivityStarter
{
	//~ Instance/static variables .............................................

	private String callback;
	private boolean canceled;


	//~ Methods ...............................................................

	// ----------------------------------------------------------
	/**
	 *  
	 * @param owner
	 */
	public void start(Activity owner)
	{
		start(owner, getDefaultCallback());
	}
	
	
	// ----------------------------------------------------------
	/**
	 * 
	 * @param owner
	 * @param callback
	 */
	public abstract void start(Activity owner, String callback);

	
	// ----------------------------------------------------------
	protected final void startActivityForResult(
			Activity owner, String callback, Intent intent)
	{
		this.callback = callback;

		ScreenMixin internals = ScreenMixin.getMixin(owner);
		internals.startActivityForResult(this, intent,
				ScreenMixin.ACTIVITY_STARTER_REQUEST_CODE);
	}


	// ----------------------------------------------------------
	/**
	 * Subclasses should override this method to return the name of the method
	 * that will be called on the parent activity when the child activity ends,
	 * if one is not provided in the {@link #start(Activity, String)} method.
	 * 
	 * @return the name of the method to call when the child activity ends
	 */
	protected abstract String getDefaultCallback();


	// ----------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	protected String getCallback()
	{
		return callback;
	}


	// ----------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public boolean wasCanceled()
	{
		return canceled;
	}


	// ----------------------------------------------------------
	/**
	 * 
	 * @param owner
	 * @param data
	 * @param requestCode
	 * @param resultCode
	 */
	public void handleActivityResult(Activity owner,
			Intent data, int requestCode, int resultCode)
	{
		canceled = (resultCode == Activity.RESULT_CANCELED);

        MethodDispatcher dispatcher = new MethodDispatcher(getCallback(), 1);
        dispatcher.callMethodOn(owner, this);
	}
}
