package sofia.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

//-------------------------------------------------------------------------
/**
 * An adapter that uses <em>decoration through annotations</em> to display
 * elements in a list view or spinner. This class is used internally by widgets
 * like {@link ListView} and {@link Spinner}, so most users won't need to use
 * this class directly unless they want to add Sofia-like decoration features
 * to other widgets that aren't yet supported.
 * 
 * @author  Tony Allevato
 * @version 2012.09.25
 */
public class DecoratingAdapter<T> extends BaseAdapter
{
	//~ Fields ................................................................

    private List<T> list;
    private LayoutInflater inflater;
    private int defaultViewResId;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public DecoratingAdapter(Context context, int defaultViewResId,
    		List<T> list)
    {
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);
        this.defaultViewResId = defaultViewResId;
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    public int getCount()
    {
        return list.size();
    }


    // ----------------------------------------------------------
    public T getItem(int position)
    {
        return list.get(position);
    }


    // ----------------------------------------------------------
    public long getItemId(int position)
    {
        return position;
    }


    // ----------------------------------------------------------
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	// TODO Support other means of rendering the list contents.
        View view;

        T item = getItem(position);
        int resource = defaultViewResId;

        if (convertView == null)
        {
            view = inflater.inflate(resource, parent, false);
        }
        else
        {
            view = convertView;
        }

        String title = decorate(item, ProvidesTitle.class, String.class);
        if (title == null)
        {
        	title = item.toString();
        }
        
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(title);

        return view;
    }


    // ----------------------------------------------------------
    @Override
    public View getDropDownView(int position, View convertView,
    		ViewGroup parent)
    {
    	// TODO Support other means of rendering the list contents.
        View view;

        T item = getItem(position);
        int resource = android.R.layout.simple_spinner_dropdown_item;

        if (convertView == null)
        {
            view = inflater.inflate(resource, parent, false);
        }
        else
        {
            view = convertView;
        }

        String title = decorate(item, ProvidesTitle.class, String.class);
        if (title == null)
        {
        	title = item.toString();
        }
        
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(title);

        return view;
    }
    
    
    //~ Private methods .......................................................

	// ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    private static <ResultType> ResultType decorate(
    		Object object, Class<? extends Annotation> annotation,
    		Class<? extends ResultType> resultType)
    {
    	ResultType result = null;

    	Method method = getAnnotatedMethod(object.getClass(), annotation);
    	
    	if (method != null)
    	{
    		try
    		{
				Object r = method.invoke(object);
				
				if (resultType.isAssignableFrom(r.getClass()))
				{
					result = (ResultType) r;
				}
			}
    		catch (InvocationTargetException e)
    		{
				throw new RuntimeException(e.getCause());
			}
    		catch (Exception e)
    		{
				throw new RuntimeException(e);
			}
    	}
    	
    	return result;
    }


    // ----------------------------------------------------------
	/**
	 * TODO Replace with reflection API.
	 */
	private static Method getAnnotatedMethod(
			Class<?> itemClass, Class<? extends Annotation> annotation)
	{
		Method method = null;

		for (Method currentMethod : itemClass.getMethods())
		{
			if (currentMethod.getAnnotation(annotation) != null)
			{
				method = currentMethod;
				break;
			}
		}

		return method;
	}
    
}
