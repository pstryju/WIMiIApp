package pl.pcz.wimii.wimiiapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import pl.pcz.wimii.wimiiapp.Utils.Utils;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;

public class FetchPlansListTask extends AsyncTask<String, Void, Void> {
    Context context;
    private boolean isRunning = true;

    public FetchPlansListTask(Context c) {
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
            try {
                Document doc = Jsoup.connect(params[0]).get();
                Elements elements = doc.select("a");
                ContentValues[] planslist = new ContentValues[elements.size()];
                for (int i = 0; i < elements.size(); i++) {
                    planslist[i] = new ContentValues();
                    planslist[i].put(ReaderContract.PlanListEntry.COLUMN_NAME_SUBJECT, elements.get(i).text());
                    planslist[i].put(ReaderContract.PlanListEntry.COLUMN_NAME_LINK, elements.get(i).attr("href"));
                }
                int deleted = context.getContentResolver().delete(ReaderContract.PlanListEntry.CONTENT_URI, null, null);
                int inserted = context.getContentResolver().bulkInsert(ReaderContract.PlanListEntry.CONTENT_URI, planslist);
                Log.d("Plans deleted", Integer.toString(deleted));
                Log.d("Plans inserted", Integer.toString(inserted));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}