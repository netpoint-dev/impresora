package com.common.demo.nfc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.databinding.ActivityNfcNewBinding;

import java.io.IOException;

/**
 * NFC 拉起页面
 */
public class NFCActivityNew extends Activity implements NfcAdapter.ReaderCallback {

    //支持的标签类型
    private NfcAdapter nfcAdapter;
    private ActivityNfcNewBinding binding;
    int successCount = 0;

    private MyReceiver myReceiver;

    private int apduSuccessCount;   //发送apdu成功次数
    private int apduAllCount;   //发送apdu成功次数

    private final static String NFC_RESET_FINISH_ACTION = "android.intent.action.NFC_RESET";
    private Tag tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityNfcNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "该机型不支持NFC", Toast.LENGTH_LONG).show();
            finish();
        }
        // Register callback  *设置一个回调，使用Android Beam（TM）动态生成要发送的NDEF消息。
//        nfcAdapter.setNdefPushMessageCallback(this, this);
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B, null);
        binding.boundingBox.setVisibility(View.GONE);
        if(SystemUtil.getInternalModel().equals("C9G") ||
                SystemUtil.getInternalModel().equals("C11") ||
                SystemUtil.getInternalModel().equals("C2")){
            binding.boundingBox.setVisibility(View.VISIBLE);

            Toast.makeText(this, getString(R.string.nfc_area_text), Toast.LENGTH_SHORT).show();
        }

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NFC_RESET_FINISH_ACTION);

        // 注册广播接收器
        registerReceiver(myReceiver, intentFilter);

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonNfcAPDU:
                if (tag == null) break;
                String apduCountStr = binding.editTextNfcAPDUCount.getText().toString();
                NfcApduTask nfcApduTask = new NfcApduTask(tag, Integer.valueOf(apduCountStr));
                nfcApduTask.execute();
//                sendApdu(tag);
                break;
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 处理接收到的广播事件
            if (intent.getAction().equals(NFC_RESET_FINISH_ACTION)) {
                if(nfcAdapter != null) {
                    nfcAdapter.disableReaderMode(NFCActivityNew.this);
                    nfcAdapter = NfcAdapter.getDefaultAdapter(NFCActivityNew.this);

                    // Register callback  *设置一个回调，使用Android Beam（TM）动态生成要发送的NDEF消息。
//                    nfcAdapter.setNdefPushMessageCallback(NFCActivityNew.this, NFCActivityNew.this);
                    nfcAdapter.enableReaderMode(NFCActivityNew.this, NFCActivityNew.this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B, null);
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        nfcAdapter.disableReaderMode(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解注册广播接收器
        if(myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
    }

    /*@Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = "Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis();
        return new NdefMessage(
                new NdefRecord[]{
                        NdefRecord.createMime("application/vnd.com.example.android.beam", text.getBytes())
                }
        );
    }*/

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d("tagg","nfc onTagDiscovered");
        long startTime = System.currentTimeMillis();
        this.tag = tag;

        String[] techList = tag.getTechList();
        String data = tag.toString();
        byte[] ID =  tag.getId();
        if(ID != null && !data.isEmpty()){
            data += "\n\nUID:\n" +StringUtil.toHexString(ID);
            data += "\nData format:";
            for (String tech : techList) {
                data += "\n" + tech;
            }
            successCount++;
        }
        long endTime = System.currentTimeMillis();
        String finalData = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.textNfcScanSuccessCount.setText(getString(R.string.nfc_tv_scann_count) + " [" + successCount + "] " + getString(R.string.nfc_tv_scann_time) + " [" + (endTime - startTime) + " ms]\n");
                binding.tvShowNfc.setText(finalData);
                if(!finalData.isEmpty()) {
                    binding.buttonNfcAPDU.setEnabled(true);
                }
            }
        });
    }

    private class NfcApduTask extends AsyncTask<Void, byte[], Void> {

        private int count;
        private Tag tag;
        private int apduAllCount = 0;
        private int apduSuccessCount = 0;
        private boolean isRunning = true; // 标记任务是否在运行

        public NfcApduTask(Tag tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (isRunning && count > 0) {
                sendApdu();
                count--;
                try {
                    Thread.sleep(200); // 延时1秒后发送下一条命令
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void sendApdu() {
            String apduStr = binding.editTextNfcAPDU.getText().toString();
            byte[] pSendAPDU = StringUtil.hexStringToBytes(apduStr);
            byte[] response = null;
            apduAllCount++;

            // 发送APDU命令并接收响应
            IsoDep isoDep = IsoDep.get(tag);
            try {
                isoDep.connect();
                response = isoDep.transceive(pSendAPDU);
                isoDep.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            publishProgress(response);
        }

        @Override
        protected void onProgressUpdate(byte[]... responses) {
            super.onProgressUpdate(responses);
            byte[] response = responses[0];
            updateUi(response);
        }

        private void updateUi(byte[] response) {
            if (response != null) {
                binding.textNfcReader.setText(TextUtils.isEmpty(StringUtil.toHexString(response)) ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + StringUtil.toHexString(response));
                if (!TextUtils.isEmpty(StringUtil.toHexString(response))) {
                    apduSuccessCount++;
                }
            } else {
                binding.textNfcReader.setText(getString(R.string.send_APDU_fail));
            }

            binding.textNfcApduCount.setText(getString(R.string.nfc_tv_apdu_count) + " [" + apduAllCount + "] " + getString(R.string.nfc_tv_apdu_success_count) + " [" + apduSuccessCount + "]");
        }

        public void stopSendingApdu() {
            isRunning = false;
        }
    }

}
