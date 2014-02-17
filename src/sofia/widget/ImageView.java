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

package sofia.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//-------------------------------------------------------------------------
/**
 * A subclass of {@link android.widget.ImageView} that can load images from
 * HTTP/HTTPS URIs as well as resource and content resolver URIs.
 *
 * @author Tony Allevato
 */
public class ImageView extends android.widget.ImageView
{
    //~ Fields ................................................................

    private Uri imageURI;
    private boolean loaded;

    private static final Logger log = LoggerFactory.getLogger(ImageView.class);


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public ImageView(Context context)
    {
        super(context);
    }


    // ----------------------------------------------------------
    public ImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    // ----------------------------------------------------------
    public ImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the image URI that was last set to this view. This will return null
     * if the image was set through a method other than
     * {@link #setImageURI(Uri)}.
     *
     * @return the image URI
     */
    public Uri getImageURI()
    {
        return imageURI;
    }


    // ----------------------------------------------------------
    /**
     * Sets the content of this ImageView to the specified Uri.
     *
     * @param uri the Uri of an image
     */
    public void setImageURI(Uri uri)
    {
        // FIXME Possible race conditions!
        this.imageURI = uri;
        loaded = false;

        if (uri != null)
        {
            String scheme = uri.getScheme();
            if ("http".equalsIgnoreCase(scheme)
                    || "https".equalsIgnoreCase(scheme))
            {
                new AsyncImageLoader().execute();
                return;
            }
            else
            {
                loaded = true;
                super.setImageURI(uri);
            }
        }
        else
        {
            loaded = true;
            setImageBitmap(null);
        }
    }


    // ----------------------------------------------------------
    @Override
    public void setImageBitmap(Bitmap bm)
    {
        imageURI = null;
        super.setImageBitmap(bm);
    }


    // ----------------------------------------------------------
    @Override
    public void setImageDrawable(Drawable drawable)
    {
        imageURI = null;
        super.setImageDrawable(drawable);
    }


    // ----------------------------------------------------------
    @Override
    public void setImageResource(int resId)
    {
        imageURI = null;
        super.setImageResource(resId);
    }


    // ----------------------------------------------------------
    public boolean isLoaded()
    {
        // FIXME Possible race conditions!
        return loaded;
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    /**
     * FIXME Rapid fire calls to setImageURI might cause this to behave oddly;
     * it should probably cancel any pending requests and only use the last
     * one.
     */
    private class AsyncImageLoader extends AsyncTask<Void, Void, Void>
    {
        //~ Fields ............................................................

        private Handler handler;


        //~ Constructors ......................................................

        // ----------------------------------------------------------
        public AsyncImageLoader()
        {
            handler = new Handler(Looper.getMainLooper());
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        @Override
        protected Void doInBackground(Void... arg0)
        {
            final Uri uri = getImageURI();
            final Bitmap bitmap = bitmapFromURI(uri);

            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    // FIXME Possible race conditions!
                    loaded = true;
                    setImageBitmap(bitmap);
                    imageURI = uri;
                }
            });

            return null;
        }


        // ----------------------------------------------------------
        private Bitmap bitmapFromURI(Uri uri)
        {
            Bitmap bm = null;
            InputStream is = null;
            BufferedInputStream bis = null;

            try
            {
                URLConnection conn = new URL(uri.toString()).openConnection();
                conn.connect();
                is = conn.getInputStream();
                bis = new BufferedInputStream(is, 8192);
                bm = BitmapFactory.decodeStream(bis);
            }
            catch (Exception e)
            {
                log.error("Error loading bitmap from URI", e);
            }
            finally
            {
                if (bis != null)
                {
                    try
                    {
                        bis.close();
                    }
                    catch (IOException e)
                    {
                        // Do nothing.
                    }
                }

                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            return bm;
        }
    }
}
