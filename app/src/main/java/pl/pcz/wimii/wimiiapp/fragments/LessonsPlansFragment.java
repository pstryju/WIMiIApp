package pl.pcz.wimii.wimiiapp.fragments;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import pl.pcz.wimii.wimiiapp.Adapters.TimetableAdapter;
import pl.pcz.wimii.wimiiapp.FetchPlanTask;
import pl.pcz.wimii.wimiiapp.FetchPlansListTask;
import pl.pcz.wimii.wimiiapp.R;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;

public class LessonsPlansFragment extends Fragment implements TimetableChooseFragment.ChooserDialogListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final static int TIMETABLE_LOADER = 1;
    SharedPreferences sharedPref;
    RecyclerView timetable;
    Cursor cursor = null;
    TimetableAdapter ttadapter;
    TextView selectedGroupTextView;
    String selectedTimetableName;
    LinearLayoutManager layoutManager;

    public static LessonsPlansFragment newInstance() {
        return new LessonsPlansFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        selectedTimetableName = sharedPref.getString("selectedtimetablename", "");
        parseHTML();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TIMETABLE_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timetable, container, false);
        Button chooseButton = (Button) v.findViewById(R.id.choose);
        selectedGroupTextView = (TextView) v.findViewById(R.id.tt_group);
        selectedGroupTextView.setText(selectedTimetableName);
        timetable = (RecyclerView) v.findViewById(R.id.timetable_rv);
        layoutManager = new LinearLayoutManager(getActivity());
        timetable.setLayoutManager(layoutManager);
        ttadapter = new TimetableAdapter(getActivity());
        timetable.setAdapter(ttadapter);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
        return v;
    }

    void parseHTML() {
        FetchPlansListTask task = new FetchPlansListTask(getActivity());
        task.execute("http://wimii.pcz.czest.pl/download/plan/studia_stacjonarne/plan_dzienne_2016_2017_lato_24_03_2017.html");
    }

    private void showEditDialog() {
        TimetableChooseFragment editNameDialog = new TimetableChooseFragment();
        editNameDialog.setTargetFragment(this, 1);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        editNameDialog.show(ft, "chooser");
    }

    @Override
    public void onFinishChooseDialog() {
        String selectedTimetableLink = sharedPref.getString("selectedtimetablelink", "");
        FetchPlanTask fetchTimetable = new FetchPlanTask(getActivity());
        fetchTimetable.execute("http://wimii.pcz.czest.pl/download/plan/studia_stacjonarne/" + selectedTimetableLink);

        String selectedTimetableName = sharedPref.getString("selectedtimetablename", "");
        selectedGroupTextView.setText(selectedTimetableName);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ReaderContract.PlanEntry.buildRssUri();
        String[] projection = {
                ReaderContract.PlanEntry._ID,
                ReaderContract.PlanEntry.COLUMN_NAME_SUBJECT,
                ReaderContract.PlanEntry.COLUMN_NAME_START,
                ReaderContract.PlanEntry.COLUMN_NAME_END,
                ReaderContract.PlanEntry.COLUMN_NAME_DAY,
        };
        return new CursorLoader(getActivity(), uri, projection, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ttadapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ttadapter.swapCursor(data);
        ttadapter.notifyDataSetChanged();
    }
}
