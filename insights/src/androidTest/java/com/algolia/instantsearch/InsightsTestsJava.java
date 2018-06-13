package com.algolia.instantsearch;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.algolia.instantsearch.insights.InstantSearchInsights;
import com.algolia.instantsearch.insights.InstantSearchInsightsException;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class InsightsTestsJava {

    private Context context = InstrumentationRegistry.getContext();
    private InstantSearchInsights.Configuration configuration = new InstantSearchInsights.Configuration(5000, 5000);

    @Test
    public void testInitShouldFail() {
        try {
            InstantSearchInsights.shared("index");
        } catch (Exception exception) {
            assertEquals(exception.getClass(), InstantSearchInsightsException.CredentialsNotFound.class);
        }
    }

    @Test
    public void testInitShouldWork() {
        InstantSearchInsights insights = InstantSearchInsights.register(context, "testApp", "testKey", "index", configuration);
        InstantSearchInsights insightsShared = InstantSearchInsights.shared("index");
        Map<String, ?> map = Collections.emptyMap();

        assertEquals(insights, insightsShared);
        insightsShared.click(map);
    }
}
