package com.xiajibakan.addcontract;

/**
 * email : brioal@foxmial.com
 * github :https://github.com/Brioal
 * Created by Brioal on 2017/1/18.
 */

public class Contract {
    private String mName;
    private String mPhone;

    public Contract(String name, String phone) {
        mName = name;
        mPhone = phone;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }
}
