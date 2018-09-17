package com.ndc.bus.Activity;

import android.databinding.DataBindingUtil;
import com.ndc.bus.R;
import com.ndc.bus.databinding.ActivitySettingBinding;


public class SettingActivity extends BaseActivity {
    private ActivitySettingBinding binding;

    @Override
    public void initSettings() {
        super.initSettings();
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setActivity(this);

    }
}

