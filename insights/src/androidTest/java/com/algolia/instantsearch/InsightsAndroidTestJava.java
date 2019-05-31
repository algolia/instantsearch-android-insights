package com.algolia.instantsearch;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.algolia.instantsearch.insights.Insights;
import com.algolia.instantsearch.insights.InsightsException;
import com.algolia.instantsearch.insights.event.Event;
import com.algolia.instantsearch.insights.event.EventObjects;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(AndroidJUnit4.class)
public class InsightsAndroidTestJava {

    private Context context = ApplicationProvider.getApplicationContext();
    private Insights.Configuration configuration = new Insights.Configuration(5000, 5000);

    @Test
    public void testInitShouldFail() {
        try {
            Insights.shared("index");
        } catch (Exception exception) {
            assertEquals(exception.getClass(), InsightsException.IndexNotRegistered.class);
        }
    }

    @Test
    public void testInitShouldWork() {
        Insights insights = Insights.register(context, "testApp", "testKey", "index", configuration);
        Insights insightsShared = Insights.shared();
        assertNotNull("shared Insights should have been registered", insightsShared);
        Event.Click click = new Event.Click(
                "",
                "",
                0,
                new EventObjects.IDs(),
                "",
                new ArrayList<Integer>()
        );
        assertEquals(insights, insightsShared);
        insightsShared.track(click);
    }
}
