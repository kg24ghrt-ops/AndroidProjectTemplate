package com.riadul.mvvm.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExampleResponse {

    @SerializedName("TestOne")
    @Expose
    private String testOne;

    @SerializedName("TestTwo")
    @Expose
    private String testTwo;

    @SerializedName("TestThree")
    @Expose
    private String testThree;

    public ExampleResponse() {
    }

    public ExampleResponse(String testOne, String testTwo, String testThree) {
        this.testOne = testOne;
        this.testTwo = testTwo;
        this.testThree = testThree;
    }

    public String getTestOne() {
        return testOne;
    }

    public void setTestOne(String testOne) {
        this.testOne = testOne;
    }

    public String getTestTwo() {
        return testTwo;
    }

    public void setTestTwo(String testTwo) {
        this.testTwo = testTwo;
    }

    public String getTestThree() {
        return testThree;
    }

    public void setTestThree(String testThree) {
        this.testThree = testThree;
    }
}
