package pl.pcz.wimii.wimiiapp.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.pcz.wimii.wimiiapp.R;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;


public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {
    Context context;
    Cursor cursor;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayTextView;
        CardView timetableCardView;
        LinearLayout subjectLayout;
        Button showMoreButton;
        Button showLessButton;

        ViewHolder(View v) {
            super(v);
            dayTextView = (TextView) v.findViewById(R.id.tt_day);
            timetableCardView = (CardView) v.findViewById(R.id.timetable_cv);
            subjectLayout = (LinearLayout) v.findViewById(R.id.tt_ll);
            showMoreButton = (Button) v.findViewById(R.id.btn_more_tt);
            showLessButton = (Button) v.findViewById(R.id.tt_lessButton);
        }
    }

    public TimetableAdapter(Context c) {
        super();
        context = c;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public void swapCursor(Cursor c) {
        cursor = c;
    }

    @Override
    public TimetableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_timetable, parent, false);
        return new TimetableAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TimetableAdapter.ViewHolder holder, int position) {
        if(cursor == null)
            return;
        cursor.moveToPosition(position);
        int numberDayOfWeek = position;
        String dayOfWeek = context.getResources().getStringArray(R.array.days_of_week)[numberDayOfWeek];
        holder.dayTextView.setText(dayOfWeek);

        holder.subjectLayout.removeAllViews();
        cursor.moveToPosition(-1);
        for(int i = 0; cursor.moveToNext();) {
            if (cursor.getInt(cursor.getColumnIndex(ReaderContract.PlanEntry.COLUMN_NAME_DAY)) == position) {
                LinearLayout ll = new LinearLayout(context);
                LayoutInflater.from(context).inflate(R.layout.timetable_single_subject, ll);
                if(i % 2 != 0)
                    ll.setBackgroundColor(context.getResources().getColor(R.color.timetableBg));
                TextView startTextView = (TextView) ll.findViewById(R.id.tt_start);
                TextView subjectTextView = (TextView) ll.findViewById(R.id.tt_subject);
                String startTime = Integer.toString(cursor.getInt(cursor.getColumnIndex(ReaderContract.PlanEntry.COLUMN_NAME_START))) + ".00";
                String endTime = Integer.toString(cursor.getInt(cursor.getColumnIndex(ReaderContract.PlanEntry.COLUMN_NAME_END))) + ".00";
                String subjectTime =  startTime + " - " + endTime;
                startTextView.setText(subjectTime);
                subjectTextView.setText(cursor.getString(cursor.getColumnIndex(ReaderContract.PlanEntry.COLUMN_NAME_SUBJECT)));
                holder.subjectLayout.addView(ll);
                i++;
            }
        }
        holder.timetableCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.subjectLayout.getVisibility() == View.GONE) {
                    holder.subjectLayout.setVisibility(View.VISIBLE);
                    holder.showMoreButton.setVisibility(View.INVISIBLE);
                    holder.showLessButton.setVisibility(View.VISIBLE);
                }
                else {
                    holder.subjectLayout.setVisibility(View.GONE);
                    holder.showMoreButton.setVisibility(View.VISIBLE);
                    holder.showLessButton.setVisibility(View.GONE);
                }
            }
        });
    }
}
