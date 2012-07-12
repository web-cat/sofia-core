package sofia.util;

import java.util.LinkedHashSet;
import java.util.Set;

import sofia.internal.MethodDispatcher;

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
 * the object {@code x}, a method named {@code changeWasObserved} that takes
 * a single parameter whose type is the same type (or a superclass) as
 * {@code observable}.
 * </p>
 * <p>
 * To use a different method name than {@code changeWasObserved}, pass its name
 * as the second parameter to {@link #addObserver(Object, String)}:
 * </p>
 * <pre>
 * observable.addObserver(x, "methodToCall");</pre>
 * 
 * @author Tony Allevato
 * @version 2012.04.16
 */
public abstract class Observable
{
	//~ Instance/static variables .............................................

	private static final String DEFAULT_METHOD_NAME = "changeWasObserved";
	
	private LinkedHashSet<Observer> observers;


	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	public Observable()
	{
		observers = new LinkedHashSet<Observer>();
	}
	
	
	//~ Methods ...............................................................

	// ----------------------------------------------------------
	public synchronized void addObserver(Object receiver)
	{
		addObserver(receiver, DEFAULT_METHOD_NAME);
	}


	// ----------------------------------------------------------
	public synchronized void addObserver(Object receiver, String method)
	{
		observers.add(new Observer(receiver, method));
	}


	// ----------------------------------------------------------
	public synchronized void removeObserver(Observer observer)
	{
		observers.remove(observer);
	}

	
	// ----------------------------------------------------------
	public synchronized void removeObserver(Object receiver, String method)
	{
		removeObserver(new Observer(receiver, method));
	}

	
	// ----------------------------------------------------------
	public synchronized void clearObservers()
	{
		observers.clear();
	}

	
	// ----------------------------------------------------------
	@SuppressWarnings("unchecked")
	public void notifyObservers()
	{
		Set<Observer> clonedObservers;
	    synchronized (this)
	    {
	    	clonedObservers = (Set<Observer>) observers.clone();
	    }
	    
	    for (Observer observer : clonedObservers)
	    {
	    	observer.observe(this);
	    }
	}


	// ----------------------------------------------------------
	private static class Observer
	{
		private Object receiver;
		private String method;
		private MethodDispatcher dispatcher;


		// ----------------------------------------------------------
		public Observer(Object receiver, String method)
		{
			this.receiver = receiver;
			this.method = method;
			
			dispatcher = new MethodDispatcher(method, 1);
		}


		// ----------------------------------------------------------
		public void observe(Object object)
		{
			dispatcher.callMethodOn(receiver, object);
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
