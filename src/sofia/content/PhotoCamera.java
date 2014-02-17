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
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

//-------------------------------------------------------------------------
/**
 * <p>
 * Allows the user to take a photo using the camera on his or her device.
 * </p><p>
 * This class should be used from an event handler in a {@link Screen}
 * subclass. Display the camera by calling the {@link #start(Activity)}
 * method. When a photo is taken and saved, the {@code photoWasTaken} method
 * will be called on the screen, if it exists. For example:
 * </p>
 * <pre>
 * public class MyScreen extends Screen
 * {
 *     public void buttonClicked()
 *     {
 *         PhotoCamera camera = new PhotoCamera();
 *         camera.start(this);
 *     }
 * 
 *     public void <b>photoWasTaken</b>(PhotoCamera camera)
 *     {
 *         // Do something with the image by calling camera.getBitmap(),
 *         // camera.getUri(), or camera.getPath().
 *     }
 * }</pre>
 * 
 * @author Tony Allevato
 */
public class PhotoCamera extends AbsActivityStarter
{
	//~ Fields ................................................................

	private static final String DEFAULT_METHOD_NAME = "photoWasTaken";

	private Uri uri;
    private String filename;
    private String path;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new photo camera.
     */
	public PhotoCamera()
	{
		// Do nothing. Constructor exists for Javadoc purposes.
	}
	
	
	//~ Methods ...............................................................

    // ----------------------------------------------------------
	/**
	 * Gets the {@link Uri} (uniform resource identifier) of the photo that was
	 * taken by the camera.
	 * 
	 * @return the {@link Uri} of the photo that was taken
	 */
	public Uri getUri()
	{
		return uri;
	}
	
	
    // ----------------------------------------------------------
	/**
	 * Gets the file system path of the photo that was taken by the camera.
	 * 
	 * @return the file system path of the photo that was taken
	 */
	public String getPath()
	{
        return path;
	}


    // ----------------------------------------------------------
	/**
	 * A convenience method that returns the photo as a {@code Bitmap}.
	 * 
	 * @return a {@code Bitmap} that represents the photo that was taken
	 */
	public Bitmap getBitmap()
	{
        return BitmapFactory.decodeFile(getPath());
	}


	// ----------------------------------------------------------
	/**
	 * Displays the camera application. When the user has taken a photo, the
	 * owning {@code Activity} (or {@code Screen}) will have its
	 * {@code photoWasTaken} method called.
	 * 
	 * @param owner the activity or screen that owns this photo camera that
	 *     will receive a notification when a photo is taken
	 */
	@Override
	public void start(Activity owner)
	{
		// This is overridden here for Javadoc purposes.
		super.start(owner);
	}


    // ----------------------------------------------------------
	/**
	 * Displays the camera application. When the user has taken a photo, the
	 * owning {@code Activity} (or {@code Screen}) will have the method with
	 * the name specified by {@code method} called.
	 * 
	 * @param owner the activity or screen that owns this photo camera that
	 *     will receive a notification when a photo is taken
	 * @param method the name of the method that will be called on
	 *     {@code owner} when a photo is taken
	 */
	public void start(Activity owner, String callback)
	{
        filename = System.currentTimeMillis() + ".jpg";
        File file = getTempImageFile(owner, filename);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

		startActivityForResult(owner, callback, intent);
	}
	
	
	// ----------------------------------------------------------
	protected String getDefaultCallback()
	{
		return DEFAULT_METHOD_NAME;
	}


    // ----------------------------------------------------------
    private File getTempImageFile(Activity owner, String name)
    {
        File dir = new File(Environment.getExternalStorageDirectory(),
            owner.getPackageName());

        if (!dir.exists())
        {
            dir.mkdir();
        }

        return new File(dir, name);
    }


    // ----------------------------------------------------------
	public void handleActivityResult(
			Activity owner, Intent data, int requestCode, int resultCode)
	{
        if (resultCode == Activity.RESULT_OK)
        {
            uri = Uri.fromFile(getTempImageFile(owner, filename));
            path = MediaUtils.pathForMediaUri(
                owner.getContentResolver(), uri);
        }

		super.handleActivityResult(owner, data, requestCode, resultCode);
	}
}
