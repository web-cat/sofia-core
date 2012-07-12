package sofia.data.internal;

import java.lang.reflect.Method;

import sofia.data.PropertyEditor;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

public class BooleanPropertyEditor extends PropertyEditor
{
	private CheckBox editor;


	// ----------------------------------------------------------
	public BooleanPropertyEditor(String name, Method getter, Method setter)
	{
		super(name, getter, setter);
	}


	// ----------------------------------------------------------
	@Override
	public View createLabel(Context context)
	{
		return null;
	}


	// ----------------------------------------------------------
	@Override
	public View createEditor(Context context)
	{
		editor = new CheckBox(context);
		editor.setText(getLabelText(context));

		return editor;
	}

	
	// ----------------------------------------------------------
	@Override
	public void takeValueFromObject(Object object)
	{
		boolean value = (Boolean) getValue(object);
		editor.setChecked(value);
	}

	
	// ----------------------------------------------------------
	@Override
	public void giveValueToObject(Object object)
	{
		boolean value = editor.isChecked();
		setValue(object, value);
	}
}
