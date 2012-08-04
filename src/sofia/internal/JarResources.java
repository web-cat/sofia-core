package sofia.internal;

import java.io.InputStream;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * <p>
 * Since we want to distribute Sofia as a single JAR (or a set of JARs), we
 * cannot make use of the standard Android resource structure that regular
 * APKs use, because all of the resources would have to be copied into the
 * application projects.
 * </p><p>
 * Instead, for things like images, we store them embedded in the JARs, and
 * this class provides a better interface for accessing them.
 * </p>
 *
 * @author Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/03 21:12 $
 */
public class JarResources
{
	//~ Methods ...............................................................

	// ----------------------------------------------------------
	/**
	 * Gets an input stream for an image resource, stored in a package
	 * relative to the specified class, and taking the current device's DPI
	 * into account. The package containing the class parameter should have a
	 * subpackage named "images", with its own subpackages that match the DPI
	 * identifiers used in resources, like "hdpi" and "mdpi", inside which the
	 * image files are held.
	 *
	 * @param context The context for determining the display resolution,
	 *                and also the application's package, if the image isn't
	 *                found in the klass' package.
	 * @param klass   The class representing the package where the images are
	 *                located.  The search will also look in the application
	 *                package determined by the context if no image is found
	 *                with respect to the klass' package (or if klass is null).
	 * @param name    The name of the image file, including its extension.
	 * @return An {@code InputStream} containing the image data, or null if no
	 *     image could be found.
	 */
	public static InputStream getImageStream(
	    Context context, Class<?> klass, String name)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();

		// Pattern is declared outside the loop because it is intended to
		// be used after the loop
        int pattern = 0;
		for (; pattern < CUTOFF.length; pattern++)
		{
		    if (metrics.densityDpi < CUTOFF[pattern])
		    {
		        break;
		    }
		}
		// pattern now contains the search pattern, 0-3.  If no pattern
		// was found in CUTOFF, pattern == CUTOFF.length == 3, which
		// defaults to the xhdpi pattern.

		// First, try to find image using the package of the given class
		InputStream result = null;
		if (klass != null)
		{
    		for (String attempt : SEARCH_PATTERN[pattern])
    		{
    			result = klass.getResourceAsStream(
    			    "images/" + attempt + "/" + name);

    			if (result != null)
    			{
    				return result;
    			}
    		}

    		// If we make it here, try for the default (no density) name
    		result = klass.getResourceAsStream("images/" + name);
    		if (result != null)
    		{
    		    return result;
    		}
		}

		// OK, now search using the app/context package instead of
		// klass' package
		String base = context.getPackageName().replace('.', '/');
		if (!base.endsWith("/"))
		{
		    base += "/";
		}
		base += "images/";
		ClassLoader loader = (klass == null)
		    ? JarResources.class.getClassLoader()
		    : klass.getClass().getClassLoader();
        for (String attempt : SEARCH_PATTERN[pattern])
        {
            result = loader.getResourceAsStream(base + attempt + "/" + name);

            if (result != null)
            {
                return result;
            }
        }
        // If we make it here, try for the default (no density) name
        return loader.getResourceAsStream(base + name);
	}


	// See http://developer.android.com/guide/practices/screens_support.html
	// Values based on info in Table 1 on that page.
	private static final int[] CUTOFF = {
	    140,    // upper limit for ldpi, which is ~120dpi
	    200,    // upper limit for mdpi, which is ~160dpi
	    280     // upper limit for hdpi, which is ~240dpi
	            // 400 is upper limit for xhdpi, which is ~320dpi
	            // don't worry about xxhigh, since xhdpi should scale well
	};

	// Search starts with the preferred resolution, and then goes high-to-low
	// on the assumption that higher res images scale down better than
	// attempting to scale up lower res images.
	private static final String[][] SEARCH_PATTERN = {
	    { "ldpi", "xhdpi", "hdpi", "mdpi" },  // for ldpi screens
        { "mdpi", "xhdpi", "hdpi", "ldpi" },  // for mdpi screens
        { "hdpi", "xhdpi", "mdpi", "ldpi" },  // for hdpi screens
        { "xhdpi", "hdpi", "mdpi", "ldpi" }   // for xhdpi screens
	};
}
