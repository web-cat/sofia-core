package sofia.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
* Use this annotation on a subclass of {@link Screen} to specify the layout
* resource that should be inflated when that screen is displayed.
* 
* @author  Tony Allevato
* @version 2012.04.29
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ScreenLayout
{
	// ----------------------------------------------------------
	/**
	 * The identifier of the layout resource (an {@code R.layout.*} constant)
	 * that should be inflated on the screen.
	 */
	public int value();
}
