package sofia.content;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 * Utility methods to make working with the built-in Android media gallery
 * easier for students.
 * 
 * TODO These might benefit from some refactoring now.
 *
 * @author  Tony Allevato
 * @version 2011.09.15
 */
public class MediaUtils
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Prevent instantiation.
     */
    private MediaUtils()
    {
        // Do nothing.
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * <p>
     * Gets a list of all external media (stored on the device's SD card) that
     * has the specified type. The type can be a complete MIME type (such as
     * {@code "image/jpeg"}) or one with a wildcard (such as {@code "image/*"}
     * to select all images).
     * </p><p>
     * The list returned by this method contains {@code Uri} objects; in order
     * to get the path that corresponds to that {@code Uri} so that you can
     * open the file, pass it to the
     * {@link #pathForMediaUri(ContentResolver, Uri)} method.
     * </p>
     *
     * @param resolver the content resolver used to find the content; this can
     *     be obtained by calling {@code getContentResolver()} from within the
     *     activity
     * @param type the MIME type of the media to list
     *
     * @return a {@code List} containing the {@code Uri}s of all the matching
     *     media on the device's external storage
     */
    public static List<Uri> getExternalMediaUris(
        ContentResolver resolver, String type)
    {
        final String[] columns = { BaseColumns._ID };
        final String selection = MediaColumns.MIME_TYPE + " LIKE ?";
        final String[] selectionArgs = { type.replace('*', '%') };

        Cursor cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns, selection, selectionArgs, null);

        cursor.moveToFirst();

        ArrayList<Uri> uris = new ArrayList<Uri>();

        for (int i = 0; i < cursor.getCount(); i++)
        {
            String id = cursor.getString(0);
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                .buildUpon().appendPath(id).build();
            uris.add(uri);

            cursor.moveToNext();
        }

        cursor.close();

        return uris;
    }


    // ----------------------------------------------------------
    /**
     * <p><strong>
     * Most students will never need to call this method -- it is intended for
     * internal and advanced usage.
     * </strong></p><p>
     * Gets a {@code Uri} that represents a media item with the specified
     * filename.
     * </p><p>
     * No MIME type is required here because the filename of the media (for
     * example, "foo.jpg" or "bar.png") would typically also distinguish the
     * type. However, if multiple media have the same filename, only the first
     * one that is found will be returned, and the ordering of what is "first"
     * is entirely dependent on the internal database ordering, and therefore
     * should not be relied upon. If this is a problem, use a longer path
     * segment than just the filename.
     * </p>
     *
     * @param resolver the content resolver used to find the content; this can
     *     be obtained by calling {@code getContentResolver()} from within the
     *     activity
     * @param filename the filename of the media to find
     *
     * @return a {@code Uri} that represents the media being requested, or null
     *     if no media with that filename could be found
     */
    public static Uri uriForMediaWithFilename(
        ContentResolver resolver, String filename)
    {
        final String[] columns = { BaseColumns._ID, MediaColumns.DATA };
        final String selection = MediaColumns.DATA + " LIKE ?";
        final String[] selectionArgs = { "%" + filename };

        Cursor cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns, selection, selectionArgs, null);

        Uri uri = null;

        if (cursor.moveToFirst())
        {
            String id = cursor.getString(0);

            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                .buildUpon().appendPath(id).build();
        }

        cursor.close();

        return uri;
    }


    // ----------------------------------------------------------
    /**
     * Converts a media {@code Uri}, such as one received by an activity's
     * {@code onActivityResult} method after popping up an image chooser, to
     * a path on the device's file system that can be used to open or
     * manipulate the media file.
     *
     * @param resolver the content resolver used to find the content; this can
     *     be obtained by calling {@code getContentResolver()} from within the
     *     activity
     * @param uri the {@code Uri} that represents the location of the media to
     *     get the path for
     *
     * @return the path to the file represented by the media {@code Uri}, or
     *     null if the {@code Uri} was invalid
     */
    public static String pathForMediaUri(ContentResolver resolver, Uri uri)
    {
        final String[] columns = {
            android.provider.MediaStore.MediaColumns.DATA
        };

        Cursor cursor = resolver.query(uri, columns, null, null, null);

        try
        {
            if (cursor.moveToFirst())
            {
                return cursor.getString(0);
            }
            else
            {
                return null;
            }
        }
        finally
        {
            cursor.close();
        }
    }
}
