package pl.pcz.wimii.wimiiapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import pl.pcz.wimii.wimiiapp.R;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;


public class TimetableChooseFragment extends DialogFragment {

    private Cursor ttListCursor;
    RadioGroup ttListRadioGroup;
    Button okButton;
    SharedPreferences sharedPref;
    int selectedTimetable;

    public interface ChooserDialogListener {
        void onFinishChooseDialog();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        selectedTimetable = sharedPref.getInt("selectedtimetable", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timetable_chooser, container);
        okButton = (Button) v.findViewById(R.id.chooser_ok);
        ttListRadioGroup = (RadioGroup) v.findViewById(R.id.radiogroup);
        getListData();
        createRadioGroup();
        okButton.setOnClickListener(new OkButtonListener());
        ttListRadioGroup.check(selectedTimetable);
        return v;
    }

    void getListData() {
        Uri uri = ReaderContract.PlanListEntry.buildRssUri();
        String[] projection = {
                ReaderContract.PlanListEntry._ID,
                ReaderContract.PlanListEntry.COLUMN_NAME_SUBJECT,
                ReaderContract.PlanListEntry.COLUMN_NAME_LINK,
        };
        ttListCursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
    }

    void createRadioGroup() {
        RadioGroup.LayoutParams ttRadioGroupParams =
                new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for(int i = 0; ttListCursor.moveToNext(); i++){
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setText(ttListCursor.getString(ttListCursor.getColumnIndex(ReaderContract.PlanListEntry.COLUMN_NAME_SUBJECT)));
            radioButton.setId(i);
            ttListRadioGroup.addView(radioButton,  ttRadioGroupParams);
        }
    }

    private class OkButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ttListCursor.moveToPosition(ttListRadioGroup.getCheckedRadioButtonId());
            SharedPreferences sharedPref = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("selectedtimetable", ttListRadioGroup.getCheckedRadioButtonId());
            editor.putString("selectedtimetablename", ttListCursor.getString(ttListCursor.getColumnIndex(ReaderContract.PlanListEntry.COLUMN_NAME_SUBJECT)));
            editor.putString("selectedtimetablelink", ttListCursor.getString(ttListCursor.getColumnIndex(ReaderContract.PlanListEntry.COLUMN_NAME_LINK)));
            editor.apply();
            ChooserDialogListener parentFragment = (ChooserDialogListener) getTargetFragment();
            parentFragment.onFinishChooseDialog();
            dismiss();
        }
    }


}
