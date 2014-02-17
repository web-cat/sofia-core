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

package sofia.content;

import sofia.app.Screen;
import sofia.app.internal.AbsActivityStarter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

//-------------------------------------------------------------------------
/**
 * <p>
 * Presents a full-screen viewer for content on the device. The content to
 * display is denoted by its {@link Uri}; it can be any kind of content that
 * supports the {@code ACTION_VIEW} intent, such as an image, video, sound,
 * contact card, and many others.
 * </p><p>
 * This class should be used from an event handler in a {@link Screen}-like
 * class. Display the content by calling the {@link #start(Activity)}
 * method. When the user returns from viewing the content, the
 * {@code contentViewerFinished} method will be called on the screen, if it
 * exists. For example:
 * </p>
 * <pre>
 * public class MyScreen extends Screen
 * {
 *     public void buttonClicked()
 *     {
 *         new ContentViewer(uri).start(this);
 *     }
 * 
 *     public void <b>contentViewerFinished</b>(ContentViewer viewer)
 *     {
 *         // Do something when the viewer closes, if desired.
 *     }
 * }</pre>
 * 
 * @author Tony Allevato
 */
public class ContentViewer extends AbsActivityStarter
{
	//~ Fields ................................................................

	private static final String DEFAULT_METHOD_NAME = "contentViewerFinished";

    private Uri uri;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new content viewer that will display the content at the
     * specified {@link Uri}.
     * 
     * @param uri the {@link Uri} of the content that will be displayed by this
     *     viewer
     */
	public ContentViewer(Uri uri)
	{
		this.uri = uri;
	}
	

	//~ Methods ...............................................................
	
    // ----------------------------------------------------------
	/**
	 * Gets the {@link Uri} of the content that will be displayed by this
	 * viewer.
	 * 
	 * @return the {@link Uri} to the content that will be displayed by this
	 *     viewer
	 */
	public Uri getUri()
	{
		return uri;
	}


	// ----------------------------------------------------------
	/**
	 * Sets the {@link Uri} of the content that will be displayed by this
	 * viewer.
	 * 
	 * @param newUri the {@link Uri} to the content that will be displayed by
	 *     this viewer
	 */
	public void setUri(Uri newUri)
	{
		uri = newUri;
	}


	// ----------------------------------------------------------
	/**
	 * Starts the content viewer. When the user dismisses the viewer, the
	 * owning {@code Activity} (or {@code Screen}) will have its
	 * {@code contentViewerFinished} method called.
	 * 
	 * @param owner the activity or screen that owns this content viewer and
	 *     will receive a notification when it is dismissed
	 */
	@Override
	public void start(Activity owner)
	{
		// This is overridden here for Javadoc purposes.
		super.start(owner);
	}


	// ----------------------------------------------------------
	/**
	 * Starts the content viewer. When the user dismisses the viewer, the
	 * owning {@code Activity} (or {@code Screen}) will have the method with
	 * the name specified by {@code method} called.
	 * 
	 * @param owner the activity or screen that owns this content viewer and
	 *     will receive a notification when it is dismissed
	 * @param method the name of the method that will be called on
	 *     {@code owner} when the content viewer is dismissed
	 */
	public void start(Activity owner, String method)
	{
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

		startActivityForResult(owner, method, intent);
	}

	
    // ----------------------------------------------------------
	protected String getDefaultCallback()
	{
		return DEFAULT_METHOD_NAME;
	}
}
