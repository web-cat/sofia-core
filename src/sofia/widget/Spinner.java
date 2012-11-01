package sofia.widget;

import java.util.List;

import sofia.util.ObservableList;
import android.content.Context;
import android.util.AttributeSet;

//-------------------------------------------------------------------------
/**
 * A subclass of {@link android.widget.Spinner} that is easier to use. It
 * provides methods like those in the {@link List} interface ({@code add},
 * {@code remove}, {@code get}, and {@code set}, among others) to manipulate
 * the contents of the list, as well as an accessor method {@link #getList()}
 * that returns a {@link List} that automatically refreshes the spinner when
 * its structure is changed.
 * 
 * @param <T> the type of elements stored in the {@code Spinner}
 *
 * @author  Tony Allevato
 * @version 2012.09.25
 */
public class Spinner<T> extends android.widget.Spinner
{
	//~ Fields ................................................................
	
	private ObservableList<T> list;
	private DecoratingAdapter<T> adapter;


	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	/**
	 * Creates a new {@code Spinner}.
	 * 
	 * @param context the context
	 */
	public Spinner(Context context)
	{
		super(context);
		init();
	}

	
	// ----------------------------------------------------------
	/**
	 * Creates a new {@code Spinner}.
	 * 
	 * @param context the context
	 * @param attrs the attribute set from the layout XML file
	 */
	public Spinner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	
	// ----------------------------------------------------------
	/**
	 * Creates a new {@code Spinner}.
	 * 
	 * @param context the context
	 * @param attrs the attribute set from the layout XML file
	 * @param defStyle the default style ID
	 */
	public Spinner(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}
	

	//~ Public methods ........................................................

	// ----------------------------------------------------------
	/**
	 * Gets the list of items that is managed by this spinner. Changes made
	 * to the structure of the list returned by this method (that is, adding,
	 * removing, or replacing items) will be immediately reflected in the
	 * spinner. You only need to explicitly {@link #refresh()} the list if you
	 * make a change to an element inside the list without directly modifying
	 * the list itself.
	 * 
	 * @return the {@link List} of items managed by this spinner
	 */
	public List<T> getList()
	{
		return list;
	}


	// ----------------------------------------------------------
	/**
	 * Adds an item to the spinner.
	 * 
	 * @param item the item to add to the spinner
	 * @return true if the item could be added, or false if it could not
	 */
	public boolean add(T item)
	{
		return list.add(item);
	}


	// ----------------------------------------------------------
	/**
	 * Inserts an item into the spinner at the specified index.
	 * 
	 * @param index the index where the new item should be inserted
	 * @param item the item to add to the spinner
	 * @return true if the item could be added, or false if it could not
	 */
	public void add(int index, T item)
	{
		list.add(index, item);
	}


	// ----------------------------------------------------------
	/**
	 * Gets the element at the specified index from the spinner.
	 * 
	 * @param index the index of the item to retrieve
	 * @return the item at the specified index
	 */
	public T get(int index)
	{
		return list.get(index);
	}


	// ----------------------------------------------------------
	/**
	 * Removes the item at the specified index from the spinner.
	 * 
	 * @param index the index of the item to be removed
	 * @return the item that was removed
	 */
	public T remove(int index)
	{
		return list.remove(index);
	}


	// ----------------------------------------------------------
	/**
	 * Removes the specified item from the spinner.
	 * 
	 * @param item the item to remove from the spinner
	 * @return true if the item was found and removed, or false if it was not
	 */
	public boolean remove(T item)
	{
		return list.remove(item);
	}


	// ----------------------------------------------------------
	/**
	 * Replaces the element at the specified index from the spinner with
	 * another item.
	 * 
	 * @param index the index of the item to retrieve
	 * @param item the item to put into the spinner
	 * @return the item previously at the specified index
	 */
	public T set(int index, T item)
	{
		return list.set(index, item);
	}


	// ----------------------------------------------------------
	/**
	 * Refreshes the spinner to update its contents from the list it manages.
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
	private void init()
	{
		list = new ObservableList<T>();
		list.addObserver(observer);
		
		adapter = new DecoratingAdapter<T>(getContext(),
				android.R.layout.simple_spinner_item, list);
		setAdapter(adapter);
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
		 * Handles notifications about a change to the list inside this
		 * spinner.
		 * 
		 * @param theList the list that was changed
		 */
		@SuppressWarnings("unused")
		public void changeWasObserved(ObservableList<T> theList)
		{
			refresh();
		}
	};
}
