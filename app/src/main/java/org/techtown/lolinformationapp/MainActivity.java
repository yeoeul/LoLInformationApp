package org.techtown.lolinformationapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LinearLayout leagueScheduleLayout;
    LinearLayout leagueRankingLayout;
    ArrayList<Button> weekButton = new ArrayList<Button>();
    ArrayList<TextView> rank = new ArrayList<TextView>();
    ArrayList<ImageView> teamLogo = new ArrayList<ImageView>();
    ArrayList<TextView> teamName = new ArrayList<TextView>();
    ArrayList<TextView> win = new ArrayList<TextView>();
    ArrayList<TextView> lose = new ArrayList<TextView>();
    ArrayList<TextView> winRate = new ArrayList<TextView>();
    ArrayList<TextView> point = new ArrayList<TextView>();

    ArrayList<ArrayList<TextView>> dataGroup = new ArrayList<ArrayList<TextView>>(Arrays.asList(rank, teamName, win, lose, winRate, point));
    ArrayList<String> dataList = new ArrayList<String>(Arrays.asList("rank","teamName","win","lose","winRate","point"));

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        if (NetworkConnection() == false)
            NotConnected_showAlert();
*/
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*///풀스크린


        initView();

        leagueRankingLayout = this.findViewById(R.id.leagueRankingLayout);
        leagueScheduleLayout = this.findViewById(R.id.leagueScheduleLayout);
        //leagueScheduleLayout.setVisibility(View.GONE);
        leagueRankingLayout.setVisibility(View.GONE);

        new Thread(new Runnable(){
            public void run(){
                Elements elements = bringScheduleData();
                String str[] = elements.text().split(" ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i < str.length; i += 2) weekButton.get(i / 2).setText(str[i] + " " + str[i + 1]);
                    }
                });
            }
        }).start();

        new Thread() {
            public void run(){
                bringRankingData();
                Message msg = teamLogoHandler.obtainMessage();
                teamLogoHandler.sendMessage(msg);
            }
        }.start();
    }

    private void initView() {
        for(int i = 1; i <= 10; i++){
            String array = "week" + i + "Button";
            int resID = getResources().getIdentifier(array, "id", getPackageName());
            weekButton.add((findViewById(resID)));
        }
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 10; j++){
                String array = dataList.get(i) + (j + 1);
                int resID = getResources().getIdentifier(array, "id", getPackageName());
                dataGroup.get(i).add((findViewById(resID)));
            }
        }
        for(int i = 1; i <= 10; i++){
            String array = "teamLogo" + i;
            int resID = getResources().getIdentifier(array, "id", getPackageName());
            teamLogo.add((findViewById(resID)));
        }
    }

    private Elements bringScheduleData() {
        Elements elements = null;
        try{
            Document doc = Jsoup.connect("https://search.naver.com/search.naver?where=nexearch&sm=tab_etc&mra=bjFE&pkid=475&os=17568053&query=2021%20LoL%20%EC%B1%94%ED%94%BC%EC%96%B8%EC%8A%A4%20%EC%BD%94%EB%A6%AC%EC%95%84%20%EC%8A%A4%ED%94%84%EB%A7%81%20%EB%A6%AC%EA%B7%B8%EC%9D%BC%EC%A0%95").get();
            elements = doc.select(".this_text");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elements;
    }

    private void bringRankingData() {
        try {
            Document doc = Jsoup.connect("https://search.naver.com/search.naver?where=nexearch&sm=tab_etc&mra=bjFE&pkid=475&os=17568053&query=2021%20LoL%20%EC%B1%94%ED%94%BC%EC%96%B8%EC%8A%A4%20%EC%BD%94%EB%A6%AC%EC%95%84%20%EC%8A%A4%ED%94%84%EB%A7%81%20%EC%A0%95%EA%B7%9C%EC%88%9C%EC%9C%84").get();
            Elements elements = doc.select("td");
            String str[] = elements.text().split(" ");
            for(int i = 0, count = 0; i < str.length; i++) {
                if (str[i].equals("담원") || str[i].equals("리브")) {
                    dataGroup.get((i - count) % 6).get((i - count) / 6).setText(str[i] + " " + str[i + 1]);
                    i++;
                    count++;
                } else dataGroup.get((i - count) % 6).get((i - count) / 6).setText(str[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final Handler teamLogoHandler = new Handler() {
        public void handleMessage(Message msg) {
            setTeamLogo();
        }
    };

    private void setTeamLogo(){
        String text = "";
        for(int i = 0; i < 10; i++) {
            text = dataGroup.get(1).get(i).getText().toString();

            if(text.equals("DRX")) teamLogo.get(i).setImageResource(R.drawable.drx);
            else if(text.equals("KT")) teamLogo.get(i).setImageResource(R.drawable.kt);
            else if(text.equals("T1")) teamLogo.get(i).setImageResource(R.drawable.t1);
            else if(text.equals("농심")) teamLogo.get(i).setImageResource(R.drawable.nongsim);
            else if(text.equals("담원 기아")) teamLogo.get(i).setImageResource(R.drawable.damwon);
            else if(text.equals("리브 샌박")) teamLogo.get(i).setImageResource(R.drawable.sandbox);
            else if(text.equals("아프리카")) teamLogo.get(i).setImageResource(R.drawable.afreeca);
            else if(text.equals("젠지")) teamLogo.get(i).setImageResource(R.drawable.geng);
            else if(text.equals("프레딧")) teamLogo.get(i).setImageResource(R.drawable.fredit);
            else if(text.equals("한화생명")) teamLogo.get(i).setImageResource(R.drawable.hanhwa);
        }
    }

    private void NotConnected_showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.\n" + "먼저 무선네트워크 연결상태를 확인해 주세요.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean NetworkConnection() {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.getType() == networkType) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}