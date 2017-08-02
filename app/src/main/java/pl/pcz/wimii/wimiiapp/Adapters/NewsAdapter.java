package pl.pcz.wimii.wimiiapp.Adapters;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.pcz.wimii.wimiiapp.R;
import pl.pcz.wimii.wimiiapp.Utils.HtmlTagHandler;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Cursor cursor;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView dateTextView;
        TextView descTextView;
        CardView cardView;

        ViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.tv_title_lcn);
            dateTextView = (TextView) v.findViewById(R.id.tv_date_lcn);
            descTextView = (TextView) v.findViewById(R.id.tv_desc_lcn);
            cardView = (CardView) v.findViewById(R.id.lcn_cv);
        }
    }

    public NewsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.titleTextView.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(ReaderContract.NewsEntry.COLUMN_NAME_TITLE))));
        holder.dateTextView.setText(cursor.getString(cursor.getColumnIndex(ReaderContract.NewsEntry.COLUMN_NAME_DATE)));
        holder.descTextView.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(ReaderContract.NewsEntry.COLUMN_NAME_DESC)), null, new HtmlTagHandler()));
        holder.descTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.descTextView.getVisibility() == View.GONE)
                    holder.descTextView.setVisibility(View.VISIBLE);
                else
                    holder.descTextView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(cursor == null) return 0;
        return cursor.getCount();
    }

    public void swapCursor(Cursor c) {
        cursor = c;
        notifyDataSetChanged();
    }
}
