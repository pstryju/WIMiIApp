package pl.pcz.wimii.wimiiapp.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pl.pcz.wimii.wimiiapp.R;
import pl.pcz.wimii.wimiiapp.fragments.LessonsPlansFragment;
import pl.pcz.wimii.wimiiapp.fragments.NewsFragment;


public class PagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    private Context context;

    public PagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return NewsFragment.newInstance();
            case 1:
                return LessonsPlansFragment.newInstance();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        String str = "";
        switch (position){
            case 0:
                str = context.getString(R.string.news_feed);
                break;
            case 1:
                str = context.getString(R.string.lessons_plans);
                break;
        }
        return str;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
