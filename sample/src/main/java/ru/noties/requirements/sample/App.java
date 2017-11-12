package ru.noties.requirements.sample;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // see: https://github.com/square/leakcanary/issues/393
        // android bug that leaks connectivityManager if obtained through Activity appContext
        getSystemService(CONNECTIVITY_SERVICE);
    }
}
