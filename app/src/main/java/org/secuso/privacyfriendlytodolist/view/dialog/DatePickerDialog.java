package org.secuso.privacyfriendlytodolist.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import org.secuso.privacyfriendlytodolist.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class DatePickerDialog extends FullScreenDialog {

    public interface DateCallback {
        void setDate(long deadline);

        void removeDate();
    }

    private DateCallback callback;

    public DatePickerDialog(Context context, long presetDate, int cancelButtonTextID) {
        super(context, R.layout.date_picker_dialog);
        initDatePicker(presetDate);
        initOkayButton();
        initCancelButton(cancelButtonTextID);
    }

    private void initDatePicker(long presetDate) {
        Calendar calendar = GregorianCalendar.getInstance();
        if (presetDate != -1) calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(presetDate));
        else calendar.setTime(Calendar.getInstance().getTime());
        DatePicker datePicker = findViewById(R.id.dp_date_picker);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    private void initOkayButton(){
        Button buttonOkay = findViewById(R.id.bt_date_picker_ok);
        buttonOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = findViewById(R.id.dp_date_picker);
                Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                callback.setDate(TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis()));

                dismiss();
            }
        });
    }

    private void initCancelButton(int cancelButtonText) {
        Button cancelButton = findViewById(R.id.bt_date_picker_no_date);
        cancelButton.setText(getContext().getResources().getText(cancelButtonText));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.removeDate();
                dismiss();
            }
        });
    }

    public void setCallback(DateCallback callback) {
        this.callback = callback;
    }
}