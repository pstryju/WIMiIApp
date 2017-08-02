package pl.pcz.wimii.wimiiapp.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import pl.pcz.wimii.wimiiapp.Utils.Utils;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;


public class NewsService extends IntentService {

    private final String LOG_TAG = "NewsService";
    public static final String ADDRESS_QUERY_EXTRA = "adr";

    private class PostData {
        String guid;
        String postTitle;
        String postDesc;
        String postLink;
        String postDate;
    }

    private enum RSSXMLTag {
        TITLE, DATE, LINK, DESC, GUID, IGNORETAG
    }


    private RSSXMLTag currentTag;
    public NewsService() {
        super("WIMiI App");
    }

    private void saveUpdateTime() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pl", "PL"));
        String DateToStr = format.format(curDate);
        Log.i(LOG_TAG, "Update Time: " + DateToStr);
        SharedPreferences sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("updatetime", DateToStr);
        editor.apply();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver rec = intent.getParcelableExtra("receiverTag");
        Bundle b = new Bundle();
        if(!Utils.isOnline(this)) {
            b.putSerializable("result", Utils.FetchRssResponse.OFFLINE);
        }
        else {
            String urlStr = intent.getStringExtra(ADDRESS_QUERY_EXTRA);
            InputStream is;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK)
                    saveUpdateTime();
                Log.i(LOG_TAG, "HTTP response is: " + response);
                is = connection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType();
                PostData rssItem = null;

                Vector<ContentValues> cVVector = new Vector<>();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        //Placeholder for action on document start
                        Log.i(LOG_TAG, "XML document start");
                    } else if (eventType == XmlPullParser.START_TAG) { //<item> -> Detect current tag in single item
                        if (xpp.getName().equals("item")) {
                            rssItem = new PostData();
                            currentTag = RSSXMLTag.IGNORETAG;
                        } else if (xpp.getName().equals("guid")) {
                            currentTag = RSSXMLTag.GUID;
                        } else if (xpp.getName().equals("title")) {
                            currentTag = RSSXMLTag.TITLE;
                        } else if (xpp.getName().equals("link")) {
                            currentTag = RSSXMLTag.LINK;
                        } else if (xpp.getName().equals("description")) {
                            currentTag = RSSXMLTag.DESC;
                        } else if (xpp.getName().equals("pubDate")) {
                            currentTag = RSSXMLTag.DATE;
                        }

                    } else if (eventType == XmlPullParser.TEXT) { //Take data from current tag
                        String content = xpp.getText().trim();
                        if (rssItem != null && content.length() != 0) {
                            switch (currentTag) {
                                case GUID:
                                    rssItem.guid = content;
                                    break;
                                case TITLE:
                                    rssItem.postTitle = content;
                                    break;
                                case DESC:
                                    rssItem.postDesc = content;
                                    break;
                                case LINK:
                                    rssItem.postLink = content;
                                    break;
                                case DATE:
                                    rssItem.postDate = content;
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG) { //</item> -> Format data and save it to vector of ContentValues
                        if (xpp.getName().equals("item")) {
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                                Date postDate = dateFormat.parse(rssItem.postDate);
                                dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
                                rssItem.postDate = dateFormat.format(postDate);
                            } catch (java.text.ParseException e) {
                                Log.e(LOG_TAG, e.getMessage());
                            }
                            ContentValues rssValues = new ContentValues();
                            rssValues.put(ReaderContract.NewsEntry.COLUMN_NAME_GUID, rssItem.guid);
                            rssValues.put(ReaderContract.NewsEntry.COLUMN_NAME_TITLE, rssItem.postTitle);
                            rssValues.put(ReaderContract.NewsEntry.COLUMN_NAME_DESC, rssItem.postDesc);
                            rssValues.put(ReaderContract.NewsEntry.COLUMN_NAME_DATE, rssItem.postDate);
                            rssValues.put(ReaderContract.NewsEntry.COLUMN_NAME_LINK, rssItem.postLink);
                            cVVector.add(rssValues);
                        } else {
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                    }
                    eventType = xpp.next();
                }
                int inserted = 0;
                if (cVVector.size() > 0) { //Insert collected data to db
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = getContentResolver().bulkInsert(ReaderContract.NewsEntry.CONTENT_URI, cvArray);
                }

                Log.i(LOG_TAG, "Inserted into db " + inserted);
                b.putSerializable("result", Utils.FetchRssResponse.UPDATETIME);
            } catch (IOException | XmlPullParserException | ParseException e) { 
                e.printStackTrace();
                b.putSerializable("result", Utils.FetchRssResponse.REFRESHERROR);
            }
        }
        if(rec != null)
            rec.send(0, b);
    }


    static public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, NewsService.class);
            sendIntent.putExtra(ADDRESS_QUERY_EXTRA, intent.getStringExtra(ADDRESS_QUERY_EXTRA));
            context.startService(sendIntent);
        }
    }


}
