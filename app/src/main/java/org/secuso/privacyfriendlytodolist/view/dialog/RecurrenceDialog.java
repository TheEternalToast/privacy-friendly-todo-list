package org.secuso.privacyfriendlytodolist.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.secuso.privacyfriendlytodolist.R;
import org.secuso.privacyfriendlytodolist.model.Helper;
import org.secuso.privacyfriendlytodolist.model.Recurrence;
import org.secuso.privacyfriendlytodolist.model.RecurrenceSelectionOption;

import java.util.Arrays;

public class RecurrenceDialog extends FullScreenDialog {

    // Internal Interfaces
    public interface RecurrenceCallback {
        void setRecurrence(Recurrence recurrence);

        void removeRecurrence();
    }

    // Instance variables
    private EditText incrementEditText;
    private GridView selectionGridView;
    private TextView startDateTextView;
    private TextView endDateTextView;

    private final Recurrence recurrence;

    private RecurrenceCallback callback;

    // Constructors
    public RecurrenceDialog(Context context) {
        super(context, R.layout.recurrence_dialog);
        recurrence = new Recurrence();
        initDialog();
    }

    public RecurrenceDialog(Context context, Recurrence recurrence) {
        super(context, R.layout.recurrence_dialog);
        this.recurrence = recurrence;
        initDialog();
    }

    // Inits
    //  entire dialog
    private void initDialog() {
        initTypeSpinner();
        initIncrementEditText();
        initSelectionGrid();
        initCancelButton();
        Button okayButton = initOkayButton();
        initStartDate(okayButton);
        initEndDate(okayButton);
    }

    //  recurrence type Spinner
    private void initTypeSpinner() {
        Spinner typeSpinner = findViewById(R.id.s_recurrence_type);
        typeSpinner.setAdapter(new ArrayAdapter<Recurrence.Type>(
                getContext(),
                R.layout.spinner_list_item,
                Arrays.copyOfRange(Recurrence.Type.values(), 1, Recurrence.Type.values().length)
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createViewFromResource(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createViewFromResource(position, convertView, parent);
            }

            private TextView createViewFromResource(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view;
                if (convertView == null)
                    view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.spinner_list_item, parent, false);
                else
                    view = (TextView) convertView;
                Recurrence.Type item = getItem(position);
                if (item == null) return view;
                view.setText(item.toString(getContext()));
                return view;
            }
        });
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recurrence.setType((Recurrence.Type) parent.getItemAtPosition(position));
                adjustIncrementAndSelectionToType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                recurrence.setType(Recurrence.Type.NONE);
                adjustIncrementAndSelectionToType();
            }
        });
        typeSpinner.setSelection(Math.max(0, recurrence.getType().getValue() - 1));
    }

    //  increment text field
    private void initIncrementEditText() {
        incrementEditText = findViewById(R.id.tv_recurrence_increment);
        incrementEditText.setText(Integer.toString(recurrence.getIncrement()));
        incrementEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    recurrence.setIncrement(Integer.parseInt(s.toString()));
                } catch (NumberFormatException ignored) {
                }
            }
        });
        incrementEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (incrementEditText.getVisibility() == View.VISIBLE && !hasFocus)
                    incrementEditText.setText(recurrence.getIncrement());
            }
        });
    }

    //  selection grid
    private void initSelectionGrid() {
        selectionGridView = findViewById(R.id.gv_recurrence_selection);
        selectionGridView.setAdapter(new ArrayAdapter<>(
                getContext(),
                R.layout.recurrence_selection_toggle_button,
                new RecurrenceSelectionOption[]{}
        ));
        selectionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToggleButton option = (ToggleButton) view;
                int item = (Integer) parent.getItemAtPosition(position);
                if (option.isChecked()) {
                    recurrence.removeFromSelection(item);
                    option.setChecked(false);
                } else {
                    recurrence.addToSelection(item);
                    option.setChecked(true);
                }
            }
        });
    }

    //  initialize start date view
    private void initStartDate(Button okayButton) {
        startDateTextView = findViewById(R.id.tv_recurrence_start_date);
        startDateTextView.setTextColor(okayButton.getCurrentTextColor());
        startDateTextView.setText(Helper.getDate(getContext(), recurrence.getStartDate()));
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog startDateDialog = new DatePickerDialog(
                        getContext(),
                        -1, // -> today
                        R.string.cancel
                );
                startDateDialog.setCallback(new DatePickerDialog.DateCallback() {
                    @Override
                    public void setDate(long d) {
                        recurrence.setStartDate(d);
                        startDateTextView.setText(Helper.getDate(getContext(), recurrence.getStartDate()));
                    }

                    @Override
                    public void removeDate() {
                    }
                });
                startDateDialog.show();
            }
        });
    }

    //  initialize end date view
    private void initEndDate(Button okayButton) {
        endDateTextView = findViewById(R.id.tv_recurrence_end_date);
        endDateTextView.setTextColor(okayButton.getCurrentTextColor());
        endDateTextView.setText(Helper.getDate(getContext(), recurrence.getEndDate()));
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog deadlineDialog = new DatePickerDialog(
                        getContext(),
                        -1, // -> today
                        R.string.no_end_date
                );
                deadlineDialog.setCallback(new DatePickerDialog.DateCallback() {
                    @Override
                    public void setDate(long d) {
                        recurrence.setEndDate(d);
                        endDateTextView.setText(Helper.getDate(getContext(), recurrence.getEndDate()));
                        TextView pastEndDateWarning = findViewById(R.id.tv_recurrence_end_date_past_warning);
                        if (recurrence.getEndDate() <= Helper.getCurrentTimestamp())
                            pastEndDateWarning.setVisibility(View.VISIBLE);
                        else pastEndDateWarning.setVisibility(View.GONE);
                    }

                    @Override
                    public void removeDate() {
                        recurrence.setEndDate(Long.MAX_VALUE);
                        endDateTextView.setText(getContext().getResources().getString(R.string.never));
                    }
                });
                deadlineDialog.show();
            }
        });
    }

    //  "no recurrence" button
    private void initCancelButton() {
        Button cancelButton = findViewById(R.id.bt_recurrence_no_recurrence);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.removeRecurrence();
                dismiss();
            }
        });
    }

    //  okay button
    private Button initOkayButton() {
        Button okayButton = findViewById(R.id.bt_recurrence_ok);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.setRecurrence(recurrence);
                dismiss();
            }
        });
        return okayButton;
    }

    // Functional methods
    public void setCallback(RecurrenceCallback callback) {
        if (recurrence.getEndDate() <= Helper.getCurrentTimestamp())
            recurrence.setType(Recurrence.Type.NONE);
        this.callback = callback;
    }

    private void adjustIncrementAndSelectionToType() {
        switch (recurrence.getType()) {
            case NONE:
            case DAILY:
            case WEEKLY:
            case MONTHLY:
            case YEARLY:
                incrementEditText.setVisibility(View.VISIBLE);
                selectionGridView.setVisibility(View.GONE);
                break;
            case WEEKDAYS:
                incrementEditText.setVisibility(View.GONE);

                selectionGridView.setAdapter(new ArrayAdapter<RecurrenceSelectionOption>(
                        getContext(),
                        R.layout.recurrence_selection_toggle_button,
                        RecurrenceSelectionOption.Weekday.values()
                ));
                selectionGridView.setVisibility(View.VISIBLE);
                break;
            case SOME_MONTHS:
                incrementEditText.setVisibility(View.GONE);

                selectionGridView.setAdapter(new ArrayAdapter<RecurrenceSelectionOption>(
                        getContext(),
                        R.layout.recurrence_selection_toggle_button,
                        RecurrenceSelectionOption.Month.values()
                ));
                selectionGridView.setVisibility(View.VISIBLE);
                break;
        }

    }
}
