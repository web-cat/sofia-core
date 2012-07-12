package sofia.content;

import sofia.app.ActivityStarter;
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
 * public void buttonClicked()
 * {
 *     MediaChooser chooser = new MediaChooser();
 *     chooser.start(this);
 * }
 * 
 * public void <b>mediaWasChosen</b>(MediaChooser chooser)
 * {
 *     // Do something with the media by calling chooser.getBitmap(),
 *     // chooser.getUri(), or chooser.getPath().
 * }
 * </pre>
 * 
 * @author Tony Allevato
 */
public class MediaChooser extends ActivityStarter
{
	//~ Instance/static variables .............................................

	private String type;
    private Uri uri;
    private String path;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
	public MediaChooser()
	{
		type = "image/*";
	}
	

	//~ Methods ...............................................................
	
    // ----------------------------------------------------------
	protected String getDefaultCallback()
	{
		return "mediaWasChosen";
	}


    // ----------------------------------------------------------
	public String getType()
	{
		return type;
	}


	// ----------------------------------------------------------
	public void setType(String newType)
	{
		this.type = newType;
	}


	// ----------------------------------------------------------
	public void start(Activity owner, String callback)
	{
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);

		startActivityForResult(
				owner, callback,
				Intent.createChooser(intent, "Select an image"));
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
