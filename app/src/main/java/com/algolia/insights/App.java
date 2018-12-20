package com.algolia.insights;

import android.app.Application;

import com.algolia.instantsearch.insights.Insights;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.facebook.stetho.Stetho;

public class App extends Application {

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

        insights = Insights.register(this, appId, apiKey, indexName, configuration);
        insights.setLoggingEnabled(true);
        insights.setUserToken("userToken");

        client = new Client(appId, apiKey);
        index = client.getIndex(indexName);
        Stetho.initializeWithDefaults(this);
    }
}
