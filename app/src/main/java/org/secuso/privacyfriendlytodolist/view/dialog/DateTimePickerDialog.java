/*
 This file is part of Privacy Friendly To-Do List.

 Privacy Friendly To-Do List is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly To-Do List is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly To-Do List. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlytodolist.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import org.secuso.privacyfriendlytodolist.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;


public class DateTimePickerDialog extends FullScreenDialog {

    private DateTimeCallback callback;

    public DateTimePickerDialog(Context context, long presetTime, long fallbackPresetTime, int cancelButtonTextID) {
        super(context, R.layout.date_time_picker_dialog);

        Calendar calendar = GregorianCalendar.getInstance();
        if(presetTime != -1) calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(presetTime));
        else if(fallbackPresetTime != -1) calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(fallbackPresetTime)); //TODO subtract predefined reminder interval
        else calendar.setTime(Calendar.getInstance().getTime());

        DatePicker datePicker = (DatePicker) findViewById(R.id.dp_date_time_picker);
        //datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                LinearLayout layoutDate = (LinearLayout)findViewById(R.id.ll_date_time_picker_date);
                layoutDate.setVisibility(View.GONE);
                LinearLayout layoutTime = (LinearLayout)findViewById(R.id.ll_date_time_picker_time);
                layoutTime.setVisibility(View.VISIBLE);
            }
        });

        TimePicker timePicker = (TimePicker) findViewById(R.id.tp_date_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE)+1);

        Button buttonDate = (Button) findViewById(R.id.bt_date_time_picker_date);
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layoutDate = (LinearLayout)findViewById(R.id.ll_date_time_picker_date);
                layoutDate.setVisibility(View.VISIBLE);
                LinearLayout layoutTime = (LinearLayout)findViewById(R.id.ll_date_time_picker_time);
                layoutTime.setVisibility(View.GONE);
            }
        });
        Button buttonTime = (Button) findViewById(R.id.bt_date_time_picker_time);
        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layoutDate = (LinearLayout)findViewById(R.id.ll_date_time_picker_date);
                layoutDate.setVisibility(View.GONE);
                LinearLayout layoutTime = (LinearLayout)findViewById(R.id.ll_date_time_picker_time);
                layoutTime.setVisibility(View.VISIBLE);
            }
        });

        Button buttonOkay = (Button) findViewById(R.id.bt_date_time_picker_ok);
        buttonOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = (DatePicker) findViewById(R.id.dp_date_time_picker);
                TimePicker timePicker = (TimePicker) findViewById(R.id.tp_date_time_picker);
                Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                callback.setDateTime(TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis()));

                dismiss();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.bt_date_time_picker_no_date_time);
        cancelButton.setText(getContext().getResources().getText(cancelButtonTextID));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.removeDateTime();
                dismiss();
            }
        });

    }

    public interface DateTimeCallback {
        void setDateTime(long dateTime);
        void removeDateTime();
    }

    public void setCallback(DateTimeCallback callback) {
        this.callback = callback;
    }

}
