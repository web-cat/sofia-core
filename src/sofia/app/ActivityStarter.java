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

package sofia.app;

import sofia.app.internal.AbsActivityStarter;
import sofia.app.internal.ScreenMixin;
import sofia.internal.NameUtils;
import sofia.internal.events.EventDispatcher;

import android.app.Activity;
import android.content.Intent;

//---------------------------------------------------------------
/**
 * <p>
 * Starts another screen or activity for the user and slides it into view. This
 * class can be used in one of two ways:
 * </p>
 * <ol>
 * <li>To start a subclass of {@code Screen} (or {@code MapScreen}), you create
 * an {@code ActivityStarter} and pass it the class that should be started and
 * the arguments that you want to pass to its {@code initialize} method. When
 * the activity returns, a callback will be called based on the name of the
 * activity class.</li>
 * <li>To start a traditional Android activity based on an {@link Intent}, pass
 * the {@link Intent} object and the name of a callback method that will be
 * called when the activity returns.</li>
 * </ol>
 * <p>
 * Users can use this class directly but should probably prefer the
 * {@link Screen#presentScreen(Class, Object...)} and
 * {@link Screen#presentActivity(Intent, String)} methods instead.
 * </p>
 *  
 * @author Tony Allevato
 */
public class ActivityStarter extends AbsActivityStarter
{
	//~ Fields ................................................................

	private Class<? extends Activity> screenClass;
	private Object[] params;
	private Intent intent;
	private String callback;

	
	//~ Constructors ..........................................................
	
	// ----------------------------------------------------------
	public ActivityStarter(Intent intent, String callback)
	{
		this.intent = intent;
		this.callback = callback;
	}


	// ----------------------------------------------------------
	public ActivityStarter(Class<? extends Activity> screenClass,
			Object... params)
	{
		this.screenClass = screenClass;
		this.params = params;
	}


	//~ Public methods ........................................................

	// ----------------------------------------------------------
	@Override
	public void start(Activity owner, String callback)
	{
		Intent theIntent;

		if (intent != null)
		{
			theIntent = intent;
		}
		else
		{
	        theIntent = new Intent(owner, screenClass);
	        theIntent.putExtra(ScreenMixin.SCREEN_ARGUMENTS,
	            ScreenMixin.registerScreenArguments(params));
		}

        startActivityForResult(owner, callback, theIntent);
	}


	//~ Protected methods .....................................................

	// ----------------------------------------------------------
	@Override
	protected String getDefaultCallback()
	{
		return NameUtils.classToMethod(screenClass) + "Finished";
	}	


	// ----------------------------------------------------------
	protected String getCanceledCallback()
	{
		return NameUtils.classToMethod(screenClass) + "Canceled";
	}	


	// ----------------------------------------------------------
	@Override
	protected void invokeCallback(Activity owner, Intent data, int resultCode)
	{
		if (intent == null)
		{
			Object result = ScreenMixin.takeScreenResult(data);

			if (resultCode == Activity.RESULT_CANCELED)
			{
		        EventDispatcher event =
		        		new EventDispatcher(getCanceledCallback());
		        event.dispatch(owner);
			}
			else
			{
		        EventDispatcher event =
		        		new EventDispatcher(getDefaultCallback());
		        event.dispatch(owner, result);
			}
		}
		else
		{
			EventDispatcher event = new EventDispatcher(callback);
	        event.dispatch(owner, data, resultCode);
		}
	}
}
