package com.dianping.cat.system.page.router.config;

import static java.lang.Integer.*;

public class OperatorServerRange {

    private final int minOid;

    private final int maxOid;

    private final int minSid;

    private final int maxSid;

    public OperatorServerRange(int minOid, int maxOid, int minSid, int maxSid) {
        this.minOid = minOid;
        this.maxOid = maxOid;
        this.minSid = minSid;
        this.maxSid = maxSid;
    }

    public boolean isInRange(String ip) {
        String[] split = ip.split("\\.");
        int oid;
        try {
            oid = parseInt(split[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (oid < minOid || oid > maxOid) {
            return false;
        }
        if (minSid == 0 && maxSid == 0) {
            return true;
        }
        int sid;
        try {
            sid = parseInt(split[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        return minSid <= sid && sid <= maxSid;
    }
}
