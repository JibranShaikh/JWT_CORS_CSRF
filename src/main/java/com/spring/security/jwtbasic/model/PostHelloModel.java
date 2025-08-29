package com.spring.security.jwtbasic.model;

public class PostHelloModel {


    String random;
    String arbitrary;

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getArbitrary() {
        return arbitrary;
    }

    public void setArbitrary(String arbitrary) {
        this.arbitrary = arbitrary;
    }

    @Override
    public String toString() {
        return "PostHelloModel{" +
                "random='" + random + '\'' +
                ", arbitrary='" + arbitrary + '\'' +
                '}';
    }
}
