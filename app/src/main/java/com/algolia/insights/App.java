package com.algolia.insights;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.WorkManager;
import com.algolia.instantsearch.insights.Insights;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.facebook.stetho.Stetho;

public class App extends Application implements Configuration.Provider {

    static String appId = "latency";
    static String apiKey = "afc3dd66dd1293e2e2736a5a51b05c0a";
    static String indexName = "bestbuy";

    Insights insights;
    Client client;
    Index index;

    @Override
    public void onCreate() {
        super.onCreate();
        Insights.Configuration configuration = new Insights.Configuration(5000, 5000);

        WorkManager.initialize(this, getWorkManagerConfiguration());
        insights = Insights.register(this, appId, apiKey, indexName, configuration);
        insights.setLoggingEnabled(true);
        insights.setUserToken("userToken");
        insights.setMinBatchSize(1);

        client = new Client(appId, apiKey);
        index = client.getIndex(indexName);
        Stetho.initializeWithDefaults(this);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder().setMinimumLoggingLevel(android.util.Log.INFO).build();
    }
}
