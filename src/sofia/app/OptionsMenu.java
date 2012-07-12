package sofia.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
 * Use this annotation on a subclass of {@link Screen} to specify the menu
 * resource that should be loaded when the user presses the Menu button.
 * 
 * @author  Tony Allevato
 * @version 2012.04.29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface OptionsMenu
{
	// ----------------------------------------------------------
	/**
	 * The identifier of the menu resource (an {@code R.menu.*} constant) that
	 * should be used as the Options menu for the screen.
	 */
	public int value();
}
