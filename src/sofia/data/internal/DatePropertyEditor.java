package sofia.data.internal;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import sofia.data.PropertyEditor;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TimePicker;

public class DatePropertyEditor extends PropertyEditor
{
	private Calendar calendar;
	private boolean isValid;

	private Button dateButton;
	private Button timeButton;
	private DateFormat dateFormat;
	private DateFormat timeFormat;

	
	// ----------------------------------------------------------
	public DatePropertyEditor(String name, Method getter, Method setter)
	{
		super(name, getter, setter);
		
		calendar = new GregorianCalendar();
		calendar.setTime(new Date());
	}


	// ----------------------------------------------------------
	@Override
	public View createEditor(Context context)
	{
		dateFormat = android.text.format.DateFormat.getMediumDateFormat(
				context);
		timeFormat = android.text.format.DateFormat.getTimeFormat(context);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		
		dateButton = new Button(context);
		dateButton.setGravity(Gravity.CENTER);
		dateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view)
			{
				displayDatePicker(view.getContext());
			}
		});

		timeButton = new Button(context);
		timeButton.setGravity(Gravity.CENTER);
		timeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view)
			{
				displayTimePicker(view.getContext());
			}
		});

		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 3);

		layout.addView(dateButton, lp3);
		layout.addView(timeButton, lp1);

		return layout;
	}

	
	// ----------------------------------------------------------
	private void displayDatePicker(Context context)
	{
		DatePickerDialog dialog = new DatePickerDialog(context,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker picker, int year,
							int month, int day)
					{
						calendar.set(GregorianCalendar.YEAR, year);
						calendar.set(GregorianCalendar.MONTH, month);
						calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
						isValid = true;
						updateButtons();
					}
				},
				calendar.get(GregorianCalendar.YEAR),
				calendar.get(GregorianCalendar.MONTH),
				calendar.get(GregorianCalendar.DAY_OF_MONTH));

		dialog.show();
	}


	// ----------------------------------------------------------
	private void displayTimePicker(Context context)
	{
		TimePickerDialog dialog = new TimePickerDialog(context,
				new TimePickerDialog.OnTimeSetListener() {
					public void onTimeSet(
							TimePicker picker, int hours, int minutes)
					{
						calendar.set(GregorianCalendar.HOUR, hours);
						calendar.set(GregorianCalendar.MINUTE, minutes);
						isValid = true;
						updateButtons();
					}
				},
				calendar.get(GregorianCalendar.HOUR),
				calendar.get(GregorianCalendar.MINUTE),
				android.text.format.DateFormat.is24HourFormat(context));

		dialog.show();
	}


	// ----------------------------------------------------------
	private void updateButtons()
	{
		if (isValid)
		{
			dateButton.setText(dateFormat.format(calendar.getTime()));
			timeButton.setText(timeFormat.format(calendar.getTime()));
		}
		else
		{
			// TODO localize
			dateButton.setText("(no date selected)");
			timeButton.setText("--:--");
		}
	}


	// ----------------------------------------------------------
	@Override
	public void takeValueFromObject(Object object)
	{
		Date value = (Date) getValue(object);

		if (value == null)
		{
			isValid = false;
		}
		else
		{
			isValid = true;
			calendar.setTime(value);
		}

		updateButtons();
	}

	
	// ----------------------------------------------------------
	@Override
	public void giveValueToObject(Object object)
	{
		if (isValid)
		{
			setValue(object, calendar.getTime());
		}
		else
		{
			setValue(object, null);
		}
	}
}
