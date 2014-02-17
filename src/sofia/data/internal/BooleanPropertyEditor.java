/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.data.internal;

import sofia.data.PropertyEditor;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import java.lang.reflect.Method;

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
