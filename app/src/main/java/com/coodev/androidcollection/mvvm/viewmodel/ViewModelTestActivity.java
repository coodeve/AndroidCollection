package com.coodev.androidcollection.mvvm.viewmodel;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coodev.androidcollection.R;
import com.coodev.androidcollection.dao.room.User;

import java.util.List;

public class ViewModelTestActivity extends AppCompatActivity {

    private TimerViewModel mTimerViewModel;
    private TextView mMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_model_test);
        mMsg = findViewById(R.id.tv_msg);
        initViewModel();
    }

    private void initViewModel() {
        // 获取viewmodel实例，同一个activity应该只有一个
        mTimerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        // 使用LiveData
        final MutableLiveData<Integer> liveData = (MutableLiveData<Integer>) mTimerViewModel.getLiveData();
        liveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                // 数据变化，显示数据或者其他处理
                // TODO anything
                mMsg.setText(String.valueOf(integer));
            }
        });

        mMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 数据更新 主线程
                liveData.setValue(0);
                // 数据更新 子线程
                liveData.postValue(0);
            }
        });

    }

    /**
     * Room和LiveData结合使用
     */
    private void testRoomWithLiveData() {
        final RoomViewModel roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        roomViewModel.getListLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                // 更新数据
            }
        });
    }


    @Override
    protected void onDestroy() {
        mTimerViewModel.onCleared();
        super.onDestroy();

    }
}
