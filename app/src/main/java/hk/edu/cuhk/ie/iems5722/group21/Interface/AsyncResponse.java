package hk.edu.cuhk.ie.iems5722.group21.Interface;

import java.util.HashMap;

public interface AsyncResponse {

    void onDataReceivedSuccess(HashMap<String,Integer> Data);
    void onDataReceivedFailed();
}
