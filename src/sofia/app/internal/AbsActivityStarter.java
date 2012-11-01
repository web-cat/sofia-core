package sofia.app.internal;

import sofia.internal.events.EventDispatcher;
import android.app.Activity;
import android.content.Intent;

//-------------------------------------------------------------------------
/**
 * An abstract class that helps manage the presentation of built-in activities
 * (such as the media chooser or camera), handling the communication of the
 * results from those activities back to the caller.
 * 
 * @author  Tony Allevato
 * @version 2012.09.05
 */
public abstract class AbsActivityStarter
{
	//~ Instance/static variables .............................................

	private String callback;
	private boolean canceled;


	//~ Methods ...............................................................

	// ----------------------------------------------------------
	/**
	 * Starts the activity managed by this class. When the activity is
	 * completed, the owning {@code Activity} (or {@code Screen}) will have its
	 * default callback method called.
	 * 
	 * @param owner the activity or screen that owns this managed sub-activity
	 *     and will receive a notification when it is completed
	 */
	public void start(Activity owner)
	{
		start(owner, getDefaultCallback());
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Starts the activity managed by this class. When the activity is
	 * completed, the owning {@code Activity} (or {@code Screen}) will have its
	 * default callback method called. Subclasses must implement this method
	 * to spawn the sub-activity.
	 * 
	 * @param owner the activity or screen that owns this managed sub-activity
	 *     and will receive a notification when it is completed
	 * @param method the name of the method that should be called when the
	 *     sub-activity completes
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
	 * Gets a value indicating whether the sub-activity was canceled (by
	 * pressing the Back button, for example).
	 * 
	 * @return true if the sub-activity was canceled, or false if it completed
	 *     normally
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
		
		invokeCallback(owner, data, resultCode);
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Invokes the callback method for the sub-activity on the calling activity
	 * (the owner). The default behavior is to call the method whose name is
	 * returned by {@link #getCallback()} and to pass it a single argument, the
	 * instance of this {@code ActivityStarter} subclass that started the
	 * sub-activity. Subclasses can override this method if they need more
	 * flexibility in how the callback is invoked (for instance, by supporting
	 * different parameter lists).
	 *  
	 * @param owner the activity that started the sub-activity represented by
	 *     this object
	 */
	protected void invokeCallback(Activity owner, Intent data, int resultCode)
	{
		// TODO cache this?
        EventDispatcher event = new EventDispatcher(getCallback());
        event.dispatch(owner, this);		
	}
}
