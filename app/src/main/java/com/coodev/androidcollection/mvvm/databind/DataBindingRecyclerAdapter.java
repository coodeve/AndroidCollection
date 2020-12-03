package com.coodev.androidcollection.mvvm.databind;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.coodev.androidcollection.R;
import com.coodev.androidcollection.databinding.ActivityDataBindingRecyclerItemBinding;
import com.coodev.androidcollection.entity.UserInfo;

import java.util.List;

public class DataBindingRecyclerAdapter extends RecyclerView.Adapter<DataBindingRecyclerAdapter.AViewHolder> {
    private List<UserInfo> mUsers;

    public DataBindingRecyclerAdapter(List<UserInfo> users) {
        mUsers = users;
    }

    @NonNull
    @Override
    public AViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ActivityDataBindingRecyclerItemBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.activity_data_binding_recycler_item,
                parent,
                false);

        return new AViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AViewHolder holder, int position) {
        final UserInfo userInfo = mUsers.get(position);
        holder.dataBinding.setUserInfo(userInfo);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class AViewHolder extends RecyclerView.ViewHolder {
        ActivityDataBindingRecyclerItemBinding dataBinding;
        public AViewHolder(@NonNull ActivityDataBindingRecyclerItemBinding viewBinding) {
            super(viewBinding.getRoot());
            dataBinding = viewBinding;
        }
    }
}
