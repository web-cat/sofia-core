package sofia.internal;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * @version $Date: 2012/08/04 15:52 $
 */
public class JarResources
{
	//~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Get an image resource by name, taking the current device's DPI into
     * account.  The image may be a traditional Android resource, in which
     * case the normal resource mechanism is used to look it up, or it may
     * be stored in the application/context package.  If stored in the
     * application's package, the package should have a subpackage named
     * "images", with one or more of its own subpackages that match the DPI
     * identifiers used in resources: "ldpi", "mdpi", "hdpi", and/or "xhdpi".
     * Image files are held in these dpi-based subpackages.  The "images"
     * subpackage itself will also be searched, with images it contains
     * treated at the same DPI as the device.
     *
     * @param context The context for determining the display resolution,
     *                and also the application's package.
     * @param name    The name of the image file, including its extension.
     * @return A {@code Bitmap} containing the image, or null if no
     *     image could be found.
     */
    public static Bitmap getBitmap(Context context, String name)
    {
        return getBitmap(context, null, name);
    }


    // ----------------------------------------------------------
	/**
     * Get an image resource by name, taking the current device's DPI into
     * account.  The image may be a traditional Android resource, in which
     * case the normal resource mechanism is used to look it up, or it may
     * be stored in the application/context package.  Alternatively, the
     * image may be stored in a package relative to the specified class
     * (or application). The package containing the class parameter should
     * have a subpackage named "images", with one or more of its own
     * subpackages that match the DPI identifiers used in resources:
     * "ldpi", "mdpi", "hdpi", and/or "xhdpi". Image files are held in these
     * dpi-based subpackages.  The "images" subpackage itself will also be
     * searched, with images it contains treated at the same DPI as the device.
	 *
	 * @param context The context for determining the display resolution,
	 *                and also the application's package, if the image isn't
	 *                found in package of the specified klass.
	 * @param klass   The class representing the package where the images are
	 *                located.  The search will also look in the application
	 *                package determined by the context if no image is found
	 *                with respect to klass' package (or if klass is null).
	 * @param name    The name of the image file, including its extension.
	 * @return An {@code Bitmap} containing the image, or null if no
	 *     image could be found.
	 */
	public static Bitmap getBitmap(
	    Context context, Class<?> klass, String name)
	{
	    return getBitmap(context, klass, name, true, true);
    }


    // ----------------------------------------------------------
    /**
     * Get an image resource by name, taking the current device's DPI into
     * account.  The image may be a traditional Android resource, in which
     * case the normal resource mechanism is used to look it up, or it may
     * be stored in the application/context package.  Alternatively, the
     * image may be stored in a package relative to the specified class
     * (or application). The package containing the class parameter should
     * have a subpackage named "images", with one or more of its own
     * subpackages that match the DPI identifiers used in resources:
     * "ldpi", "mdpi", "hdpi", and/or "xhdpi". Image files are held in these
     * dpi-based subpackages.  The "images" subpackage itself will also be
     * searched, with images it contains treated at the same DPI as the device.
     *
     * @param context The context for determining the display resolution,
     *                and also the application's package, if needed.
     * @param klass   The class representing the package where the images are
     *                located.
     * @param name    The name of the image file, including its extension.
     * @param searchAppPkg If true, and no image is found with respect to
     *                klass' package (or if klass is null), the search will
     *                also look in the application package determined by the
     *                context.  If false, the application package will not
     *                be searched.
     * @param scaleForDpi If true, the loaded image will be automatically
     *                upscaled or downscaled for the current device display
     *                density by the BitmapFactory.  If false, the image will
     *                be loaded at its stored resolution regardless of the
     *                current display density.
     * @return An {@code Bitmap} containing the image, or null if no
     *     image could be found.
     */
    public static Bitmap getBitmap(
        Context context, Class<?> klass, String name,
        boolean searchAppPkg, boolean scaleForDpi)
    {
        BitmapFactory.Options bfo = null;
        if (!scaleForDpi)
        {
            bfo = new BitmapFactory.Options();
            bfo.inScaled = false;
        }

        boolean hasExtension = false;
        String nameWithoutExt = name;
        {
            // trim file extension, if present
            int pos = name.lastIndexOf('.');
            if (pos >= 0)
            {
                nameWithoutExt = name.substring(0, pos);
                hasExtension = true;
            }
        }

        // First, try for a resource by this name:
	    int id = context.getResources().getIdentifier(
	        nameWithoutExt, "drawable", context.getPackageName());
	    if (id != 0)
	    {
	        return BitmapFactory.decodeResource(
	            context.getResources(), id, bfo);
	    }

	    // If no resource was found ...
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int foundDensity = -1;

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
		InputStream stream = null;
		if (klass != null)
		{
    		for (int attempt : SEARCH_PATTERN[pattern])
    		{
    		    String dir = "images/" + DENSITY_NAME[attempt] + "/";
    		    if (hasExtension)
    		    {
    		        stream = klass.getResourceAsStream(dir + name);
    		    }
    		    else
    		    {
    	            for (String extension : EXTENSIONS)
    	            {
                        stream =
                            klass.getResourceAsStream(dir + name + extension);
                        if (stream != null)
                        {
                            break;
                        }
    	            }
    		    }

    			if (stream != null)
    			{
    			    foundDensity = attempt;
    			    break;
    			}
    		}

    		if (stream == null)
    		{
    		    // If we make it here, try for the default (no density) name
    		    stream = klass.getResourceAsStream("images/" + name);
    		}
		}

		if (stream == null)
		{
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
		    for (int attempt : SEARCH_PATTERN[pattern])
		    {
		        String dir = base + DENSITY_NAME[attempt] + "/";

                if (hasExtension)
                {
                    stream = loader.getResourceAsStream(dir + name);
                }
                else
                {
                    for (String extension : EXTENSIONS)
                    {
                        stream =
                            loader.getResourceAsStream(dir + name + extension);
                        if (stream != null)
                        {
                            break;
                        }
                    }
                }

		        if (stream != null)
		        {
		            foundDensity = attempt;
		            break;
		        }
            }

		    if (stream == null)
		    {
		        // If we make it here, try for the default (no density) name
		        stream = loader.getResourceAsStream(base + name);
		    }
        }

		Bitmap result = null;
		if (stream != null)
		{
            if (foundDensity >= 0 && scaleForDpi)
            {
                bfo = new BitmapFactory.Options();
                bfo.inDensity = DENSITY[foundDensity];
            }
            else if (!scaleForDpi)
            {
                bfo = new BitmapFactory.Options();
                bfo.inScaled = false;
            }
		    result = BitmapFactory.decodeStream(stream, null, bfo);
		}
		return result;
	}


	// Map from ints 0-3 to corresponding density names here
    private static final String[] DENSITY_NAME = {
        "ldpi",
        "mdpi",
        "hdpi",
        "xhdpi"
    };

    // constants for the int codes
    private static final int LDPI  = 0;
    private static final int MDPI  = 1;
    private static final int HDPI  = 2;
    private static final int XHDPI = 3;

    // DPI density for each name
    private static final int[] DENSITY = {
        120,
        160,
        240,
        320
    };

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
	private static final int[][] SEARCH_PATTERN = {
	    { LDPI, XHDPI, HDPI, MDPI },  // for ldpi screens
        { MDPI, XHDPI, HDPI, LDPI },  // for mdpi screens
        { HDPI, XHDPI, MDPI, LDPI },  // for hdpi screens
        { XHDPI, HDPI, MDPI, LDPI }   // for xhdpi screens
	};

    private static final String[] EXTENSIONS = {
        ".png", ".PNG", ".gif", ".GIF", ".jpg", ".JPG", ".JPEG", ".JPEG"
    };
}
