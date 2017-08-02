package pl.pcz.wimii.wimiiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Vector;

import javax.security.auth.Subject;

import pl.pcz.wimii.wimiiapp.Utils.Utils;
import pl.pcz.wimii.wimiiapp.data.DbHelper;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;

public class FetchPlanTask extends AsyncTask<String, Void, Void> {
    Context context;
    private boolean isRunning = true;
    private final int COLUMNS_COUNT = 6;
    private final int DAYS_IN_PLAN = 5;

    public FetchPlanTask(Context c) {
        context = c;
    }

    @Override
    protected void onCancelled() {
        isRunning = false;
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        if(!Utils.isOnline(context)) {
            cancel(true);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        if(isRunning) {
            Vector<Subject> days[] = new Vector[DAYS_IN_PLAN];
            for (int i = 0; i < DAYS_IN_PLAN; i++)
                days[i] = new Vector<>();
            Document doc = null;
            try {
                doc = Jsoup.connect(params[0]).get();
                Elements elements = doc.select("td"); //Take only <td> tags
                int startHour = 8;
                for (int i = COLUMNS_COUNT + 1; i < elements.size(); i++) {
                    if (elements.get(i).text().equals(" ")) //skip empty cell
                        continue;
                    if (i % COLUMNS_COUNT != 0) { //if row not changed pass through columns and add to proper day vector
                        days[(i % COLUMNS_COUNT) - 1].add(new Subject((i % COLUMNS_COUNT) - 1, startHour, elements.get(i).text()));
                    } else {
                        startHour++; //row changed -> next subjects start one hour later
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < DAYS_IN_PLAN; j++) {
                for (int i = days[j].size() - 1; i > 0; i--) { //duplicates merging loop
                    if (days[j].get(i).name.equals(days[j].get(i - 1).name)) {
                        days[j].remove(i);
                        days[j].get(i - 1).ehour++;
                    }
                }
            }

            Vector<Subject> subjects = new Vector<>();
            for (int j = 0; j < DAYS_IN_PLAN; j++) { //merge day-vectors into one vector
                subjects.addAll(days[j]);
                for (int i = 0; i < days[j].size(); i++) //debug loop
                    Log.v(Integer.toString(j), days[j].elementAt(i).shour + ":00 -> " + days[j].elementAt(i).ehour + ":00 " + days[j].elementAt(i).name);
            }

            ContentValues[] plan = new ContentValues[subjects.size()];
            for (int i = 0; i < subjects.size(); i++) { //pack vector to db-friendly ContentValues
                plan[i] = new ContentValues();
                plan[i].put(ReaderContract.PlanEntry.COLUMN_NAME_SUBJECT, subjects.get(i).name);
                plan[i].put(ReaderContract.PlanEntry.COLUMN_NAME_DAY, subjects.get(i).day);
                plan[i].put(ReaderContract.PlanEntry.COLUMN_NAME_START, subjects.get(i).shour);
                plan[i].put(ReaderContract.PlanEntry.COLUMN_NAME_END, subjects.get(i).ehour);
            }

            int deleted = context.getContentResolver().delete(ReaderContract.PlanEntry.CONTENT_URI, null, null); //clear table before inserting
            int inserted = context.getContentResolver().bulkInsert(ReaderContract.PlanEntry.CONTENT_URI, plan);
            Log.d("Deleted in plan table", Integer.toString(deleted));
            Log.d("Items in plan inserted", Integer.toString(inserted));
        }
        return null;
    }

    private class Subject {
        Subject(int d, int h, String n) {
            day = d;
            shour = h;
            ehour = h + 1;
            name = n;
        }
        int day;
        int shour;
        int ehour;
        String name;
    }
}