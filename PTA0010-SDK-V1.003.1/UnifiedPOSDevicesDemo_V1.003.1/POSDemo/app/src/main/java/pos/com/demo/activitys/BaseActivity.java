package pos.com.demo.activitys;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pos.com.demo.R;
import pos.com.demo.utils.LogUtil;


public abstract class BaseActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(findLayoutId());
        findViews();
        init();
    }

    protected abstract int findLayoutId();
    protected abstract void findViews();
    protected abstract void init();


    public void setTitle(int id){
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(id);
    }

    public void showLog(String str){
        LogUtil.info(str);
    }

    public void showErr(String str){
        LogUtil.error(str);
    }
}
