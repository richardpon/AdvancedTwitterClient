package com.codepath.apps.simpletwitterclient.networking;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "pfFJ21lB4DP7VQa9h0xsH8Oms";       // Change this
	public static final String REST_CONSUMER_SECRET = "GlqHMgulVGVdOR4sRf3YBlagNRTXsMTKIf9bgdL3sf1xrhm4cU"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletwitterclient"; // Change this (here and in manifest)

    private static final String TAG = "TwitterClient";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    //Method --> endpoint

    // HomeTimeLine - Get us the home timeline
/*
- Get the home timeline for the user
url: GET statuses/home_timeline.json
	count = 25
	since_id=1
 */
    public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");

        //Specify Params
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);
        params.put("max_id", maxId);

        // Execute
        getClient().get(apiUrl, params, handler);
    }


    // Get Users profile
    public void getUserProfile(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");

        //Specify Params
        RequestParams params = new RequestParams();

        // Execute
        getClient().get(apiUrl, params, handler);
    }

    public void sendTweet(String text, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");

        //Specify Params
        RequestParams params = new RequestParams();
        params.put("status", text);

        // Execute
        getClient().post(apiUrl, params, handler);
    }

    public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("statuses/mentions_timeline.json");

        //Specify Params
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("max_id", maxId);

        // Execute
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(long maxId, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        //Specify Params
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        params.put("max_id", maxId);
        // Execute
        getClient().get(apiUrl, params, handler);
    }

}
