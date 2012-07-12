package sofia.content;

import java.io.File;

import sofia.app.ActivityStarter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

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
 * public void buttonClicked()
 * {
 *     PhotoCamera camera = new PhotoCamera();
 *     camera.start(this);
 * }
 * 
 * public void <b>photoWasTaken</b>(PhotoCamera camera)
 * {
 *     // Do something with the image by calling camera.getBitmap(),
 *     // camera.getUri(), or camera.getPath().
 * }
 * </pre>
 * 
 * @author Tony Allevato
 */
public class PhotoCamera extends ActivityStarter
{
	//~ Instance/static variables .............................................

	private Uri uri;
    private String filename;
    private String path;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
	public PhotoCamera()
	{
	}
	
	
	//~ Methods ...............................................................

	// ----------------------------------------------------------
	protected String getDefaultCallback()
	{
		return "photoWasTaken";
	}


    // ----------------------------------------------------------
	public void start(Activity owner, String callback)
	{
        filename = System.currentTimeMillis() + ".jpg";
        File file = getTempImageFile(owner, filename);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

		startActivityForResult(owner, callback, intent);
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
	
	
    // ----------------------------------------------------------
	public Uri getUri()
	{
		return uri;
	}
	
	
    // ----------------------------------------------------------
	public String getPath()
	{
        return path;
	}


    // ----------------------------------------------------------
	public Bitmap getBitmap()
	{
        return BitmapFactory.decodeFile(getPath());
	}
}
