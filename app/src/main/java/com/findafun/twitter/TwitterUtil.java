package com.findafun.twitter;

import android.widget.Toast;

import com.findafun.utils.FindAFunConstants;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by bxdc46 on 12/23/2015.
 */
public final class TwitterUtil {

    private  RequestToken requestToken = null;
    private TwitterFactory twitterFactory = null;
    private Twitter twitter;

    private TwitterUtil() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(FindAFunConstants.TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(FindAFunConstants.TWITTER_CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitterFactory = new TwitterFactory(configuration);
        twitter = twitterFactory.getInstance();
    }

    public TwitterFactory getTwitterFactory()
    {
        return twitterFactory;
    }

    public void setTwitterFactory(AccessToken accessToken)
    {
        twitter = twitterFactory.getInstance(accessToken);
    }

    public Twitter getTwitter()
    {
        return twitter;
    }
    public RequestToken getRequestToken() {
        if (requestToken == null) {
            try {
                requestToken = twitterFactory.getInstance().getOAuthRequestToken(FindAFunConstants.TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();
                 //To change body of catch statement use File | Settings | File Templates.

            }
        }
        return requestToken;
    }

    static TwitterUtil instance = new TwitterUtil();

    public static TwitterUtil getInstance() {
        return instance;
    }


    public void reset() {
        instance = new TwitterUtil();
    }

    public void resetTwitterRequestToken(){
        requestToken = null;
    }

}
