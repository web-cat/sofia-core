package sofia.widget;

import java.util.Collection;
import java.util.List;

import sofia.util.ObservableList;
import android.content.Context;
import android.util.AttributeSet;

//-------------------------------------------------------------------------
/**
 * A subclass of {@link android.widget.ListView} that is easier to use. It
 * provides methods like those in the {@link List} interface ({@code add},
 * {@code remove}, {@code get}, and {@code set}, among others) to manipulate
 * the contents of the list, as well as an accessor method {@link #getList()}
 * that returns a {@link List} that automatically refreshes the list view when
 * its structure is changed.
 *
 * @param <E> the type of elements stored in the {@code ListView}
 *
 * @author  Tony Allevato
 * @version 2012.09.25
 */
public class ListView<E> extends android.widget.ListView
{
    //~ Fields ................................................................

    private ObservableList<E> list;
    private DecoratingAdapter<E> adapter;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code ListView}.
     *
     * @param context the context
     */
    public ListView(Context context)
    {
        super(context);
        init(null);
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code ListView}.
     *
     * @param context the context
     * @param attrs the attribute set from the layout XML file
     */
    public ListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(null);
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code ListView}.
     *
     * @param context the context
     * @param attrs the attribute set from the layout XML file
     * @param defStyle the default style ID
     */
    public ListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(null);
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the list of items that is managed by this list view. Changes made
     * to the structure of the list returned by this method (that is, adding,
     * removing, or replacing items) will be immediately reflected in the list
     * view. You only need to explicitly {@link #refresh()} the list if you
     * make a change to an element inside the list without directly modifying
     * the list itself.
     *
     * @return the {@link List} of items managed by this list view
     */
    public List<E> getList()
    {
        return list;
    }


    // ----------------------------------------------------------
    /**
     * Adds an item to the list view.
     *
     * @param item the item to add to the list view
     * @return true if the item could be added, or false if it could not
     */
    public boolean add(E item)
    {
        return list.add(item);
    }


    // ----------------------------------------------------------
    /**
     * Inserts an item into the list view at the specified index.
     *
     * @param index the index where the new item should be inserted
     * @param item the item to add to the list view
     */
    public void add(int index, E item)
    {
        list.add(index, item);
    }


    // ----------------------------------------------------------
    /**
     * Adds the items in the specified collection to the list view.
     *
     * @param collection the items to add to the list view
     * @return  rue if the items could be added, or false if they could not
     */
    public boolean addAll(Collection<? extends E> collection)
    {
        return list.addAll(collection);
    }


    // ----------------------------------------------------------
    /**
     * Inserts the items in the specified collection into the list view at the
     * specified index.
     *
     * @param index the index where the new items should be inserted
     * @param collection the items to add to the list view
     * @return true if the items could be added, or false if they could not
     */
    public boolean addAll(int index, Collection<? extends E> collection)
    {
        return list.addAll(index, collection);
    }


    // ----------------------------------------------------------
    /**
     * Removes all items from the list view.
     */
    public void clear()
    {
        list.clear();
    }


    // ----------------------------------------------------------
    /**
     * Gets the element at the specified index from the list view.
     *
     * @param index the index of the item to retrieve
     * @return the item at the specified index
     */
    public E get(int index)
    {
        return list.get(index);
    }


    // ----------------------------------------------------------
    /**
     * Removes the item at the specified index from the list view.
     *
     * @param index the index of the item to be removed
     * @return the item that was removed
     */
    public E remove(int index)
    {
        return list.remove(index);
    }


    // ----------------------------------------------------------
    /**
     * Removes the specified item from the list view.
     *
     * @param item the item to remove from the list view
     * @return true if the item was found and removed, or false if it was not
     */
    public boolean remove(E item)
    {
        return list.remove(item);
    }


    // ----------------------------------------------------------
    /**
     * Replaces the element at the specified index from the list view with
     * another item.
     *
     * @param index the index of the item to retrieve
     * @param item the item to put into the list
     * @return the item previously at the specified index
     */
    public E set(int index, E item)
    {
        return list.set(index, item);
    }


    // ----------------------------------------------------------
    /**
     * Refreshes the list view to update its contents from the list it manages.
     * This method does not need to be called after methods like {@code add}
     * or {@code remove} -- it only needs to be called if you change a property
     * of one of the elements in the list (for example, by calling a setter)
     * without modifying the structure of the list itself.
     */
    public void refresh()
    {
        adapter.notifyDataSetChanged();
    }


    //~ Private methods .......................................................

    // ----------------------------------------------------------
    /**
     * Common initialization for new list views.
     */
    @SuppressWarnings("unchecked")
    private void init(AttributeSet attrs)
    {
        list = new ObservableList<E>();

        if (attrs != null)
        {
            // If the ListView has an android:entries attribute, populate the
            // list with those entries automatically. (The generic type E of
            // the ListView better be String if this is going to work.)

            int entriesId = attrs.getAttributeResourceValue(
                    "http://schemas.android.com/apk/res/android",
                    "entries", 0);

            if (entriesId != 0)
            {
                CharSequence[] entries =
                        getContext().getResources().getTextArray(entriesId);
                for (CharSequence entry : entries)
                {
                    list.add((E) entry.toString());
                }
            }
        }

        adapter = new DecoratingAdapter<E>(getContext(),
                android.R.layout.simple_list_item_1, list);
        setAdapter(adapter);

        list.addObserver(observer);
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    /**
     * The observer is pulled into a separate object so that we do not expose
     * the changeWasObserved method in the class's public interface.
     */
    private final Object observer = new Object()
    {
        // ----------------------------------------------------------
        /**
         * Handles notifications about a change to the list inside this list
         * view.
         *
         * @param theList the list that was changed
         */
        @SuppressWarnings("unused")
        public void changeWasObserved(ObservableList<E> theList)
        {
            refresh();
        }
    };
}
