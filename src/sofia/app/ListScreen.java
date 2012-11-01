package sofia.app;

import java.util.Collection;
import java.util.List;

import sofia.app.internal.SofiaLayoutInflater;
import sofia.widget.ListView;

// -------------------------------------------------------------------------
/**
 * <p>
 * {@code ListScreen} is a subclass of screen that provides a built-in
 * {@link ListView} and convenience methods for manipulating the list directly
 * in the screen class instead of having to call {@link #getListView()} for
 * every operation.
 * </p><p>
 * The {@code ListScreen} class is generic, so users who extend it should
 * include the type of the items that will be stored in the list so that
 * methods like {@code add} and {@code remove} will have the correct types. For
 * example,
 * </p>
 * <pre>
 * public class MyListScreen extends ListScreen&lt;MyObject&gt; { ...</pre>
 * <p>
 * Please see the documentation for {@link ListView} for more information on
 * how arbitrary objects are displayed in the list.
 * </p><p>
 * When you subclass {@code ListScreen}, by default it will create a new
 * {@code ListView} that occupies the entire width and height of the screen.
 * If this is not what you want (for example, if you want to have a
 * {@code ListView} alongside other widgets but still retain the convenience
 * of methods like {@link #add(E)} directly on the screen), then place an
 * instance of {@code ListView} in your layout file with the ID
 * {@code listView}. Then the {@code ListScreen} will use that view for all
 * of its other methods instead of creating its own.
 * </p><p>
 * Note to developers coming from the traditional Android API: though similar,
 * {@code ListScreen} is not a subclass of {@code ListActivity}, and it does
 * not have exactly the same functionality or interface.
 * </p>
 *
 * @param <E> the type of items that will be stored in the list
 * 
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Date$
 */
public abstract class ListScreen<E> extends Screen
{
    //~ Fields ................................................................

    private ListView<E> listView;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    protected void afterLayoutInflated(boolean inflated)
    {
    	boolean hasListView = false;
    	
    	if (inflated)
    	{
        	int listViewId = getResources().getIdentifier(
        			"listView", "id", getPackageName());

        	if (listViewId != 0)
        	{
        		listView = (ListView<E>) findViewById(listViewId);
        		hasListView = true;
        	}
    	}

    	if (!hasListView)
    	{
    		listView = createListView(this);
    		SofiaLayoutInflater.autoBindEvents(listView, null);
    		setContentView(listView);
        
    		listView.requestFocus();
    	}
    }


    // ----------------------------------------------------------
    /**
     * This factory method is used to create the {@link ListView}
     * that will be contained by this screen.  It is provided for
     * subclass extensibility, in case a subclass of {@code ListScreen} wants
     * to use a more specialized {@code ListView} instance.
     * 
     * @param parent The screen that will contain the view (e.g., "this")
     * @return A new {@code ListView} object to use for this screen.
     */
    protected ListView<E> createListView(ListScreen<E> parent)
    {
   		return new ListView<E>(parent);
    }


    // ----------------------------------------------------------
    /**
     * Gets the {@link ListView} that holds all of the items on this screen.
     *
     * @return The {@link ListView} that holds all of the items on this
     *         screen.
     */
    public final ListView<E> getListView()
    {
        return listView;
    }


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
		return listView.getList();
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
		return listView.add(item);
	}


	// ----------------------------------------------------------
	/**
	 * Inserts an item into the list view at the specified index.
	 * 
	 * @param index the index where the new item should be inserted
	 * @param item the item to add to the list view
	 * @return true if the item could be added, or false if it could not
	 */
	public void add(int index, E item)
	{
		listView.add(index, item);
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
		return listView.addAll(collection);
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
		return listView.addAll(index, collection);
	}


	// ----------------------------------------------------------
	/**
	 * Removes all items from the list view.
	 */
	public void clear()
	{
		listView.clear();
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
		return listView.get(index);
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
		return listView.remove(index);
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
		return listView.remove(item);
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
		return listView.set(index, item);
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
		listView.refresh();
	}
}
