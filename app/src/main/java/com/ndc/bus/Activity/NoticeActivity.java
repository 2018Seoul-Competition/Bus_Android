package com.ndc.bus.Activity;

import android.databinding.DataBindingUtil;
import android.view.View;

import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.R;
import com.ndc.bus.databinding.ActivityNoticeBinding;



public class NoticeActivity extends BaseActivity {
    private ActivityNoticeBinding binding;

    @Override
    public void initSettings() {
        super.initSettings();
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_notice);
        binding.setActivity(this);

        binding.noticeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });

        if(BaseApplication.LAN_MODE.compareTo("EN") == 0)
            binding.noticeText.setText("NOTICE");
        else
            binding.noticeText.setText("공지사항");
    }
}
