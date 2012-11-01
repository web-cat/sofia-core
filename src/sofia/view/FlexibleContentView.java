package sofia.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//-------------------------------------------------------------------------
/**
 * <p>
 * A view that takes an arbitrary {@code Object} and renders it the best way it
 * knows how. The following types are currently supported:
 * </p>
 * 
 * <dl>
 * <dt>{@link String}</dt>
 * <dd>Renders the string using a {@link TextView}.</dd>
 * 
 * <dt>{@link Bitmap}, {@link Drawable}</dt>
 * <dd>Renders the object using an {@link ImageView}.</dd>
 * </dl>
 * 
 * @author  Tony Allevato
 * @version 2012.07.19
 */
public class FlexibleContentView extends LinearLayout
{
	//~ Fields ................................................................

	private View contentView;
	private int maxWidth;
	private int maxHeight;
	private int textColor;
	private float textSize;


	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	public FlexibleContentView(Context context)
	{
		super(context);		
		setOrientation(VERTICAL);
	}

	
	// ----------------------------------------------------------
	public FlexibleContentView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setOrientation(VERTICAL);
	}

	
	//~ Methods ...............................................................

	// ----------------------------------------------------------
	/**
	 * Sets the content that is displayed by this view, changing the type of
	 * the internal view to render it correctly.
	 * 
	 * @param object the content displayed in the view
	 */
	public void setContent(Object object)
	{
		if (contentView != null)
		{
			removeView(contentView);
			contentView = null;
		}

		if (object instanceof Bitmap)
		{
			contentView = setBitmap((Bitmap) object);
		}
		else if (object instanceof Drawable)
		{
			contentView = setDrawable((Drawable) object);
		}
		else if (object instanceof String)
		{
			contentView = setString((String) object);
		}
		
		if (contentView != null)
		{
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER_HORIZONTAL;
			addView(contentView, lp);
		}
	}
	
	
	// ----------------------------------------------------------
	private ImageView createImageView()
	{
		ImageView view = new ImageView(getContext());
		view.setScaleType(ImageView.ScaleType.FIT_CENTER);
		view.setAdjustViewBounds(true);
		return view;
	}


	// ----------------------------------------------------------
	private View setBitmap(Bitmap bitmap)
	{
		ImageView view = createImageView();
		view.setImageBitmap(bitmap);
		return view;
	}
	
	
	// ----------------------------------------------------------
	private View setDrawable(Drawable drawable)
	{
		ImageView view = createImageView();
		view.setImageDrawable(drawable);
		return view;
	}
	
	
	// ----------------------------------------------------------
	private View setString(String string)
	{
		TextView view = new TextView(getContext());
		view.setText(string);
		
		if (textColor != 0)
		{
			view.setTextColor(textColor);
		}
		
		if (textSize != 0)
		{
			view.setTextSize(textSize);
		}

		return view;
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Gets the maximum width of the view.
	 * 
	 * @return the maximum width of the view
	 */
	public int getMaxWidth()
	{
		return maxWidth;
	}


	// ----------------------------------------------------------
	/**
	 * Sets the maximum width of the view.
	 * 
	 * @param maxWidth the new maximum width of the view
	 */
	public void setMaxWidth(int maxWidth)
	{
		this.maxWidth = maxWidth;
		requestLayout();
	}


	// ----------------------------------------------------------
	/**
	 * Gets the maximum height of the view.
	 * 
	 * @return the maximum height of the view
	 */
	public int getMaxHeight()
	{
		return maxHeight;
	}


	// ----------------------------------------------------------
	/**
	 * Sets the maximum height of the view.
	 * 
	 * @param maxWidth the new maximum height of the view
	 */
	public void setMaxHeight(int maxHeight)
	{
		this.maxHeight = maxHeight;
		requestLayout();
	}


	// ----------------------------------------------------------
	/**
	 * Gets the color used to render the view's content when it is text.
	 * 
	 * @return the color used to render the view's content when it is text
	 */
	public int getTextColor()
	{
		return textColor;
	}


	// ----------------------------------------------------------
	/**
	 * Sets the color used to render the view's content when it is text.
	 * 
	 * @param color the color used to render the view's content when it is text
	 */
	public void setTextColor(int color)
	{
		this.textColor = color;

		if (contentView instanceof TextView)
		{
			TextView textView = (TextView) contentView;
			textView.setTextColor(textColor);
		}
	}


	// ----------------------------------------------------------
	/**
	 * Gets the text size used to render the view's content when it is text.
	 * 
	 * @return the text size used to render the view's content when it is text
	 */
	public float getTextSize()
	{
		return textSize;
	}


	// ----------------------------------------------------------
	/**
	 * Sets the text size used to render the view's content when it is text.
	 * 
	 * @param size the text size used to render the view's content when it is
	 *     text
	 */	
	public void setTextSize(float size)
	{
		this.textSize = size;

		if (contentView instanceof TextView)
		{
			TextView textView = (TextView) contentView;
			textView.setTextSize(textSize);
		}
	}


	// ----------------------------------------------------------
	/**
	 * Sets the text size used to render the view's content when it is text.
	 * 
	 * @param unit the desired dimensional unit
	 * @param size the text size used to render the view's content when it is
	 *     text
	 */	
	public void setTextSize(int unit, float size)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		Activity activity = (Activity) getContext();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		setTextSize(TypedValue.applyDimension(unit, size, metrics));
	}


	// ----------------------------------------------------------
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        
        if (maxWidth > 0 && maxWidth < measuredWidth)
        {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(
            		maxWidth, measureMode);
        }

        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        
        if (maxHeight > 0 && maxHeight < measuredHeight)
        {
            int measureMode = MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
            		maxHeight, measureMode);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
