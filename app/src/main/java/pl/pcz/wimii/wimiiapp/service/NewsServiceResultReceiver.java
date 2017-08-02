package pl.pcz.wimii.wimiiapp.service;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

public class NewsServiceResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public NewsServiceResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);

    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}
