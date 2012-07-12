package sofia.app.internal;

import java.lang.reflect.Field;

import sofia.internal.MethodDispatcher;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

public class SofiaLayoutInflater extends LayoutInflater
{
	private static final String ANDROID_NS =
			"http://schemas.android.com/apk/res/android";

    private static final String[] classPrefixList = {
        "sofia.view.", "sofia.graphics.",
        "android.widget.", "android.webkit."
    };
    

    private IdTool idTool;


    // ----------------------------------------------------------
	public SofiaLayoutInflater(Context context)
	{
		super(context);
		
		idTool = new IdTool(context);
	}


    // ----------------------------------------------------------
	protected SofiaLayoutInflater(LayoutInflater original, Context newContext)
	{
		super(original, newContext);

		idTool = new IdTool(newContext);
	}

	
    // ----------------------------------------------------------
    @Override
    protected View onCreateView(String name, AttributeSet attrs)
    		throws ClassNotFoundException
    {
        for (String prefix : classPrefixList)
        {
            try
            {
                View view = createView(name, prefix, attrs);

                if (view != null)
                {
                	autoBindEvents(view, attrs);
                    return view;
                }
            }
            catch (ClassNotFoundException e)
            {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }


    // ----------------------------------------------------------
    public LayoutInflater cloneInContext(Context newContext)
    {
        return new SofiaLayoutInflater(this, newContext);
    }
    

    // ----------------------------------------------------------
    protected void autoBindEvents(View view, AttributeSet attrs)
    {
    	bindOnClick(view, attrs);
    	bindField(view, attrs);
    }
    

    // ----------------------------------------------------------
    private void bindField(final View view, AttributeSet attrs)
    {
    	Class<?> ctxClass = view.getContext().getClass();

		String id = idTool.getFieldNameForId(view.getId());
		
		try
		{
	    	Field field = ctxClass.getDeclaredField(id);
	
	    	if (field != null
	    			&& field.getType().isAssignableFrom(view.getClass()))
	        {
	            field.setAccessible(true);
                field.set(view.getContext(), view);
	        }
        }    	
        catch (Exception e)
        {
            // Do nothing.
        }
    }


    // ----------------------------------------------------------
    /**
     * This method implements the following binding rule: if a view is
     * clickable, and it does not already have a method bound to the onClick
     * attribute, then connect it to the context method "${id}Clicked", where
     * "${id}" is the string name of the view's identifier.
     * 
     * @param view the view
     * @param attrs the attribute set
     */
    private void bindOnClick(final View view, AttributeSet attrs)
    {
    	if (!AdapterView.class.isAssignableFrom(view.getClass())
    			&& view.isClickable()
    			&& attrs.getAttributeValue(ANDROID_NS, "onClick") == null)
    	{
    		final String id = idTool.getFieldNameForId(view.getId());

    		if (id != null)
    		{
    			// TODO Optimize
	    		final MethodDispatcher dispatcher1 =
	    				new MethodDispatcher(id + "Clicked", 1);
	    		final MethodDispatcher dispatcher0 =
	    				new MethodDispatcher(id + "Clicked", 0);

	    		try
	    		{
		    		view.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v)
						{
							if (dispatcher1.supportedBy(v.getContext(), v))
							{
								dispatcher1.callMethodOn(v.getContext(), v);
							}
							else
							{
								dispatcher0.callMethodOn(v.getContext());
							}
						}
					});
	    		}
	    		catch (Exception e)
	    		{
	    			// Do nothing. This try/catch was placed here because it
	    			// was discovered that setOnClickListener on an AdapterView
	    			// throws an exception with a message directing you to use
	    			// setOnItemClickListener instead. For future-proofing, we
	    			// want to catch any such possible exceptions and log them
	    			// so that we can place appropriate checks at the front of
	    			// this method.
	    			
	    			Log.d("SofiaLayoutInflater", e.getMessage());
	    		}
    		}
    	}
    }
}
