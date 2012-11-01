package sofia.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

// -------------------------------------------------------------------------
/**
 * <p>
 * An implementation of the {@link List} interface that is {@link Observable}
 * and notifies its observers whenever the collection is changed.
 * </p><p>
 * Users of this class can add themselves as observers to an instance of this
 * class and they will be notified when the structure of the list changes; that
 * is, when items are added, removed, or replaced.
 * </p>
 *
 * @param <E> the type of element stored in the list
 *
 * @author  Tony Allevato
 * @version 2012.09.25
 */
public class ObservableList<E>
	extends Observable
    implements List<E>
{
    //~ Fields ................................................................

    private List<E> contents;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code ObservableList} that is backed by an
     * {@link ArrayList} with default capacity.
     */
    public ObservableList()
    {
        contents = new ArrayList<E>();
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code ObservableList} that is backed by an
     * {@link ArrayList} with the specified capacity.
     * 
     * @param capacity the capacity of the array list
     */
    public ObservableList(int capacity)
    {
        contents = new ArrayList<E>(capacity);
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code ObservableList} that provides an observable view of
     * an existing list. The list passed to this constructor is <em>not</em>
     * copied; changes made to the observable list will also be reflected in
     * the list being wrapped, and vice versa.
     * 
     * @param listToWrap the list to wrap with an observable front-end
     */
    public ObservableList(List<E> listToWrap)
    {
    	contents = listToWrap;
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code ObservableList} that is initialized with a copy of
     * the data in the specified collection. Since this constructor creates a
     * copy, changes to the observable list will <em>not</em> be reflected in
     * the source collection, and vice versa.
     * 
     * @param collection the collection to be copied into the new list
     */
    public ObservableList(Collection<? extends E> collection)
    {
        contents = new ArrayList<E>(collection);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void add(int index, E item)
    {
        contents.add(index, item);

        notifyObservers();
    }


    // ----------------------------------------------------------
    public boolean add(E item)
    {
        boolean result = contents.add(item);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean addAll(int index, Collection<? extends E> collection)
    {
        boolean result = contents.addAll(index, collection);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean addAll(Collection<? extends E> collection)
    {
        boolean result = contents.addAll(collection);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public void clear()
    {
        contents.clear();

        notifyObservers();
    }


    // ----------------------------------------------------------
    public boolean contains(Object object)
    {
        return contents.contains(object);
    }


    // ----------------------------------------------------------
    public boolean containsAll(Collection<?> collection)
    {
        return contents.containsAll(collection);
    }


    // ----------------------------------------------------------
    public E get(int index)
    {
        return contents.get(index);
    }


    // ----------------------------------------------------------
    public int indexOf(Object object)
    {
        return contents.indexOf(object);
    }


    // ----------------------------------------------------------
    public boolean isEmpty()
    {
        return contents.isEmpty();
    }


    // ----------------------------------------------------------
    public Iterator<E> iterator()
    {
        return new NotifyingIterator(contents.iterator());
    }


    // ----------------------------------------------------------
    public int lastIndexOf(Object object)
    {
        return contents.lastIndexOf(object);
    }


    // ----------------------------------------------------------
    public ListIterator<E> listIterator()
    {
        return new NotifyingListIterator(contents.listIterator());
    }


    // ----------------------------------------------------------
    public ListIterator<E> listIterator(int index)
    {
        return new NotifyingListIterator(contents.listIterator(index));
    }


    // ----------------------------------------------------------
    public E remove(int index)
    {
        E result = contents.remove(index);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean remove(Object object)
    {
        boolean result = contents.remove(object);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean removeAll(Collection<?> collection)
    {
        boolean result = contents.removeAll(collection);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean retainAll(Collection<?> collection)
    {
        boolean result = contents.retainAll(collection);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public E set(int index, E item)
    {
        E result = contents.set(index, item);

        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public int size()
    {
        return contents.size();
    }


    // ----------------------------------------------------------
    public List<E> subList(int start, int end)
    {
        return contents.subList(start, end);
    }


    // ----------------------------------------------------------
    public Object[] toArray()
    {
        return contents.toArray();
    }


    // ----------------------------------------------------------
    public <T> T[] toArray(T[] array)
    {
        return contents.toArray(array);
    }


    //~ Nested classes ........................................................

    // ----------------------------------------------------------
    private class NotifyingIterator implements Iterator<E>
    {
        private Iterator<E> iterator;


        // ----------------------------------------------------------
        public NotifyingIterator(Iterator<E> iterator)
        {
            this.iterator = iterator;
        }


        // ----------------------------------------------------------
        public boolean hasNext()
        {
            return iterator.hasNext();
        }


        // ----------------------------------------------------------
        public E next()
        {
            return iterator.next();
        }


        // ----------------------------------------------------------
        public void remove()
        {
            iterator.remove();

            notifyObservers();
        }
    }


    // ----------------------------------------------------------
    private class NotifyingListIterator implements ListIterator<E>
    {
        private ListIterator<E> iterator;


        // ----------------------------------------------------------
        public NotifyingListIterator(ListIterator<E> iterator)
        {
            this.iterator = iterator;
        }


        // ----------------------------------------------------------
        public void add(E item)
        {
            iterator.add(item);

            notifyObservers();
        }


        // ----------------------------------------------------------
        public boolean hasNext()
        {
            return iterator.hasNext();
        }


        // ----------------------------------------------------------
        public boolean hasPrevious()
        {
            return iterator.hasPrevious();
        }


        // ----------------------------------------------------------
        public E next()
        {
            return iterator.next();
        }


        // ----------------------------------------------------------
        public int nextIndex()
        {
            return iterator.nextIndex();
        }


        // ----------------------------------------------------------
        public E previous()
        {
            return iterator.previous();
        }


        // ----------------------------------------------------------
        public int previousIndex()
        {
            return iterator.previousIndex();
        }


        // ----------------------------------------------------------
        public void remove()
        {
            iterator.remove();

            notifyObservers();
        }


        // ----------------------------------------------------------
        public void set(E item)
        {
            iterator.set(item);

            notifyObservers();
        }
    }
}
