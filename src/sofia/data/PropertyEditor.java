package sofia.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import sofia.data.internal.BooleanPropertyEditor;
import sofia.data.internal.DatePropertyEditor;
import sofia.data.internal.StringPropertyEditor;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public abstract class PropertyEditor implements Comparable<PropertyEditor>
{
	private String name;
	private Method getter;
	private Method setter;


	// ----------------------------------------------------------
	public PropertyEditor(String name, Method getter, Method setter)
	{
		this.name = name;
		this.getter = getter;
		this.setter = setter;
	}


	// ----------------------------------------------------------
	public static PropertyEditor create(Class<?> type, String name,
			Method getter, Method setter)
	{
		// TODO types to support:
		//   Number: EditText with appropriate keypad
		//   String: EditText
		//   Date: EditText with popup calendar
		//   Color:
		//   Boolean: CheckBox
		//   Enum: Drop-down list
		//
		// Handle annotations such as @Property to convert strings to
		// drop-downs

		if (String.class.isAssignableFrom(type))
		{
			return new StringPropertyEditor(name, getter, setter);
		}
		else if (boolean.class.equals(type))
		{
			return new BooleanPropertyEditor(name, getter, setter);
		}
		else if (Date.class.isAssignableFrom(type))
		{
			return new DatePropertyEditor(name, getter, setter);
		}
		else
		{
			return null;
		}
	}


	// ----------------------------------------------------------
	public String getName()
	{
		return name;
	}


	// ----------------------------------------------------------
	protected String getLabelText(Context context)
	{
		Property annotation = getter.getAnnotation(Property.class);

		if (annotation != null)
		{
			if (annotation.labelId() != 0)
			{
				return context.getResources().getString(annotation.labelId());
			}
			else if (annotation.label().length() > 0)
			{
				return annotation.label();
			}
		}

		return friendlyName(name);
	}


	// ----------------------------------------------------------
	protected String getHintText(Context context)
	{
		Property annotation = getter.getAnnotation(Property.class);

		if (annotation != null)
		{
			if (annotation.hintId() != 0)
			{
				return context.getResources().getString(annotation.hintId());
			}
			else if (annotation.hint().length() > 0)
			{
				return annotation.hint();
			}
		}

		return null;
	}


	// ----------------------------------------------------------
	private static String friendlyName(String name)
	{
		return name.replaceAll("([A-Z]+)", " $1");
	}


	// ----------------------------------------------------------
	public View createLabel(Context context)
	{
		TextView tv = new TextView(context);
		tv.setText(getLabelText(context));
		return tv;
	}


	// ----------------------------------------------------------
	public abstract View createEditor(Context context);


	// ----------------------------------------------------------
	public abstract void takeValueFromObject(Object object);
	
	
	// ----------------------------------------------------------
	public abstract void giveValueToObject(Object object);


	// ----------------------------------------------------------
	public Class<?> getType()
	{
		return getter.getReturnType();
	}


	// ----------------------------------------------------------
	protected Object getValue(Object receiver)
	{
		try
		{
			return getter.invoke(receiver);
		}
		catch (InvocationTargetException e)
		{
			// TODO handle the error
			return null;
		}
		catch (IllegalAccessException e)
		{
			// This should never happen; properties are only created from
			// public methods.
			
			return null;
		}
	}


	// ----------------------------------------------------------
	protected void setValue(Object receiver, Object value)
	{
		try
		{
			setter.invoke(receiver, value);
		}
		catch (InvocationTargetException e)
		{
			// TODO handle the error
		}
		catch (IllegalAccessException e)
		{
			// This should never happen; properties are only created from
			// public methods.
		}
	}
	
	
	// ----------------------------------------------------------
	public int compareTo(PropertyEditor other)
	{
		Property annotation = getter.getAnnotation(Property.class);
		Property otherAnnotation = other.getter.getAnnotation(Property.class);

		int order = 0;
		int otherOrder = 0;
		
		if (annotation != null)
		{
			order = annotation.order();
		}
		
		if (otherAnnotation != null)
		{
			otherOrder = otherAnnotation.order();
		}

		if (order != otherOrder)
		{
			return order - otherOrder;
		}
		else
		{
			return getName().compareToIgnoreCase(other.getName());
		}
	}
}
