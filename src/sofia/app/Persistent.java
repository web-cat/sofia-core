package sofia.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
 * <p>
 * Indicates that a field in a {@link Screen} subclass should be saved to disk
 * when the activity is dismissed and restored the next time the activity is
 * started.
 * </p><p>
 * By default, the field will be set to null (or zero for primitive types) if
 * it is not found when loading the activity's persistent storage file. (This
 * can happen when the application is first loaded, or if new persistent fields
 * are added during development.) 
 * </p><p>
 * This default behavior can be changed so that a new instance of an object is
 * created when it isn't found in storage. To do this, set the {@code create}
 * property of the annotation to {@code true}:
 * <pre>    &#64;Persistent(create = true)</pre>
 * Using this mode is preferable when you always want the field to refer to a
 * valid object, since the objects are automatically created before the
 * screen's {@code initialize} method is called. Note: When {@code create} is
 * set to {@code true}, the type of the field must have a parameterless
 * constructor or an exception will be thrown at runtime.
 * </p><p>
 * Making a field persistent places some restrictions on the type of that field
 * and types reachable from it. If the {@code create} property is set to
 * {@code true}, then the field's type must be a primitive type or have a
 * parameterless constructor. Any types reachable from that type (its fields,
 * fields in those objects, and so on) must also be primitive types or have
 * parameterless constructors.
 * </p><p>
 * As with standard Java serialization, you can use the {@code transient}
 * keyword on a field inside a persistent object to prevent that field from
 * being written to the data store or read back out.
 * </p><p>
 * There are some common types that might be useful to have inside a persisted
 * object, despite not having a parameterless constructor. Extra support has
 * been added to make these persistable:
 * <ul>
 * <li>{@link GeoPoint}</li>
 * <li>{@link sofia.graphics.Color}</li>
 * </ul>
 * </p>
 * 
 * @author  Tony Allevato
 * @version 2012.04.29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Persistent
{
	// ----------------------------------------------------------
	/**
	 * Indicates that the field should be set to a new instance of its type if
	 * the field is not found in persistent storage.
	 */
	boolean create() default false;
}
