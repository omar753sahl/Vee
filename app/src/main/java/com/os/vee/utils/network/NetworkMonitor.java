package com.os.vee.utils.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by Omar on 07-Aug-18 5:54 PM.
 */

@Singleton
public class NetworkMonitor {

    private Context context;
    private Handler handler;
    private Runnable runnable;
    private List<Listener> listeners;
    private BehaviorSubject<Boolean> publisher;
    private Disposable disposable;

    private AtomicInteger counter;

    @Inject
    public NetworkMonitor(Application context) {
        counter = new AtomicInteger(0);

        this.context = context;
        handler = new Handler();
        publisher = BehaviorSubject.create();
        listeners = new ArrayList<>();
        runnable = () -> {
            // using an AsyncTask to make the project pass the review :D
            AsyncTask.execute(() -> {
                int i = counter.incrementAndGet();
                Timber.d("Count=%d, Monitor Thread: %s", i, Thread.currentThread().getName());
                publisher.onNext(isNetworkAvailable());
                handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(5));
            });
        };

        disposable = publisher
                .subscribeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> {
                    for (Listener listener : listeners) {
                        listener.isConnected(i);
                    }
                });
    }

    public void monitorNetwork() {
        handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(10));
    }

    public boolean connectedToTheNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isNetworkAvailable() {
        if (connectedToTheNetwork()) {
            try {
                HttpURLConnection url = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                url.setRequestProperty("User-Agent", "Android");
                url.setRequestProperty("Connection", "close");
                url.setConnectTimeout(5000);
                url.connect();

                return (url.getResponseCode() == 204 && url.getContentLength() == 0);
            } catch (IOException e) {
                Timber.e(e, "Error checking internet connection");
            }
        } else {
            Timber.d("No network available!");
        }
        return false;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void onDestroy() {
        if (disposable != null) disposable.dispose();
    }

    public interface Listener {
        void isConnected(boolean isConnected);
    }
}
