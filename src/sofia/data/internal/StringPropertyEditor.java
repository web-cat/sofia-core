package sofia.data.internal;

import java.lang.reflect.Method;

import sofia.data.PropertyEditor;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class StringPropertyEditor extends PropertyEditor
{
	private EditText editor;


	// ----------------------------------------------------------
	public StringPropertyEditor(String name, Method getter, Method setter)
	{
		super(name, getter, setter);
	}


	// ----------------------------------------------------------
	@Override
	public View createEditor(Context context)
	{
		editor = new EditText(context);
		editor.setHint(getHintText(context));

		return editor;
	}

	
	// ----------------------------------------------------------
	@Override
	public void takeValueFromObject(Object object)
	{
		String value = (String) getValue(object);
		
		if (value == null)
		{
			editor.setText("");
		}
		else
		{
			editor.setText(value);
		}
	}

	
	// ----------------------------------------------------------
	@Override
	public void giveValueToObject(Object object)
	{
		String value = editor.getText().toString();
		
		if (value.length() == 0)
		{
			setValue(object, null);
		}
		else
		{
			setValue(object, value);
		}
	}
}
