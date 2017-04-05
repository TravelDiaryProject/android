package com.traveldiary.android.network;


public interface CallBack {
    void responseNetwork(Object o);
    void failNetwork(Throwable t);
}
