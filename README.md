<!--TODO: Simplify and link to docs once released -->
[ ![Download](https://api.bintray.com/packages/algolia/maven/com.algolia.instantsearch-android%3Ainsights/images/download.svg) ](https://bintray.com/algolia/maven/com.algolia.instantsearch-android%3Ainsights/_latestVersion)

# Algolia InstantSearch Insights for Android

**InstantSearch Insights Android** library allows developers to report click and conversion metrics related to search queries. It does so by correlating events with queryIDs generated by the search API when a query parameter clickAnalytics=true is set.

Once a search has been initialized and the queryID received, the library currently supports two types of events - click and conversion.

# Getting started

## Supported platforms

**InstantSearch Insights Android** is supported on Android devices starting from SDK 14 (Ice Cream Sandwich) and is usable from both **Kotlin** and **Java** code.

# Install

Add `jCenter` to your repositories in `build.gradle`

```
allprojects {
    repositories {
        // [...]
        jcenter()
    }
}
```

Add the following dependency to your `Gradle` file

```gradle
dependencies {
    // [...]
    implementation 'com.algolia.instantsearch-android:insights:2.0.0'
    // [...]
}
```

- The `beta` branch rely on the [evernote](https://github.com/evernote/android-job) library, which is stable.
- The `alpha` branch rely on the [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager/) library, which is itself still in alpha and is known to have issues.

**Please do not use the alpha branch in production**

## Quick Start

### Initialize the Insights client

You first need to initialize the Insights client. For that you need your **Application ID**, **API Key** and the **index name**.
You can find them on [your Algolia account](https://www.algolia.com/api-keys).

**Kotlin**
```kotlin
val configuration = Insights.Configuration(
    connectTimeoutInMilliseconds= 5000,
    readTimeoutInMilliseconds = 5000
)
Insights.register(context, "testApp", "testKey", "indexName", configuration)
```

**Java**
```java
Insights.Configuration configuration = new Insights.Configuration(5000, 5000);
Insights.register(context,  "testApp", "testKey",  "indexName", configuration);
```

### Sending metrics

Once that you registered your **index** with the **Application ID** and the **API Key** you can easily start sending metrics


#### View events

**Kotlin**
```kotlin
Insights.shared("indexName").viewed("eventName", EventObjects.IDs("objectID1", "objectID2"))
Insights.shared("indexName").viewed("eventName", EventObjects.Filters("foo:bar", "foo:baz"))
```

**Java**
```java
Insights.shared("indexName").viewed("eventName", new EventObjects.IDs("objectID1", "objectID2"));
Insights.shared("indexName").viewed("eventName", new EventObjects.Filters("foo:bar", "foo:baz"));

```

#### Click events

**Kotlin**
```kotlin
Insights.shared?.clicked("eventName", EventObjects.IDs("objectID1", "objectID2"))
Insights.shared?.clicked("eventName", EventObjects.Filters("foo:bar", "foo:baz"))
Insights.shared?.clickedAfterSearch("eventName", "queryID", EventObjects.IDs("objectID1", "objectID2"), listOf(0, 3))
```

**Java**
```java
Insights.shared().clicked("eventName", new EventObjects.IDs("objectID1", "objectID2"));
Insights.shared().clicked("eventName", new EventObjects.Filters("foo:bar", "foo:baz"));
Insights.shared().clickedAfterSearch("eventName", "queryID", new EventObjects.IDs("objectID1", "objectID2"), Arrays.asList(0, 3));
```

#### Conversion events

**Kotlin**
```kotlin
Insights.shared?.converted("eventName", EventObjects.IDs("objectID1", "objectID2"))
Insights.shared?.converted("eventName", EventObjects.Filters("foo:bar", "foo:baz"))
Insights.shared?.convertedAfterSearch("eventName", "queryID", EventObjects.IDs("objectID1", "objectID2"))
```

**Java**
```java
Insights.shared().converted("eventName", new EventObjects.IDs("objectID1", "objectID2"));
Insights.shared().converted("eventName", new EventObjects.Filters("foo:bar", "foo:baz"));
Insights.shared().convertedAfterSearch("eventName", "queryID", new EventObjects.IDs("objectID1", "objectID2"));
```

### Event Batching
By default, events are only sent by batches of 10. You can customize this setting with `minBatchSize`:

**Kotlin**
```kotlin
Insights.shared?.minBatchSize = 1 // Sends each event as soon as it is tracked
```

**Java**
```java
Insights.shared().setMinBatchSize(1); // Sends each event as soon as it is tracked
```

### User tracking
Any event should have an `userToken` field to specify the user it relates to. You can set it in three ways:
- Globally for all events
- Per application, for every event tracked by this app
- Individually on an event


**Kotlin**
```kotlin
// Global userToken default value
val configuration = Insights.Configuration(
    connectTimeoutInMilliseconds= 5000,
    readTimeoutInMilliseconds = 5000,
    defaultUserToken = "foo"
)
Insights.register("testApp", "testKey", "indexName", configuration)

// Application userToken, overrides global default
Insights.shared?.userToken = "bar"

// Event usertoken, overrides previous defaults
Insights.shared?.clicked(Event.Click("eventName", "userToken", System.currentTimeMillis(), "queryId", Arrays.asList("objectID1", "objectID2")))
```

**Java**
```java
// Global userToken default value
Insights.Configuration configuration = new Insights.Configuration(5000, 5000, "foo");
Insights.register(context,  "testApp", "testKey",  "indexName", configuration);

// Application userToken, overrides global default
Insights.shared().setUserToken("bar");

// Event userToken, overrides previous defaults
Insights.shared().clicked(new Event.Click("eventName", "userToken", System.currentTimeMillis(), "queryId", Arrays.asList("objectID1", "objectID2")));
```

### User opt-out
You should allow users to opt-out of tracking anytime they want to. When they request opt-out, you can honor it using `enabled`:

**Kotlin**
```kotlin
Insights.shared?.enabled = false
```

**Java**
```java
Insights.shared().setEnabled(false);
```


### Logging and debuging

In case you want to check if the metric was sent correctly, you need to enable the logging first

```kotlin
Insights.shared?.loggingEnabled = true
```

```java
Insights.shared().setLoggingEnabled(true)
```

After you enabled it, you can check the output for success messages or errors

```
// Success
D/Algolia Insights - indexName Sync succeeded for Click(params: {"position": 2, "queryID": 74e382ecaf889f9f2a3df0d4a9742dfb,"objectID": 85725102})

// Error
D/Algolia Insights - indexName The objectID field is missing (Code: 422)
```

To get a more meaningful search experience, please follow our [Getting Started Guide](https://community.algolia.com/instantsearch-android/getting-started.html).

## Upload mechanism

Events are stored in a `SharedPreference` database. Each time an event is successfuly uploaded, it is removed from the database.
An event can be updated using two distinct mechanisms:

### Single upload attempt

Every time a new event is stored in the database, the following check is performed:

If the number of events is superior or equal to `minBatchSize`, an attempt at uploading all events is performed.
If the attempt fails, because of a network issue for example, it will not be retried, and events remain stored in the database.
If you set `minBatchSize` to `1`, an upload attempt will be performed each time a new event is generated.

### Periodic upload

The Insights library relies on a third party library [android-job](https://github.com/evernote/android-job/).
Every 15 minutes, a job will run in the background, whether the application is launched in the foreground or not.
Each time a job runs, it will attempt to upload all events stored in the database. If it fails, because of a network issue for example, it will be retried later.
The 15 minutes delay between each attempt is enforced by the Android system. It does not allow for a shorter delay, for battery saving reason.
You can however configure a longer delay, by setting an higher value than `15` to the variable `debouncingIntervalInMinutes`. Any value below 15 will be ignored, and the 15 minutes delay will remain.

## Getting Help

- **Need help**? Ask a question to the [Algolia Community](https://discourse.algolia.com/) or on [Stack Overflow](http://stackoverflow.com/questions/tagged/algolia).
- **Found a bug?** You can open a [GitHub issue](https://github.com/algolia/instantsearch-android-insights/issues/new).
- **Questions about Algolia?** You can search our [FAQ in our website](https://www.algolia.com/doc/faq/).


## Getting involved

* If you **want to contribute** please feel free to **[submit pull requests](https://github.com/algolia/instantsearch-android-insights/pull/new)**.
* If you **have a feature request** please **open an issue**.
* If you use **InstantSearch** in your app, we would love to hear about it! Drop us a line on [discourse](https://discourse.algolia.com/new-topic?title=InstantSearch%20Mobile%20App:&category_id=10&body=I%27m%20using%20InstantSearch%20Insights%20for...) or [twitter](https://twitter.com/algolia).

# License

InstantSearch Android Insights is [MIT licensed](LICENSE.md).
