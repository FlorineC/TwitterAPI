/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import twitter4j.*;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * This is a code example of Twitter4J Streaming API - user stream.<br>
 * Usage: java twitter4j.examples.PrintUserStream. Needs a valid twitter4j.properties file with Basic Auth _and_ OAuth properties set<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @author Rémy Rakic - remy dot rakic at gmail.com
 */
public final class Twitter {

    private static java.nio.file.Path path = Paths.get("twitter-credentials.txt");
    private static TwitterStream twitterStream;
    private static twitter4j.Twitter twitter;

    public static void main(String[] args) throws Exception {
        oAuth();
        oAuthStream();
        StatusListener listener = new UserStreamAdapter() {
            public void onStatus(Status status) {
                System.out.println(status); // logs the tweet author and text
            }
        };
        User user = twitter.showUser("eluyha");
        twitterStream.addListener(listener);
        System.out.println("Listener initiated; listening for status updates.");

        FilterQuery tweetFilterQuery = new FilterQuery();
        tweetFilterQuery.follow(user.getId());
        tweetFilterQuery.filterLevel("medium");
        twitterStream.filter(tweetFilterQuery);
    }



    public static void oAuthStream() throws Exception{

        // The factory instance is re-useable and thread safe.
        twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream.setOAuthConsumer("JzF5t6ZH5nFBA3xEOn3ooujOI", "CE4rbduP7hJA8lTAPjuaP86JtQ0HhzY3NCgtzcSsJwn7rNKQDj");
        RequestToken requestToken = twitterStream.getOAuthRequestToken();
        AccessToken accessToken = null;
        if(Files.exists(path)) {
            List<String> token = Files.readAllLines(path);
            accessToken = new AccessToken(token.get(0), token.get(1));
            twitterStream.setOAuthAccessToken(accessToken);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try{
                if(pin.length() > 0){
                    accessToken = twitterStream.getOAuthAccessToken(requestToken, pin);
                } else{
                    accessToken = twitterStream.getOAuthAccessToken();
                }
                storeAccessToken(accessToken);
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                }else{
                    te.printStackTrace();
                }
            }
        }
    }

    public static void oAuth() throws Exception{

        // The factory instance is re-useable and thread safe.
        twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("JzF5t6ZH5nFBA3xEOn3ooujOI", "CE4rbduP7hJA8lTAPjuaP86JtQ0HhzY3NCgtzcSsJwn7rNKQDj");
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        if(Files.exists(path)) {
            List<String> token = Files.readAllLines(path);
            accessToken = new AccessToken(token.get(0), token.get(1));
            twitter.setOAuthAccessToken(accessToken);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try{
                if(pin.length() > 0){
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else{
                    accessToken = twitter.getOAuthAccessToken();
                }
                storeAccessToken(accessToken);
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                }else{
                    te.printStackTrace();
                }
            }
        }
    }

    private static void storeAccessToken(AccessToken accessToken) throws IOException{
        Files.write(path, Arrays.asList(accessToken.getToken(), accessToken.getTokenSecret()), StandardOpenOption.CREATE);
    }
}