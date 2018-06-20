package com.algolia.insights;

import android.app.Application;

import com.algolia.instantsearch.insights.InstantSearchInsights;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;

public class App extends Application {

    static String appId = "latency";
    static String apiKey = "afc3dd66dd1293e2e2736a5a51b05c0a";
    static String indexName = "bestbuy";

    InstantSearchInsights insights;
    Client client;
    Index index;

    @Override
    public void onCreate() {
        super.onCreate();
        InstantSearchInsights.Configuration configuration = new InstantSearchInsights.Configuration(5000, 5000);

        insights = InstantSearchInsights.register(this, appId, apiKey, indexName, configuration);
        insights.setLoggingEnabled(true);

        client = new Client(appId, apiKey);
        index = client.getIndex(indexName);
    }
}
