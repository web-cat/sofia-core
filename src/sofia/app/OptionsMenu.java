package sofia.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
 * <p>
 * Use this annotation on a subclass of {@link Screen} to specify the menu
 * resource that should be loaded when the user presses the Menu button.
 * </p><p>
 * This annotation supports three usage forms:
 * </p>
 * <dl>
 * <dt>{@code @OptionsMenu("foo")}</dt>
 * <dd>This will search for a menu defined in the project's
 * {@code res/menu/foo.xml} file. This is the preferred form, since it
 * performs a more advanced search than the numeric "id" form below.</dd>
 * <dt>{@code @OptionsMenu(id = R.menu.foo)}</dt>
 * <dd>This will find the same menu as above, but using the auto-generated
 * numeric ID of the menu resource instead.</dd>
 * <dt>{@code @OptionsMenu}</dt>
 * <dd>When specified without parameters, a lookup based on the name of the
 * class is performed. For example, if the annotation is placed on a class
 * named {@code MyScreen}, then this will search for a menu in
 * {@code res/menu/myscreen.xml}, followed by
 * {@code res/menu/my_screen.xml}.</dd>
 * </dl>
 * <p>
 * This annotation must be present for the screen to have an options menu that
 * is automatically loaded from a resource.
 * </p>
 * 
 * @author  Tony Allevato
 * @version 2012.04.29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface OptionsMenu
{
	//~ Values ................................................................

	// ----------------------------------------------------------
	/**
	 * The name of a menu resource, without the {@code "res/menu"}
	 * prefix or {@code .xml} extension. For example, {@code "foo"} would
	 * refer to {@code "res/menu/foo.xml"}.
	 */
	public String value() default "";
	
	
	// ----------------------------------------------------------
	/**
	 * The ID of a menu resource; for example, {@code R.menu.foo}.
	 */
	public int id() default 0;
}
