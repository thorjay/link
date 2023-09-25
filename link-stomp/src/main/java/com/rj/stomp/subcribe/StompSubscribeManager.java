package com.rj.stomp.subcribe;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StompSubscribeManager {

    /**{destination : 订阅集合}*/
    private static final ConcurrentMap<String, Set<StompSubscription>> destinationMap = new ConcurrentHashMap<>();

    public static Map<String, Set<StompSubscription>> getDestinationMap(){
        return destinationMap;
    }

    public static Set<StompSubscription> getSubscriptions(String destination){
        return destinationMap.get(destination);
    }

    public static Set<StompSubscription> addSubscription(String destination, Set<StompSubscription> subscription){
        return destinationMap.putIfAbsent(destination,subscription);
    }

}
