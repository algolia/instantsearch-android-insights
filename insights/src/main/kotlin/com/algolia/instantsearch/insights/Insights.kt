package com.algolia.instantsearch.insights

import android.content.Context
import com.algolia.instantsearch.insights.database.Database
import com.algolia.instantsearch.insights.database.DatabaseSharedPreferences
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventUploader
import com.algolia.instantsearch.insights.event.EventUploaderAndroidJob
import com.algolia.instantsearch.insights.webservice.WebService
import com.algolia.instantsearch.insights.webservice.WebServiceHttp

/**
 * Main class used for interacting with the InstantSearch Insights library.
 * In order to send insights, you first need to register an APP ID and API key for a given Index.
 * Once registered, you can simply call `Insights.shared(index:  String)` to send your events.
 * Example:
 * ```
 *   val indexName = "myAwesomeIndex"
 *   Insights.register(
 *      context = context,
 *      appId = "APPID",
 *      apiKey = "APIKEY",
 *      indexName = indexName
 *   )
 *   val clickData = mapOf(
 *      "eventName": "My super event",
 *      "queryID": "6de2f7eaa537fa93d8f8f05b927953b1",
 *      "position": 1,
 *      "objectID": "54675051",
 *      "indexName": indexName,
 *      "timestamp": Date.timeIntervalBetween1970AndReferenceDate
 *   )
 *   Insights.shared(indexName = indexName).click(params = data)
 * ```
 */
class Insights internal constructor(
    private val indexName: String,
    private val eventUploader: EventUploader,
    internal val database: Database,
    internal val webService: WebService
) {

    /**
     * Insights configuration.
     * @param connectTimeoutInMilliseconds Maximum amount of time in milliseconds before a connect timeout.
     * @param readTimeoutInMilliseconds Maximum amount of time in milliseconds before a read timeout.
     */
    class Configuration(
        val connectTimeoutInMilliseconds: Int,
        val readTimeoutInMilliseconds: Int
    )

    inner class Search internal constructor() {
        fun click(event: Event.Click) = this@Insights.track(event)
        fun click(
            eventName: String,
            indexName: String,
            timestamp: Long,
            queryId: String? = null,
            objectIDs: List<String>? = null
        ) = click(Event.Click(eventName, indexName, userTokenOrThrow(), timestamp, queryId, objectIDs))
    }

    inner class Personalization internal constructor() {
        fun view(event: Event.View) = this@Insights.track(event)
        fun view(
            eventName: String,
            indexName: String,
            timestamp: Long,
            queryId: String? = null,
            objectIDs: List<String>? = null,
            positions: List<Int>? = null
        ) = view(Event.View(eventName, indexName, userTokenOrThrow(), timestamp, queryId, objectIDs, positions))

        fun conversion(event: Event.Conversion) = this@Insights.track(event)
        fun conversion(
            eventName: String,
            indexName: String,
            timestamp: Long,
            queryId: String? = null,
            objectIDs: List<String>? = null
        ) = conversion(Event.Conversion(eventName, indexName, userTokenOrThrow(), timestamp, queryId, objectIDs))

        fun click(event: Event.Click) = this@Insights.track(event)
        fun click(
            eventName: String,
            indexName: String,
            timestamp: Long,
            queryId: String? = null,
            objectIDs: List<String>? = null
        ) = click(Event.Click(eventName, indexName, userTokenOrThrow(), timestamp, queryId, objectIDs))
    }

    /**
     * You can use **search** events for _A/B Testing_ or general _Click Analytics_.
     *
     * - A/B Testing allows you to create 2 alternative indices, A and B, each with their own settings,
     * and to put them both live, to see which one performs best.
     * Capture the same user events for both A and B.
     * Measure these captured events against each other, creating scores.
     * Use these scores to determine whether A or B is a better user experience.
     * Adjust your main index accordingly.

     * - Click Analytics helps you answer the following questions:
     * Does a user, after performing a search, click-through to one or more of your products?
     * Does he or she take a particularly significant action, called a “conversion point”?
     */
    val search: Insights.Search = Search()

    /**
     * You can use **personalization** events to leverage personalized search.
     *
     * Personalization feature introduces user-based relevance,
     * an additional layer on top of Algolia’s relevance strategy
     * by injecting user preferences into the relevance formula.
     * Personalization relies on the event capturing mechanism, which allows you
     * to track events that will eventually form the basis of every profile.
     */
    val personalization: Insights.Personalization = Personalization()

    /**
     * Change this variable to `true` or `false` to enable or disable logging.
     * Use a filter on tag `Algolia Insights` to see all logs generated by the Insights library.
     */
    @Suppress("unused") // setter does side-effect
    var loggingEnabled: Boolean = false
        set(value) {
            field = value
            InsightsLogger.enabled[indexName] = value
        }

    /**
     * Change this variable to `true` or `false` to disable Insights, opting-out the current session from tracking.
     */
    var enabled: Boolean = true

    /**
     * Change this variable to change the default debouncing interval. Values lower than 15 minutes will be ignored.
     */
    var debouncingIntervalInMinutes: Long? = null
        set(value) {
            value?.let { eventUploader.setInterval(value) }
        }

    /**
     * Set a user identifier that will override any event's.
     *
     * Depending if the user is logged-in or not, several strategies can be used from a sessionId to a technical identifier.
     * You should always send pseudonymous or anonymous userTokens.
     */
    var userToken: String? = null

    private fun userTokenOrThrow(): String = userToken ?: throw InsightsException.NoUserToken()

    /**
     * Change this variable to change the default amount of event sent at once.
     */
    var minBatchSize: Int = 10

    init {
        eventUploader.startPeriodicUpload()
    }

    /**
     * Method for tracking an event.
     * For a complete description of events see our [documentation][https://www.algolia.com/doc/rest-api/insights/?language=android#push-events].
     * @param [event] An [Event] that you want to track.
     */
    fun track(event: Event) {
        if (enabled) {
            database.append(event)
            if (database.count() >= minBatchSize) {
                eventUploader.startOneTimeUpload()
            }
        }
    }

    override fun toString(): String {
        return "Insights(indexName='$indexName', webService=$webService)"
    }

    companion object {

        internal val insightsMap = mutableMapOf<String, Insights>()

        /**
         * Register your index with a given appId and apiKey.
         * @param context A [Context].
         * @param appId The given app id for which you want to track the events.
         * @param apiKey The API Key for your `appId`.
         * @param indexName The index that is being tracked.
         * @param configuration A [Configuration] class.
         * @return An [Insights] instance.
         */
        @JvmStatic
        fun register(
            context: Context,
            appId: String,
            apiKey: String,
            indexName: String,
            configuration: Configuration = Configuration(5000, 5000)
        ): Insights {
            val eventUploader = EventUploaderAndroidJob(context)
            val database = DatabaseSharedPreferences(context, indexName)
            val webService = WebServiceHttp(
                appId = appId,
                apiKey = apiKey,
                environment = WebServiceHttp.Environment.Prod,
                connectTimeoutInMilliseconds = configuration.connectTimeoutInMilliseconds,
                readTimeoutInMilliseconds = configuration.readTimeoutInMilliseconds
            )
            val insights = Insights(indexName, eventUploader, database, webService)

            val previousInsights = insightsMap.put(indexName, insights)
            previousInsights?.let {
                System.out.println("Registering new Insights for indexName $indexName. Previous instance: $insights")
            }
            return insights
        }

        /**
         * Access an already registered `Insights` without having to pass the `apiKey` and `appId`.
         *
         * If the index was not register before, it will throw an [InsightsException.IndexNotRegistered] exception.
         * @param indexName The index that is being tracked.
         * @return An [Insights] instance.
         * @throws InsightsException.IndexNotRegistered if no index was registered as [indexName] before.
         */
        @JvmStatic
        fun shared(indexName: String): Insights {
            return insightsMap[indexName]
                ?: throw InsightsException.IndexNotRegistered()
        }
    }
}
