package com.ndc.bus.Activity;

import android.content.Intent;

public class IndexActivity extends BaseActivity{

    @Override
    public void initSettings(){
        //처음 화면 보여주기

        //DB 호출

        //다음 Activity로 변환
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
