package com.zimbra.cs.im.interop.yahoo;

public interface YahooSession {

    public void setMyStatus(YMSGStatus status);

    public void disconnect();

    public void sendMessage(String dest, String message);

    public Iterable<YahooBuddy> buddies();

    public Iterable<YahooGroup> groups();
    
    public void addBuddy(String id, String group);
    
    public void removeBuddy(String id, String group);
    
    public YahooBuddy getBuddy(String id);
    
    public YahooGroup getGroup(String id);
}