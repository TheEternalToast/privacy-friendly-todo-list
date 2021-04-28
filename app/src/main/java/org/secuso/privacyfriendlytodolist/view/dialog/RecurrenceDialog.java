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
import android.widget.CompoundButton;
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

    // Internal Interfaces/Classes
    public interface RecurrenceCallback {
        void setRecurrence(Recurrence recurrence);

        void removeRecurrence();
    }

    private class SelectionGridAdapter extends ArrayAdapter<RecurrenceSelectionOption> {
        public SelectionGridAdapter(@NonNull Context context, int resource, @NonNull RecurrenceSelectionOption[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ToggleButton button;
            if (convertView == null)
                button = (ToggleButton) LayoutInflater.from(getContext())
                        .inflate(R.layout.recurrence_selection_toggle_button, parent, false);
            else
                button = (ToggleButton) convertView;
            final RecurrenceSelectionOption item = getItem(position);
            if (item == null) return button;
            button.setTextOn(item.toString(getContext()));
            button.setTextOff(item.toString(getContext()));
            button.setChecked(item.isSelected());
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) recurrence.addToSelection(item.getValue());
                    else recurrence.removeFromSelection(item.getValue());
                }
            });
            return button;
        }
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
        initOkayButton();
        initStartDate();
        initEndDate();
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
        final RecurrenceSelectionOption[] options = recurrence.getType().getOptionType().getEnumConstants();
        for (RecurrenceSelectionOption option : options)
            option.setSelected(recurrence.isSelected(option.getValue()));

        selectionGridView.setAdapter(new SelectionGridAdapter(
                getContext(),
                R.layout.recurrence_selection_toggle_button,
                options
        ));
    }

    //  initialize start date view
    private void initStartDate() {
        startDateTextView = findViewById(R.id.tv_recurrence_start_date);
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
    private void initEndDate() {
        endDateTextView = findViewById(R.id.tv_recurrence_end_date);
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
    private void initOkayButton() {
        Button okayButton = findViewById(R.id.bt_recurrence_ok);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.setRecurrence(recurrence);
                dismiss();
            }
        });
    }

    // Functional methods
    public void setCallback(RecurrenceCallback callback) {
        this.callback = callback;
    }

    private void adjustIncrementAndSelectionToType() {
        switch (recurrence.getType()) {
            case NONE:
            case DAILY:
            case WEEKLY:
            case MONTHLY:
            case YEARLY:
                selectionGridView.setVisibility(View.GONE);
                break;
            case WEEKDAYS:
            case SOME_MONTHS:
                selectionGridView.setAdapter(new SelectionGridAdapter(
                        getContext(),
                        R.layout.recurrence_selection_toggle_button,
                        recurrence.getType().getOptionType().getEnumConstants()
                ));
                switch (recurrence.getType()) {
                    case WEEKDAYS:
                        selectionGridView.setNumColumns(7);
                        break;
                    case SOME_MONTHS:
                        selectionGridView.setNumColumns(6);
                        break;
                }
                selectionGridView.setVisibility(View.VISIBLE);
                break;
        }

    }

}
