package com.xiajibakan.addcontract;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brioal.labelview.LabelView;
import com.brioal.labelview.entity.LabelEntity;
import com.brioal.labelview.interfaces.OnLabelSelectedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private int count = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("Count:" + msg.what);
            if (msg.what == -1 && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };
    private Toast mToast;
    private List<LabelEntity> mProData = new ArrayList<>();//存储省份
    private List<LabelEntity> mCityData = new ArrayList<>();//存储城市
    private String mPro = "上海";
    private String mCity = "上海";
    private LabelView mLabePro;
    private LabelView mLabelCity;
    private EditText mEtCount;
    private Button mBtnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mLabePro = (LabelView) findViewById(R.id.add_label_pro);
        mLabelCity = (LabelView) findViewById(R.id.add_label_city);
        mEtCount = (EditText) findViewById(R.id.add_et_count);
        mBtnAdd = (Button) findViewById(R.id.add_btn_add);
        showPro();
        showCity(0);
        showSelected(mLabePro, 0);
        showSelected(mLabelCity, 0);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(mEtCount.getText().toString());
                if (mPro.isEmpty()) {
                    showToast("省份不能为空");
                    return;
                }
                if (mCity.isEmpty()) {
                    showToast("城市不能为空");
                    return;
                }
                if (value == 0 || value < 0) {
                    showToast("请输入正确的次数");
                    return;
                }
                count = value;
                addContract(value);
            }
        });
    }

    //显示城市
    private void showCity(int index) {
        mLabelCity.setListener(new OnLabelSelectedListener() {
            @Override
            public void selected(int position, String content) {
                if (mPro.isEmpty()) {
                    showToast("请先选择省份");
                } else {
                    showSelected(mLabelCity, position);
                    mCity = mCityData.get(position).getTitle();
                }

            }
        });
        mCityData = new ArrayList<>();
        mLabelCity.removeAllViews();
        try {
            String[] fileNames = getAssets().list("num/" + mProData.get(index).getTitle());
            for (int i = 0; i < fileNames.length; i++) {
                System.out.println(fileNames[i]);
                String name = fileNames[i].replace(".txt", "");
                mCityData.add(new LabelEntity(name, i + ""));
            }
            mLabelCity.setLabelColor(Color.DKGRAY);
            mLabelCity.setLabels(mCityData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //显示省会
    private void showPro() {
        mLabePro.setListener(new OnLabelSelectedListener() {
            @Override
            public void selected(int position, String content) {
                showSelected(mLabePro, position);
                showCity(position);
                mPro = mProData.get(position).getTitle();
                mCity = "";
            }
        });
        mProData = new ArrayList<>();
        try {
            String[] fileNames = getAssets().list("num");
            for (int i = 0; i < fileNames.length; i++) {
                System.out.println(fileNames[i]);
                mProData.add(new LabelEntity(fileNames[i], i + ""));
            }
            mLabePro.setLabelColor(Color.DKGRAY);
            mLabePro.setLabels(mProData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSelected(ViewGroup viewGroup, int position) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (i == position) {
                child.setBackgroundResource(R.drawable.ic_bg_select);
            } else {
                child.setBackgroundResource(R.drawable.ic_bg_normal);
            }
        }
    }


    private void showToast(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    //添加联系人
    public void addContract(final int count) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("请稍等");
        mProgressDialog.setMessage("正在添加联系人，请稍等");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(count);
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer buffer = new StringBuffer();
                try {
                    InputStream is = getAssets().open("num/" + mPro + "/" + mCity + ".txt");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "gbk"));
                    String str = "";
                    while ((str = br.readLine()) != null) {
                        buffer.append(str);
                    }
                    String[] phoneQian = buffer.toString().split("\\D");
                    if (phoneQian.length == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("参数错误，请稍后重试");
                                mProgressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    //开始生成电话号码
                    List<String> list = new ArrayList<>();
                    List<Contract> contracts = new ArrayList<Contract>();
                    for (int i = 0; i < count; i++) {
                        Random random = new Random();
                        String qianqi = phoneQian[random.nextInt(phoneQian.length)];//获得前七位
                        System.out.println("前七位" + qianqi);
                        String housi = addZero(random.nextInt(9999));
                        System.out.println("后四位：" + housi);
                        String phone = qianqi + housi;
                        System.out.println("Phone:" + phone);
                        if (!list.contains(phone)) {
                            list.add(phone);
                            // addContract(i + "", phone);
                            contracts.add(new Contract(phone, phone));
                        } else {
                            i--;
                        }

                    }
                    int index = 0;
                    if (contracts.size() > 1000) {
                        while (index < contracts.size()) {
                            List<Contract> ss = new ArrayList<>();
                            for (int i = index; i < ((index + 1000) > contracts.size() ? contracts.size() : (index + 100)); i++) {

                                ss.add(contracts.get(i));
                            }
                            index = index + 1000;
                            addAll(ss);
                        }
                    } else {
                        addAll(contracts);
                    }
                    mHandler.sendEmptyMessage(-1);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast("参数错误，请稍后重试");
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        });
        new Thread(new TestRunnable()).start();


    }

    private class TestRunnable implements Runnable {

        @Override
        public void run() {
            StringBuffer buffer = new StringBuffer();
            try {
                InputStream is = getAssets().open("num/" + mPro + "/" + mCity + ".txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "gbk"));
                String str = "";
                while ((str = br.readLine()) != null) {
                    buffer.append(str);
                }
                String[] phoneQian = buffer.toString().split("\\D");
                if (phoneQian.length == 0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast("参数错误，请稍后重试");
                            mProgressDialog.dismiss();
                        }
                    });
                    return;
                }
                //开始生成电话号码
                List<String> list = new ArrayList<>();
                List<Contract> contracts = new ArrayList<Contract>();
                int s = 0;
                for (int i = 0; i < count; i++) {
                    Random random = new Random();
                    String qianqi = phoneQian[random.nextInt(phoneQian.length)];//获得前七位
                    System.out.println("前七位" + qianqi);
                    String housi = addZero(random.nextInt(9999));
                    System.out.println("后四位：" + housi);
                    String phone = qianqi + housi;
                    System.out.println("Phone:" + phone);
                    if (!list.contains(phone)) {
                        list.add(phone);
                        // addContract(i + "", phone);
                        contracts.add(new Contract(phone, phone));
                    } else {
                        i--;
                    }
                    s++;
                    System.out.println("count"+s);
                    mProgressDialog.setProgress(i);
                }
                int index = 0;
                if (contracts.size() > 1000) {
                    while (index < contracts.size()) {
                        List<Contract> ss = new ArrayList<>();
                        for (int i = index; i < ((index + 1000) > contracts.size() ? contracts.size() : (index + 1000)); i++) {

                            ss.add(contracts.get(i));
                        }
                        index = index + 1000;
                        addAll(ss);
                    }
                } else {
                    addAll(contracts);
                }
                mHandler.sendEmptyMessage(-1);
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("参数错误，请稍后重试");
                        mProgressDialog.dismiss();
                    }
                });
            }
        }
    }
    
    private void addAll(List<Contract> list) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex;
        for (int i = 0; i < list.size(); i++) {
            rawContactInsertIndex = ops.size();//这句好很重要，有了它才能给真正的实现批量添加。

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, list.get(i).getName())
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, list.get(i).getPhone())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withYieldAllowed(true)
                    .build());
            System.out.println(i);

        }
        try {
//这里才调用的批量添加
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            System.out.println("添加联系人完成");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private String addZero(int index) {
        String in = index + "";
        int cha = 4 - in.length();
        if (cha == 0) {
            return in;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cha; i++) {
            result.append("0");
        }
        result.append(in);
        return result.toString();
    }


}
