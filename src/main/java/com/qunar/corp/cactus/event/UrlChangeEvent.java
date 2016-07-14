package com.qunar.corp.cactus.event;

import com.qunar.corp.cactus.bean.GovernanceData;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-23
 * Time: 下午2:53
 */
public class UrlChangeEvent {

    public final int uid;

    public final EventType type;

    public final GovernanceData data;

    private UrlChangeEvent(int uid, EventType type, GovernanceData data) {
        this.uid = uid;
        this.type = type;
        this.data = data;
    }

    public static UrlChangeEvent make(int uid, EventType type, GovernanceData data) {
        return new UrlChangeEvent(uid, type, data.copy());
    }

    public int getUid() {
        return uid;
    }

    public EventType getType() {
        return type;
    }

    public GovernanceData getData() {
        return data;
    }
}
