package com.qunar.corp.cactus.drainage.bean;

/**
 * @author sen.chai
 * @date 2015-05-10 15:06
 */
public class DrainageResultDesc {

    int errorCount = 0;
    StringBuilder failedMsg = new StringBuilder();
    StringBuilder successMsg = new StringBuilder();


    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public StringBuilder getFailedMsg() {
        return failedMsg;
    }

    public void setFailedMsg(StringBuilder failedMsg) {
        this.failedMsg = failedMsg;
    }

    public StringBuilder getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(StringBuilder successMsg) {
        this.successMsg = successMsg;
    }

    public DrainageResultDesc appendSuccessMsg(CharSequence msg) {
        this.successMsg.append(msg);
        return this;
    }

    public DrainageResultDesc appendFailedMsg(CharSequence msg) {
        this.failedMsg.append(msg);
        return this;
    }

    public DrainageResultDesc incError() {
        this.errorCount++;
        return this;
    }

    public boolean hasError() {
        return this.errorCount > 0;
    }

    public String buildAllDesc() {
        return "errorCount: " + this.errorCount + ", " + this.failedMsg.toString() + ", " + this.successMsg;
    }

}
