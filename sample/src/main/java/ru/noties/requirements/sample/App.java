package ru.noties.requirements.sample;

import android.app.Application;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;

public class App extends Application {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // see: https://github.com/square/leakcanary/issues/393
        // android bug that leaks connectivityManager if obtained through Activity appContext
        getSystemService(CONNECTIVITY_SERVICE);
    }
}
