package sofia.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property
{
	String label() default "";

	int labelId() default 0;
	
	String hint() default "";
	
	int hintId() default 0;
	
	String category() default "";
	
	int categoryId() default 0;
	
	int order() default 0;
	
	Class<? extends PropertyEditor> editor() default PropertyEditor.class;
}
