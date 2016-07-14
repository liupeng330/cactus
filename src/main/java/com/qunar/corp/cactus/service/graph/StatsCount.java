package com.qunar.corp.cactus.service.graph;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-18
 * Time: 下午2:05
 */
public class StatsCount {

    public final int allAppCount;
    public final int allServiceCount;
    public final int allProvider;
    public final int allConsumer;

    public StatsCount(int allAppCount, int allServiceCount, int allProvider, int allConsumer) {
        this.allAppCount = allAppCount;
        this.allServiceCount = allServiceCount;
        this.allProvider = allProvider;
        this.allConsumer = allConsumer;
    }

    public int getAllAppCount() {
        return allAppCount;
    }

    public int getAllServiceCount() {
        return allServiceCount;
    }

    public int getAllProvider() {
        return allProvider;
    }

    public int getAllConsumer() {
        return allConsumer;
    }
}
