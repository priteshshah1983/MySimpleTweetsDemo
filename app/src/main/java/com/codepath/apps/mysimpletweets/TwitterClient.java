package com.codepath.apps.mysimpletweets;

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

    private static int ITEMS_PER_PAGE = 25;

    public static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "GSgWyeJO5crdazkeDKNjBoybt";
	public static final String REST_CONSUMER_SECRET = "bXiqrwAprJxVoUUPSNK98xrURZ0QKHoDLBjYkCi97L5fwcgc7E";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", ITEMS_PER_PAGE);
        params.put("since_id", 1);
        if (max_id > 0) {
            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", ITEMS_PER_PAGE);
        if (max_id > 0) {
            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", ITEMS_PER_PAGE);
        params.put("screen_name", screenName);
        if (max_id > 0) {
            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getCurrentUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, null, handler);
    }

    public void postTweet(String tweet, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        getClient().post(apiUrl, params, handler);
    }

    public void getFollowers(String screenName, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
//        params.put("count", ITEMS_PER_PAGE);
//        params.put("since_id", 1);
        params.put("screen_name", screenName);
        if (max_id > 0) {
//            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getFriends(String screenName, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
//        params.put("count", ITEMS_PER_PAGE);
//        params.put("since_id", 1);
        params.put("screen_name", screenName);
        if (max_id > 0) {
//            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void favoriteTweet(long tweetId, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        getClient().post(apiUrl, params, handler);
    }

    public void unFavoriteTweet(long tweetId, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        getClient().post(apiUrl, params, handler);
    }

    public void retweet(long tweetId, AsyncHttpResponseHandler handler){
        String id = String.valueOf(tweetId);
        String apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
        getClient().post(apiUrl, null, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}