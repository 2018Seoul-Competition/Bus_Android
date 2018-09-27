package com.ndc.bus.Activity;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.view.View;
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

        binding.settingBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });

        lanSwitch = (Switch) findViewById(R.id.switch_Language);

        if(BaseApplication.LAN_MODE.compareTo("EN") == 0)
            lanSwitch.setChecked(true);

        lanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sf = getSharedPreferences(BaseApplication.VEH_LOG, 0);
                SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
                String strLan = "";
                if (isChecked == true){
                    BaseApplication.LAN_MODE = "EN";
                    strLan = "EN";
                } else {
                    BaseApplication.LAN_MODE = "KR";
                    strLan = "KR";
                }
                editor.putString(BaseApplication.LAN_INTENT, strLan); // 입력
                editor.apply(); // 파일에 최종 반영함
            }
        });
    }
}