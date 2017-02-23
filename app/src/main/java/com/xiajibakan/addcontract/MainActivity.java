package com.xiajibakan.addcontract;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //添加
    public void add(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    //清空
    public void clear(View view) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("请稍等");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在删除联系人，请稍等");
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                int count = 0;
//                ContentResolver cr = getContentResolver();
//                // 查询contacts表的所有记录
//                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
//                        null, null, null);
//                // 如果记录不为空
//                if (cursor.getCount() > 0) {
//                    while (cursor.moveToNext()) {
//                        mCount = cursor.getCount();
//                        mProgressDialog.setMax(mCount);
//                        count++;
//                        String name = cursor.getString(cursor
//                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//                        Cursor cursor1 = cr.query(uri, new String[]{ContactsContract.Contacts.Data._ID}, "display_name=?", new String[]{name}, null);
//                        if (cursor1.moveToFirst()) {
//                            int id = cursor1.getInt(0);
//                            cr.delete(uri, "display_name=?", new String[]{name});
//                            uri = Uri.parse("content://com.android.contacts/data");
//                            cr.delete(uri, "raw_contact_id=?", new String[]{id + ""});
//                        }
//                        cursor1.close();
//                        mProgressDialog.setProgress(count);
//                    }
//                    cursor.close();
//                    mHandler.sendEmptyMessage(-1);
//                }
                //  第二种
//                Cursor contactsCur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//                while(contactsCur.moveToNext()){
//                    //获取ID
//                    String rawId = contactsCur.getString(contactsCur.getColumnIndex(ContactsContract.Contacts._ID));
//                    //删除
//                    String where = ContactsContract.Data._ID  + " =?";
//                    String[] whereparams = new String[]{rawId};
//                    getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, where, null);
//                }
                //第三种
//                ContentResolver cr = getContentResolver();
//                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
//                        null, null, null, null);
//                while (cur.moveToNext()) {
//                    try{
//                        String lookupKey = cur.getString(cur.getColumnIndex(
//                                ContactsContract.Contacts.LOOKUP_KEY));
//                        Uri uri = Uri.withAppendedPath(ContactsContract.
//                                Contacts.CONTENT_LOOKUP_URI, lookupKey);
//                        System.out.println("The uri is " + uri.toString());
//                        cr.delete(uri, null, null);//删除所有的联系人
//                    }
//                    catch(Exception e)
//                    {
//                        System.out.println(e.getStackTrace());
//                    }
//                }
                //第四种
                Uri uri = Contacts.People.CONTENT_URI;
                getContentResolver().delete(uri, null, null);
                mHandler.sendEmptyMessage(-1);
            }
        }).start();

    }
}
