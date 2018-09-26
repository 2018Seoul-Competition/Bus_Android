package com.ndc.bus.Activity;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.R;
import com.ndc.bus.databinding.ActivitySettingBinding;


public class SettingActivity extends BaseActivity {
    private ActivitySettingBinding binding;
    Switch lanSwitch;

    @Override
    public void initSettings() {
        super.initSettings();
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setActivity(this);

        lanSwitch = (Switch) findViewById(R.id.switch_Lan);

        lanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sf = getSharedPreferences(BaseApplication.VEH_LOG, 0);
                SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
                String strLan = "";
                if (isChecked == true){
                    BaseApplication.LAN_MODE = "KR";
                    strLan = "KR";
                } else {
                    BaseApplication.LAN_MODE = "EN";
                    strLan = "EN";
                }
                editor.putString(BaseApplication.LAN_INTENT, strLan); // 입력
                editor.apply(); // 파일에 최종 반영함
            }
        });
    }
}