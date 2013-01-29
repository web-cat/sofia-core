package sofia.util;

import java.util.LinkedHashSet;
import java.util.Set;

import sofia.internal.events.EventDispatcher;

//--------------------------------------------------------------------------
/**
 * A base class for classes that want to send out change notifications using
 * the "observer" pattern.
 * <p>
 * If the variable {@code observable} is a reference to
 * an object that extends the {@code Observable} class, then any other object
 * can observe changes to it by calling {@link #addObserver(Object)}:
 * </p>
 * <pre>
 * observable.addObserver(x);</pre>
 * <p>
 * In this case, when a change notification occurs, the system will call, on
 * the object {@code x}, a method named {@code changeWasObserved}, whose first
 * parameter is the same type (or a superclass) as {@code observable}. See the
 * documentation for the {@link #notifyObservers(Object...)} method to see how
 * the {@code changeWasObserved} callback can take additional parameters as
 * well.
 * </p><p>
 * To use a different method name than {@code changeWasObserved}, pass its name
 * as the second parameter to {@link #addObserver(Object, String)}:
 * </p>
 * <pre>
 * observable.addObserver(x, "methodToCall");</pre>
 *
 * @author  Tony Allevato
 * @version 2012.04.16
 */
public abstract class Observable
{
    //~ Fields ................................................................

    // The default name of the method that will be called on each observer
    // object.
    private static final String DEFAULT_METHOD_NAME = "changeWasObserved";

    // The set of observers for this object.
    private transient LinkedHashSet<Observer> observers;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes the object with an empty set of observers.
     */
    public Observable()
    {
        observers = new LinkedHashSet<Observer>();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * <p>
     * Adds the specified object {@code observer} to the set of observers for
     * this object. When an instance of this observable object calls
     * {@link #notifyObservers()}, the {@code changeWasObserved} method will be
     * called on the observer.
     * </p><p>
     * The {@code changeWasObserved} method on the observer must take a single
     * argument that is the same type (or a superclass) of this observable
     * object. You can also have other overloads of {@code changeWasObserved}
     * that take additional parameters after the observable parameter; these
     * match the parameters that the observable passes when it calls
     * {@link #notifyObservers(Object...)}.
     * </p>
     *
     * @param observer the object that will observe changes to this object
     */
    public synchronized void addObserver(Object observer)
    {
        addObserver(observer, DEFAULT_METHOD_NAME);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Adds the specified object {@code observer} to the set of observers for
     * this object, but using a method name different from the default. When
     * an instance of this observable object calls {@link #notifyObservers()},
     * the method with the specified name will be called on the observer.
     * </p><p>
     * The method specified here must take a single argument that is the same
     * type (or a superclass) of this observable object. You can also have
     * other overloads of this method that take additional parameters after the
     * observable parameter; these match the parameters that the observable
     * passes when it calls {@link #notifyObservers(Object...)}.
     * </p>
     *
     * @param observer the object that will observe changes to this object
     * @param method the name of the method that will be called on the observer
     */
    public synchronized void addObserver(Object observer, String method)
    {
        observers.add(new Observer(observer, method));
    }


    // ----------------------------------------------------------
    /**
     * Removes the specified object's {@code changeWasObserved} method from the
     * set of observers for the receiver. In other words, after this method is
     * called, the {@code changeWasObserved} method will no longer be called
     * when this object notifies observers of changes.
     *
     * @param observer the observer whose {@code changeWasObserved} method
     *     should be removed from the set of observers
     */
    public synchronized void removeObserver(Object observer)
    {
        removeObserver(observer, DEFAULT_METHOD_NAME);
    }


    // ----------------------------------------------------------
    /**
     * Removes the specified object's named method from the set of observers
     * for the receiver. In other words, after this method is called, the
     * method with the name given in the {@code method} argument will no longer
     * be called when this object notifies observers of changes.
     *
     * @param observer the observer whose method should be removed from the set
     *     of observers
     * @param method the method that should be removed from the set of
     *     observers
     */
    public synchronized void removeObserver(Object observer, String method)
    {
        removeObserver(new Observer(observer, method));
    }


    // ----------------------------------------------------------
    /**
     * Removes all observers from the receiver.
     */
    public synchronized void clearObservers()
    {
        observers.clear();
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Notifies all observers of the receiver that a change has occurred
     * affecting the state of the object. Typically, this method is called
     * from inside a setter method or some other method that performs a
     * computation or an action on an observable object.
     * </p><p>
     * This method can take an argument list of your choosing. These arguments
     * are passed directly to the {@code changeWasObserved} method, after the
     * observable object itself, on the observer object. So, for example, if
     * your subclass of {@code Observable} is called {@code MyModel}, and you
     * call {@code notifyObservers} from within it like this:
     * </p>
     * <pre>
     *     notifyObservers("hello", 5, 9.3);</pre>
     * <p>
     * Then the observer would be searched for a {@code changeWasObserved}
     * method that has its first parameter of type {@code MyModel} (or a
     * superclass) followed by parameter types compatible with those in the
     * argument list above; for example:
     * </p>
     * <pre>
     *     public void changeWasObserved(MyModel model, String str, int x, double y)</pre>
     * <p>
     * If no such method exists, then an attempt is made to call one that takes
     * only the observable object as a parameter.
     * </p>
     * <pre>
     *     public void changeWasObserved(MyModel model)</pre>
     * <p>
     * The order that the observers are called is undefined. User code should
     * not be written that depends on some observers being called before or
     * after others.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public void notifyObservers(Object... arguments)
    {
        Set<Observer> clonedObservers;
        synchronized (this)
        {
            clonedObservers = (Set<Observer>) observers.clone();
        }

        for (Observer observer : clonedObservers)
        {
            observer.observe(this, arguments);
        }
    }


    // ----------------------------------------------------------
    /**
     * Removes the internal observer from the set of observers.
     *
     * @param observer the internal observer
     */
    private synchronized void removeObserver(Observer observer)
    {
        observers.remove(observer);
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    /**
     * Encapsulates the information needed to represent an observer -- the
     * receiving object, the method to call on it, and a method dispatcher to
     * perform the dynamic call.
     */
    private static class Observer
    {
        //~ Fields ............................................................

        private Object receiver;
        private String method;
        private EventDispatcher event;


        //~ Constructors ......................................................

        // ----------------------------------------------------------
        public Observer(Object receiver, String method)
        {
            this.receiver = receiver;
            this.method = method;
            event = new EventDispatcher(method);
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        public void observe(Object object, Object... arguments)
        {
            // Create a new argument array that has the observable object
            // first, followed by the remaining arguments.
            Object[] realArgs = new Object[arguments.length + 1];
            realArgs[0] = object;
            System.arraycopy(arguments, 0, realArgs, 1, arguments.length);

            // Try to dispatch first to the one that takes the actual
            // arguments, or to one that just takes the observable object.
            @SuppressWarnings("unused")
            boolean result =
                    event.dispatch(receiver, realArgs) ||
                    event.dispatch(receiver, object);
        }


        // ----------------------------------------------------------
        @Override
        public int hashCode()
        {
            return receiver.getClass().hashCode() ^ method.hashCode();
        }


        // ----------------------------------------------------------
        @Override
        public boolean equals(Object other)
        {
            if (other instanceof Observer)
            {
                Observer otherObserver = (Observer) other;

                return receiver == otherObserver.receiver
                        && method.equals(otherObserver.method);
            }
            else
            {
                return false;
            }
        }
    }
}
