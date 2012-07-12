package sofia.internal;

import java.io.InputStream;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * <p>
 * Since we want to distribute Sofia as a single JAR (or a set of JARs), we
 * cannot make use of the standard Android resource structure that regular APKs
 * use, because all of the resources would have to be copied into the
 * application projects.
 * </p><p>
 * Instead, for things like images, we store them embedded in the JARs, and
 * this class provides a better interface for accessing them.
 * </p>
 * 
 * @author Tony Allevato
 * @version 2012.05.07
 */
public class JarResources
{
	//~ Methods ...............................................................

	// ----------------------------------------------------------
	/**
	 * Gets an input stream for an image resource, stored in a package relative
	 * to the specified class, and taking the current device's DPI into
	 * account. The package containing the class parameter should have a
	 * subpackage named "images", with its own subpackages that match the DPI
	 * identifiers used in resources, like "hdpi" and "mdpi", inside which the
	 * image files are held.
	 * 
	 * @param context the context for determining the display resolution
	 * @param klass the class representing the package where the images are
	 *     located
	 * @param name the name of the image file, including its extension
	 * @return an {@code InputStream} containing the image data, or null if no
	 *     image could be found
	 */
	public static InputStream getImageStream(Context context, Class<?> klass,
			String name)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();

		String[] attempts;

		if (metrics.density < 160)
		{
			attempts = new String[] { "mdpi", "hdpi" };
		}
		else
		{
			attempts = new String[] { "hdpi", "mdpi" };
		}

		for (String attempt : attempts)
		{
			InputStream stream = klass.getResourceAsStream(
					"images/" + attempt + "/" + name);

			if (stream != null)
			{
				return stream;
			}
		}
		
		return null;
	}
}
