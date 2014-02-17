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

//-------------------------------------------------------------------------
/**
 * Subclasses of this class can be "injected" into a screen in order to execute
 * code at key points during the screen's lifecycle. Mainly intended for
 * internal use.
 * 
 * @author Tony Allevato
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
