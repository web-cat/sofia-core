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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

//-------------------------------------------------------------------------
/**
 * <p>
 * Allows the user to choose a piece of media (such as an image or a video)
 * from the media gallery on his or her device.
 * </p><p>
 * This class should be used from an event handler in a {@link Screen}
 * subclass. Display the media chooser by calling the {@link #start(Activity)}
 * method. When a selection is made, the {@code mediaWasChosen} method will be
 * called on the screen, if it exists. For example:
 * </p>
 * <pre>
 * public class MyScreen extends Screen
 * {
 *     public void buttonClicked()
 *     {
 *         MediaChooser chooser = new MediaChooser();
 *         chooser.start(this);
 *     }
 * 
 *     public void <b>mediaWasChosen</b>(MediaChooser chooser)
 *     {
 *         // Do something with the media by calling chooser.getBitmap(),
 *         // chooser.getUri(), or chooser.getPath().
 *     }
 * }</pre>
 * 
 * @author Tony Allevato
 */
public class MediaChooser extends AbsActivityStarter
{
	//~ Fields ................................................................

	private static final String DEFAULT_METHOD_NAME = "mediaWasChosen";

	private String type;
    private Uri uri;
    private String path;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new media chooser that, by default, has the content type
     * {@code image/*}.
     */
	public MediaChooser()
	{
		type = "image/*";
	}
	

	//~ Methods ...............................................................
	
    // ----------------------------------------------------------
	/**
	 * Gets the content type of the acceptable kinds of media that can be
	 * selected by this media chooser.
	 * 
	 * @return the content type for this media chooser
	 */
	public String getType()
	{
		return type;
	}


	// ----------------------------------------------------------
	/**
	 * <p>
	 * Sets the content type (also known as the MIME type or internet media
	 * type) of the acceptable kinds of media that can be selected by this
	 * media chooser. This content type can be a specific individual type
	 * (such as {@code image/jpeg}) or include a wildcard to support multiple
	 * related types (e.g., {@code image/*} or {@code video/*}).
	 * </p><p>
	 * More information about content types can be found in the Wikipedia
	 * article <a href="http://en.wikipedia.org/wiki/Internet_media_type">
	 * Internet media types</a>. 
	 * </p>
	 * 
	 * @param newType the MIME type for this media chooser
	 */
	public void setType(String newType)
	{
		this.type = newType;
	}


	// ----------------------------------------------------------
	/**
	 * Starts the media chooser. When the user has chosen an item from the
	 * media gallery, the owning {@code Activity} (or {@code Screen}) will
	 * have its {@code mediaWasChosen} method called.
	 * 
	 * @param owner the activity or screen that owns this media chooser and
	 *     will receive a notification when a media item is chosen
	 */
	@Override
	public void start(Activity owner)
	{
		// This is overridden here for Javadoc purposes.
		super.start(owner);
	}


	// ----------------------------------------------------------
	/**
	 * Starts the media chooser. When the user has chosen an item from the
	 * media gallery, the owning {@code Activity} (or {@code Screen}) will
	 * have the method with the name specified by {@code method} called.
	 * 
	 * @param owner the activity or screen that owns this media chooser and
	 *     will receive a notification when a media item is chosen
	 * @param method the name of the method that will be called on
	 *     {@code owner} when a media item is chosen
	 */
	public void start(Activity owner, String method)
	{
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);

		startActivityForResult(
				owner, method,
				Intent.createChooser(intent, "Select an image"));
	}
	
	
    // ----------------------------------------------------------
	/**
	 * Gets the {@link Uri} (uniform resource identifier) of the media that was
	 * chosen in the media chooser.
	 * 
	 * @return the {@link Uri} of the media that was chosen
	 */
	public Uri getUri()
	{
		return uri;
	}
	
	
    // ----------------------------------------------------------
	/**
	 * Gets the file system path of the media that was chosen in the media
	 * chooser.
	 * 
	 * @return the file system path of the media that was chosen
	 */
	public String getPath()
	{
        return path;
	}


    // ----------------------------------------------------------
	/**
	 * A convenience method that returns the chosen image as a {@code Bitmap}
	 * if the selected media was an image. Otherwise, the method returns null.
	 * 
	 * @return a {@code Bitmap} that represents the image that was chosen, or
	 *     null if it was not an image
	 */
	public Bitmap getBitmap()
	{
        return BitmapFactory.decodeFile(getPath());
	}


    // ----------------------------------------------------------
	public void handleActivityResult(
			Activity owner, Intent data, int requestCode, int resultCode)
	{
        if (resultCode == Activity.RESULT_OK)
        {
            uri = data.getData();
            path = MediaUtils.pathForMediaUri(owner.getContentResolver(), uri);
        }

		super.handleActivityResult(owner, data, requestCode, resultCode);
	}


    // ----------------------------------------------------------
	protected String getDefaultCallback()
	{
		return DEFAULT_METHOD_NAME;
	}
}
