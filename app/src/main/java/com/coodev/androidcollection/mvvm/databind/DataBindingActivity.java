package com.coodev.androidcollection.mvvm.databind;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.coodev.androidcollection.R;
import com.coodev.androidcollection.databinding.ActivityDataBindingBinding;
import com.coodev.androidcollection.entity.DataBindingBean;

/**
 * DataBinding使用
 * 单向绑定
 */
public class DataBindingActivity extends AppCompatActivity implements View.OnClickListener {

    public static class EventClick2 {
        public void onClickHere() {
            // do something
        }
    }

    private ActivityDataBindingBinding mViewDataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
        DataBindingBean dataBindingBean = new DataBindingBean();
        // mViewDataBinding.setVariable(BR.binding_bean, dataBindingBean);也可以使用此方法
        mViewDataBinding.setMessage("this is databinding test ");
        mViewDataBinding.setBindingBean(dataBindingBean);
        // 上述操作后,就关联到了view,不需要主动设置参数
        // 数据传给include界面
        mViewDataBinding.levelTwo.setBindingBean(dataBindingBean);
        // click操作
        mViewDataBinding.setEventClick(this);
        // 自定义DataBinding
        mViewDataBinding.setNetworkImage("http://www.xxx/picture.jpn");

    }

    /**
     * recyclerView处理
     */
    public void handleRecyclerView() {
        mViewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 使用onclick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

    }
}
