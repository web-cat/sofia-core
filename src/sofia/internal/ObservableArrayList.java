package sofia.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

// -------------------------------------------------------------------------
/**
 * An implementation of the {@link List} interface that is backed by an
 * {@link ArrayList}, but which also extends {@link Observable} and can notify
 * its observers whenever the collection is changed.
 *
 * @param <E> the type of element stored in the list
 *
 * @author  Tony Allevato
 * @version 2011.10.13
 */
public class ObservableArrayList<E>
    extends Observable
    implements List<E>
{
    //~ Instance/static fields ................................................

    private ArrayList<E> contents;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public ObservableArrayList()
    {
        contents = new ArrayList<E>();
    }


    // ----------------------------------------------------------
    public ObservableArrayList(int capacity)
    {
        contents = new ArrayList<E>(capacity);
    }


    // ----------------------------------------------------------
    public ObservableArrayList(Collection<? extends E> collection)
    {
        contents = new ArrayList<E>(collection);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void add(int index, E item)
    {
        contents.add(index, item);

        setChanged();
        notifyObservers();
    }


    // ----------------------------------------------------------
    public boolean add(E item)
    {
        boolean result = contents.add(item);

        setChanged();
        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean addAll(int index, Collection<? extends E> collection)
    {
        boolean result = contents.addAll(index, collection);

        setChanged();
        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean addAll(Collection<? extends E> collection)
    {
        boolean result = contents.addAll(collection);

        setChanged();
        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public void clear()
    {
        contents.clear();

        setChanged();
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

        setChanged();
        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean remove(Object object)
    {
        boolean result = contents.remove(object);

        setChanged();
        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean removeAll(Collection<?> collection)
    {
        boolean result = contents.removeAll(collection);

        setChanged();
        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public boolean retainAll(Collection<?> collection)
    {
        boolean result = contents.retainAll(collection);

        setChanged();
        notifyObservers();

        return result;
    }


    // ----------------------------------------------------------
    public E set(int index, E item)
    {
        E result = contents.set(index, item);

        setChanged();
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

            setChanged();
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

            setChanged();
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

            setChanged();
            notifyObservers();
        }


        // ----------------------------------------------------------
        public void set(E item)
        {
            iterator.set(item);

            setChanged();
            notifyObservers();
        }
    }
}
