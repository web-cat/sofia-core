package sofia.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//-------------------------------------------------------------------------
/**
 * An adapter that uses <em>decoration through annotations</em> to display
 * elements in a list view or spinner. This class is used internally by widgets
 * like {@link ListView} and {@link Spinner}, so most users won't need to use
 * this class directly unless they want to add Sofia-like decoration features
 * to other widgets that aren't yet supported.
 *
 * @param <E> the type of items managed by the adapter
 *
 * @author  Tony Allevato
 * @version 2012.09.25
 */
public class DecoratingAdapter<E>
    extends BaseAdapter
    implements Filterable
{
    //~ Fields ................................................................

    private List<E> list;
    private LayoutInflater inflater;
    private int defaultViewResId;

    private DecoratingFilter filter;
    private Object lock = new Object();
    private ArrayList<E> originalList;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public DecoratingAdapter(Context context, int defaultViewResId,
            List<E> list)
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
    public E getItem(int position)
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

        E item = getItem(position);

        String title = decorate(item, ProvidesTitle.class, String.class);
        if (title == null)
        {
            title = item.toString();
        }

        String subtitle = decorate(item, ProvidesSubtitle.class, String.class);

        int resource = defaultViewResId;

        if (resource == 0)
        {
            resource = (subtitle != null) ?
                    android.R.layout.simple_list_item_2 :
                    android.R.layout.simple_list_item_1;
        }

        if (convertView == null)
        {
            view = inflater.inflate(resource, parent, false);
        }
        else
        {
            view = convertView;
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(title);

        if (subtitle != null)
        {
            textView = (TextView) view.findViewById(android.R.id.text2);

            if (textView != null)
            {
                textView.setText(subtitle);
            }
        }

        return view;
    }


    // ----------------------------------------------------------
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent)
    {
        // TODO Support other means of rendering the list contents.
        View view;

        E item = getItem(position);
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


    // ----------------------------------------------------------
    @Override
    public Filter getFilter()
    {
        if (filter == null)
        {
            filter = new DecoratingFilter();
        }

        return filter;
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

                if (r == null || resultType.isAssignableFrom(r.getClass()))
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


    // ----------------------------------------------------------
    // FIXME This should be made general somehow, so that users can plug in
    // different filters. Maybe a context callback?
    private class DecoratingFilter extends Filter
    {
        // ----------------------------------------------------------
        @Override
        protected FilterResults performFiltering(CharSequence prefix)
        {
            FilterResults results = new FilterResults();

            if (originalList == null)
            {
                synchronized (lock)
                {
                    originalList = new ArrayList<E>(list);
                }
            }

            if (prefix == null || prefix.length() == 0)
            {
                ArrayList<E> list;
                synchronized (lock)
                {
                    list = new ArrayList<E>(originalList);
                }

                results.values = list;
                results.count = list.size();
            }
            else
            {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<E> values;

                synchronized (lock)
                {
                    values = new ArrayList<E>(originalList);
                }

                int count = values.size();
                ArrayList<E> newValues = new ArrayList<E>();

                for (int i = 0; i < count; i++)
                {
                    E value = values.get(i);
                    String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString))
                    {
                        newValues.add(value);
                    }
                    else
                    {
                        String[] words = valueText.split(" ");
                        int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++)
                        {
                            if (words[k].startsWith(prefixString))
                            {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }


        // ------------------------------------------------------
        @Override @SuppressWarnings("unchecked")
        protected void publishResults(
                CharSequence constraint, FilterResults results)
        {
            list = (List<E>) results.values;

            if (results.count > 0)
            {
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated();
            }
        }
    }
}
