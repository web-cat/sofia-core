package sofia.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
 * <p>
 * Provides custom persistence support for objects of the annotated class.
 * </p><p>
 * By default, most classes are persisted by storing all of their fields to
 * the device's memory, and then reloaded by creating an instance using a
 * parameterless constructor and then setting all of its fields using the saved
 * data. If the class does not have a parameterless constructor, however, this
 * process will fail.
 * </p><p>
 * If a class {@code package.MyClass} has a class in the same package named
 * {@code package.MyClassPersistor}, then that class will be used to persist
 * objects of that class. If the default package/naming convention is
 * inappropriate for some reason, then this annotation can be used to provide
 * the name of the persistor to use.
 * </p><p>
 * The persistor class must define two methods:
 * </p>
 * <dl>
 * <dt>{@code public static void represent(Object obj, Map<String, Object> rep)}</dt>
 * <dd>Called when writing the object {@code obj} out to persistent storage.
 * This method should store any data in {@code rep} that would be needed to
 * reconstruct the object the next time the application runs.</dd>
 *
 * <dt>{@code public static Object construct(Map<String, Object> rep)}</dt>
 * <dd>Called when a persisted object is being reconstructed from stored data.
 * This method should use the values that were stored in the map {@code rep}
 * to recreate the object and return it.</dd>
 * </dl>
 * 
 * @author Tony Allevato
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Persistor
{
	// ----------------------------------------------------------
	/**
	 * Indicates a class containing static {@code represent} and
	 * {@code construct} methods that will be called to store and load objects
	 * belonging to the annotated class.
	 */
	public Class<?> value();
}
