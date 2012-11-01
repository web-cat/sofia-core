package sofia.widget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
 * <p>
 * Indicates which method should be called on an object to provide its title
 * in a {@link ListView}, {@link Spinner}, or some other widget that uses
 * <em>decoration</em> to render arbitrary objects.
 * </p><p>
 * If a widget tries to render an object that does not have any methods marked
 * with this annotation, then it will call the {@link Object#toString()}
 * method to get its title.
 * </p>
 * 
 * @author  Tony Allevato
 * @version 2012.10.14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProvidesTitle
{
}
