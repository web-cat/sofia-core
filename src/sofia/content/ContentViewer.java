package sofia.content;

import sofia.app.ActivityStarter;
import sofia.app.Screen;
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
 * public void buttonClicked()
 * {
 *     new ContentViewer(uri).start(this);
 * }
 * 
 * public void <b>contentViewerFinished</b>(ContentViewer viewer)
 * {
 *     // Do something when the viewer closes, if desired.
 * }
 * </pre>
 * 
 * @author Tony Allevato
 */
public class ContentViewer extends ActivityStarter
{
	//~ Instance/static variables .............................................

    private Uri uri;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
	public ContentViewer(Uri uri)
	{
		this.uri = uri;
	}
	

	//~ Methods ...............................................................
	
    // ----------------------------------------------------------
	protected String getDefaultCallback()
	{
		return "contentViewerFinished";
	}


	// ----------------------------------------------------------
	public void start(Activity owner, String callback)
	{
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

		startActivityForResult(owner, callback, intent);
	}

	
    // ----------------------------------------------------------
	public Uri getUri()
	{
		return uri;
	}
}
