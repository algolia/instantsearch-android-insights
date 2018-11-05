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
    implementation 'com.algolia.instantsearch-android:insights:1.1-0-beta02'
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

*Click events*

**Kotlin**
```kotlin
val params = mapOf(
    "queryID" to "74e382ecaf889f9f2a3df0d4a9742dfb",
    "objectID" to "85725102",
    "position" to 1
)

Insights.shared(indexName = "indexName").click(params)
```

**Java**
```java
HashMap<String, Object> clickParams = new HashMap<>();

map.put("queryID", "74e382ecaf889f9f2a3df0d4a9742dfb");
map.put("objectID", "85725102");
map.put("position", 1);
Insights.shared("indexName").click(clickParams);
```

*Conversion events*

**Kotlin**
```
val params = mapOf(
    "queryID" to "74e382ecaf889f9f2a3df0d4a9742dfb",
    "objectID" to "85725102"
)

Insights.shared(indexName = "indexName").conversion(params)
```

**Java**
```java
HashMap<String, Object> conversionParams = new HashMap<>();

map.put("queryID", "74e382ecaf889f9f2a3df0d4a9742dfb");
map.put("objectID", "85725102");
Insights.shared("indexName").conversion(conversionParams);
```

### Logging and debuging

In case you want to check if the metric was sent correctly, you need to enable the logging first

```kotlin
Insights.shared(indexName = "indexName").loggingEnabled = true
```

After you enabled it, you can check the output for success messages or errors

```
// Success
D/Algolia Insights - indexName Sync succeded for Click(params: {"position": 2, "queryID": 74e382ecaf889f9f2a3df0d4a9742dfb,"objectID": 85725102})

// Error
D/Algolia Insights - indexName The objectID field is missing (Code: 422)
```

To get a more meaningful search experience, please follow our [Getting Started Guide](https://community.algolia.com/instantsearch-ios/getting-started.html).

## Getting Help

- **Need help**? Ask a question to the [Algolia Community](https://discourse.algolia.com/) or on [Stack Overflow](http://stackoverflow.com/questions/tagged/algolia).
- **Found a bug?** You can open a [GitHub issue](https://github.com/algolia/instantsearch-android-insights/issues/new).
- **Questions about Algolia?** You can search our [FAQ in our website](https://www.algolia.com/doc/faq/).


## Getting involved

* If you **want to contribute** please feel free to **[submit pull requests](https://github.com/algolia/instantsearch-android-insights/pull/new)**.
* If you **have a feature request** please **open an issue**.
* If you use **InstantSearch** in your app, we would love to hear about it! Drop us a line on [discourse](https://discourse.algolia.com/new-topic?title=InstantSearch%20Mobile%20App:&category_id=10&body=I%27m%20using%20InstantSearch%20for...) or [twitter](https://twitter.com/algolia).

# License

InstantSearch Android Insights is [MIT licensed](LICENSE.md).

[react-instantsearch-github]: https://github.com/algolia/react-instantsearch/
[instantsearch-ios-github]: https://github.com/algolia/instantsearch-ios
[instantsearch-js-github]: https://github.com/algolia/instantsearch.js
