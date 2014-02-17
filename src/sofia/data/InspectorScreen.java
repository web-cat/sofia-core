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

package sofia.data;

import sofia.app.Screen;
import sofia.data.internal.Inspector;
import sofia.data.internal.InspectorScreenProxy;
import sofia.internal.ReflectiveListWrapper;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class InspectorScreen extends Screen
{
	private InspectorScreenProxy proxy;


	public void initialize(Object object)
	{
		if (object instanceof List)
		{
			proxy = new ListInspector();
		}
		else if (object.getClass().isArray())
		{
			//proxy = new ListInspector();
		}
		else if (object instanceof Map)
		{
			proxy = new MapInspector();
		}
		else
		{
			proxy = new ObjectInspector();
		}
		
		proxy.initialize(object);
	}


	private class ObjectInspector implements InspectorScreenProxy
	{
		private Object object;
		private List<PropertyEditor> properties;
		private List<View> itemViews;
		
		private ListView listView;
		private PropertyListAdapter adapter;


		public void initialize(Object object)
		{
			this.object = object;

			Inspector inspector = new Inspector(object.getClass());
			properties = inspector.getProperties();

			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

			setContentView(createForm());
		}

		
		public void resume()
		{
			adapter.notifyDataSetChanged();
		}


		public void store()
		{
			// TODO only if not canceled
			
			for (PropertyEditor editor : properties)
			{
				editor.giveValueToObject(object);
			}
		}		

	
		private ViewGroup createForm()
		{
			itemViews = new ArrayList<View>();

			// TODO need a button bar for Done/Cancel controls

			listView = new ListView(InspectorScreen.this);
			adapter = new PropertyListAdapter();
			listView.setAdapter(adapter);
			
			for (PropertyEditor editor : properties)
			{
				View label = editor.createLabel(InspectorScreen.this);			
				View editorView = editor.createEditor(InspectorScreen.this);
				editor.takeValueFromObject(object);

				if (label == null)
				{
					itemViews.add(editorView);
				}
				else
				{
					LinearLayout layout = new LinearLayout(InspectorScreen.this);
					layout.setOrientation(LinearLayout.VERTICAL);

					layout.addView(label);
					layout.addView(editorView);
					
					itemViews.add(layout);
				}
			}

			return listView;
		}
		
		
		private class PropertyListAdapter extends BaseAdapter
		{
			public int getCount()
			{
				return properties.size();
			}

			public Object getItem(int index)
			{
				return properties.get(index);
			}

			public long getItemId(int paramInt)
			{
				return 0;
			}

			public View getView(int index, View convertView, ViewGroup parent)
			{
				return itemViews.get(index);
			}
		}
	}


	private class ListInspector implements InspectorScreenProxy
	{
		private List<?> list;
		private ReflectiveListWrapper wrappedList;
		private SimpleAdapter adapter;

		private Object pendingNewItem;

		private Button newButton;
		private Button deleteButton;
		private ListView listView;

		public void initialize(Object object)
		{
			this.list = (List<?>) object;
			this.wrappedList = new ReflectiveListWrapper(list);

			setContentView(createForm());
		}

		
		public void resume()
		{
			if (pendingNewItem != null)
			{
				
			}

			adapter.notifyDataSetChanged();
		}


		public void store()
		{
		}		

	
		private ViewGroup createForm()
		{
			// TODO do we want a button bar here to add items? Figure out the
			// best UI for this

			newButton = new Button(InspectorScreen.this);
			newButton.setText("New...");
			newButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view)
				{
					newClicked();
				}
			});

			deleteButton = new Button(InspectorScreen.this);
			deleteButton.setText("Delete");
			deleteButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view)
				{
					deleteClicked();
				}
			});

			LinearLayout buttonBar = createButtonBar(newButton, deleteButton);
			buttonBar.setId(1001);

			listView = new ListView(InspectorScreen.this);
			listView.setId(1000);
			listView.setOnItemClickListener(
					new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapterView,
						View itemView, int index, long id)
				{
					presentScreen(InspectorScreen.class, list.get(index));
				}
			});

			adapter = new SimpleAdapter(InspectorScreen.this,
					wrappedList, android.R.layout.simple_list_item_1,
					new String[] { "toString" },
					new int[] { android.R.id.text1 });
			listView.setAdapter(adapter);
			
			return createDualLayout(listView, buttonBar);
		}
		
		
		private void newClicked()
		{
			
		}

	
		private void deleteClicked()
		{
			adapter = new SimpleAdapter(InspectorScreen.this,
					wrappedList, android.R.layout.simple_list_item_checked,
					new String[] { "toString" },
					new int[] { android.R.id.text1 });
			listView.setAdapter(adapter);
		}
	}

	
	private class MapInspector implements InspectorScreenProxy
	{
		private Map<?, ?> map;
		private List<Map.Entry<?, ?>> sortedEntries;
		private ReflectiveListWrapper wrappedEntries;
		private SimpleAdapter adapter;


		public void initialize(Object object)
		{
			this.map = (Map<?, ?>) object;
			
			sortedEntries = new ArrayList<Map.Entry<?, ?>>(map.entrySet());

			Collections.sort(sortedEntries, new Comparator<Map.Entry<?, ?>>() {
				@SuppressWarnings("unchecked")
				public int compare(Map.Entry<?, ?> a, Map.Entry<?, ?> b)
				{
					if (a.getKey() instanceof Comparable)
					{
						return ((Comparable<Object>) a.getKey()).compareTo(
								b.getKey());
					}
					else
					{
						return a.getKey().toString().compareTo(
								b.getKey().toString());
					}
				}
			});

			this.wrappedEntries = new ReflectiveListWrapper(sortedEntries);

			setContentView(createForm());
		}

		
		public void resume()
		{
			adapter.notifyDataSetChanged();
		}


		public void store()
		{
		}		

	
		private ViewGroup createForm()
		{
			// TODO do we want a button bar here to add items? Figure out the
			// best UI for this

			ListView listView = new ListView(InspectorScreen.this);
			adapter = new SimpleAdapter(InspectorScreen.this,
					wrappedEntries, android.R.layout.simple_list_item_2,
					new String[] { "key.toString", "value.toString" },
					new int[] { android.R.id.text1, android.R.id.text2 });
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(
					new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapterView,
						View itemView, int index, long id)
				{
					presentScreen(InspectorScreen.class,
							sortedEntries.get(index).getValue());
				}
			});
			return listView;
		}
	}

	
	private LinearLayout.LayoutParams layoutParams(int h, int v)
	{
		return new LinearLayout.LayoutParams(h, v);
	}


	@Override
	protected void onPause()
	{
		proxy.store();
		super.onPause();
	}


	@Override
	protected void onResume()
	{
		proxy.resume();
		super.onResume();
	}
	
	
	private LinearLayout createDualLayout(View mainView, View buttonBar)
	{
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams mainLp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		layout.addView(mainView, mainLp);

		LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(buttonBar, barLp);

		return layout;
	}


	private LinearLayout createButtonBar(Button... buttons)
	{
		LinearLayout bar = new LinearLayout(this);

		for (Button button : buttons)
		{
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);

			bar.addView(button, lp);
		}

		return bar;
	}
}
