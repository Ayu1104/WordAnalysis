package net.reduls.word;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.res.AssetManager; //アセット
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Sanmoku extends Activity implements TextWatcher
{
	//public String noun;
	public StringBuilder sb = new StringBuilder(); //歌詞読み込み用
	public StringBuilder noun = new StringBuilder(); //名詞抽出用
	public int total = 0; //名詞の総数を記録
	public String test = "test";
	//可変長配列
	public ArrayList<String> noun_word = new ArrayList<String>(); //抽出した名詞いれる
	//歌詞リスト
	int array = 15;
	public String[] category = new String[array];
	//歌詞の分類
	public int [] season = new int[4];
	//0...春　1...夏　2...秋　3...冬
	public int [] weather = new int[4];
	//0...晴れ　1...曇り　2...雨　3...雪
	public int [] time = new int[5];
	//0...朝　1...昼　2...夕　3...夜　4...深夜
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	Arrays.fill(season, 0);
    	//季節 spring summer autumn winter
    	category[0] = "春　桜　さくら　サクラ　三月　四月　五月　蝶　卒業";
    	category[1] = "夏　梅雨　海　アイス　入道雲　六月　七月　八月　蛍　ホタル　ほたる　サマー　熱　花火　熱帯夜";
    	category[2] = "秋　落ち葉　枯れ葉　九月　十月　十一月　ハロウィン";
    	category[3] = "冬　枯れ木　雪　十二月　一月　二月　マフラー　イルミネーション　クリスマス　暖炉";
    	
    	//天気sunny cloudy rain snow
    	category[4] = "晴　太陽　日ざし　日差し　陽射し　日光　暑";
    	category[5] = "曇り空　雲　曇り　積乱雲　霧";
    	category[6] = "雨　レイン　台風";
    	category[7] = "雪　冬　ソリ　スキー　暖炉";
    	
    	//時間 morning noon evening night midnight
    	category[8] = "朝　明け方　太陽　日光　日差し　陽射し　おはよう";
    	category[9] = "昼　昼下がり";
    	category[10] = "夕　夕方";
    	category[11] = "夜　眠れない　おやすみ　朝まで　ナイト　ベッド　死　星　月　night";
    	category[12] = "深夜　午前二時　ベッド　ナイト";
    	//場所
    	//山　海
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View searchBar = findViewById(R.id.search_bar);
        ((EditText)searchBar).addTextChangedListener(this);

        String key = getIntent().getStringExtra("search.key");
        getIntent().putExtra("search.key","");
        if(key!=null && key.equals("")==false)
            ((EditText)searchBar).setText(key);
        
        //ボタンクリック処理
        //形態素解析ボタン
		Button analysbtn = (Button) this.findViewById(R.id.analysbtn);
		analysbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				analysbotton_OnClick(v);
			}
		});
		
		//歌詞解析ボタン
		Button analysisbtn = (Button) this.findViewById(R.id.analysisbtn);
		analysisbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wordanalysisbotton_OnClick(v);
			}
		});
		//ボタンクリック処理ここまで
    }
    
	

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    
    public void afterTextChanged(Editable s) {
        String key = s.toString();
        //String key = "YO! SAY! 夏が胸を刺激する 生足魅惑のマーメイド 出すとこだして たわわになったら 宝物の恋は やれ爽快 誤魔化し聞かない 薄着の曲線は 確信犯の しなやかなSTYLE 耐水性の 気持ちに切り替わる  瞬間のまぶしさは いかがなもの 心まで脱がされる 熱い風の誘惑に 負けちゃって構わないから 真夏は 不祥事も 君次第で 妖精たちが 夏を刺激する 生足魅惑のマーメイド 出すとこだして たわわになったら 宝物の恋が できそうかい 君じゃなくても バテ気味にもなるよ 暑いばっかの 街は憂鬱で スキを見せたら 不意に耳に入る サブいギャグなんかで 涼みたくない むせ返る熱帯夜を 彩る花火のように 打ち上げて散る思いなら  このまま抱き合って 焦がれるまで 妖精たちと 夏をしたくなる 熱い欲望はトルネイド 出すものだして 素直になりたい 君と僕となら it`s all right 都会のビルの上じゃ 感じなくなってる君を 冷えたワインの口づけで 酔わせて とろかせて 差し上げましょう 妖精たちが 夏を刺激する 生足ヘソ出しマーメイド 恋にかまけて お留守になるのも ダイスケ的にも オールオーケー YO! SAY! 夏を誰としたくなる 一人根の夜に you can say good bye 奥の方まで 乾く間ないほど 宝物の恋を しま鮮花";
    	LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
        resultArea.removeAllViews();
        
        if(key.length() > 0) { 
            for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(key)) {
                TextView txt = new TextView(this);
                SpannableString spannable = DataFormatter.format("<"+e.surface+">\n"+e.feature);
                txt.setText(spannable, TextView.BufferType.SPANNABLE);
                resultArea.addView(txt);
            }
        }
    }
    
    //形態素ボタンをクリックしたとき
	private void analysbotton_OnClick(View v) {
		AssetManager as = getResources().getAssets();

		InputStream is = null;
		BufferedReader br = null;

		//StringBuilder sb = new StringBuilder();
		try {
			try {
				//歌詞を読み込む
				is = as.open("hotlimit.txt");
				br = new BufferedReader(new InputStreamReader(is));

				String str;
				while ((str = br.readLine()) != null) {
					sb.append(str + "\n");
					//song=new String(str);
				}
			} finally {
				if (br != null)
					br.close();
			}
		} catch (IOException e) { // 例外処理
			Toast.makeText(this, "読み込み失敗", Toast.LENGTH_SHORT).show();
		}
		//解析用の文字列にファイルの中身（sb）をコピー
		String key =new String(sb);
		//↓リザルトエリアに表示しますよーのやつ？
    	LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
        resultArea.removeAllViews();
        
        TextView label = (TextView) this.findViewById(R.id.label);
        
        if(key.length() > 0) { 
            for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(key)) {
                TextView txt = new TextView(this);
                SpannableString spannable = DataFormatter.format("<"+e.surface+">\n"+e.feature);
                txt.setText(spannable, TextView.BufferType.SPANNABLE);
                //String word = new String(spannable, TextView.BufferType.SPANNABLE);
                resultArea.addView(txt); //結果の表示
                //e.surface　は　元の単語
                //e.feature　は　品詞
                
                //名詞を抽出
                if (e.feature.contains("名詞")) {//一致の場合trueが帰ってくる
                	Log.d("たしかめ","名詞と判断されました");
                	noun.append(e.surface + "　");
                	total++; //名詞の総数カウント
                	Log.d("たしかめ","可変長配列に追加");
                	noun_word.add(new String(e.surface));                	
                }
                //label.setText(new String(noun + "\n名詞は" + total + "個"));
                //label.setText(total+""); //label.setText(total);にするとエラー吐くから空の文字はいれる
            }
            
        }
        //test = noun_word.get(100);
        label.setText("名詞の個数" + total + "試しに表示→" +noun_word.get(54) + "\n" + noun);
        
	}//解析ボタンクリックした処理終わり
	//メモ
	//tf法
	//単語の頻度／文章で出現する総単語数
	private void wordanalysisbotton_OnClick(View v) {
		Log.d("たしかめ","ボタン押された");
	    for (int i = 0; i < noun_word.size(); i++){
	    	Log.d("たしかめ", "配列" +i+ "の名詞" );
	        for (int j = 0; j <(array - 3); j++){
	        	Log.d("たしかめ", "配列" +j+ "の要素");
                if (category[j].contains(noun_word.get(i))) {
                	switch (j){
                	//季節
                	  case 0:
                		  Log.d("たしかめ","　　　　　　　　　　　　　　　　　　　　　　　春");
                		  season[0]++;
                		  break;
                	  case 1:
                		  season[1]++;
                		  break;
                	  case 2:
                		  Log.d("たしかめ","　　　　　　　　　　　　　　　　　　　　　　　秋");
                		  season[2]++;
                		  break;
                	  case 3:
                		  Log.d("たしかめ","　　　　　　　　　　　　　　　　　　　　　　　冬");
                		  season[3]++;
                	    break;
                	//天気
                	  case 4:
                		  weather[0]++;
                		  break;
                	  case 5:
                		  weather[1]++;
                		  break;
                  	  case 6:
                  		Log.d("たしかめ","　　　　　　　　　　　　　　　　　　　　　　　　　雨");
                  		  weather[2]++;
                  		  break;
                  	  case 7:
                  		Log.d("たしかめ","　　　　　　　　　　　　　　　　　　　　　　　　　雪");
                  		  weather[3]++;
                  	      break;
                  	//時間帯
                  	  case 8:
                  		Log.d("たしかめ","　　　　　　　　　　　　　　　　　　　　　　　　　朝");
                  		  time[0]++;
                  		  break;
                  	  case 9:
                  		  time[1]++;
                  		  break;
                  	  case 10:
                  		  time[2]++;
                  	      break;
                  	  case 11:
                  		  time[3]++;
                    	  break;
                  	  case 12:
                  	      time[4]++;
                    	  break;
                      default:
   
                    	  break;
                	}
                }else{
                	//Log.d("どれにも","該当しない");
                }
	        }
	      }
	    TextView label2 = (TextView) this.findViewById(R.id.label2);
	    //ためしに表示
	    label2.setText(" 春の要素" + season[0] + 
	    			   " 夏の要素" + season[1] + 
	    			   " 秋の要素" + season[2] +
	    			   " 冬の要素" + season[3] +
	    			   " 晴れの要素" + weather[0] +
	    			   " 曇りの要素" + weather[1] +
	    			   " 雨の要素" + weather[2] +
	    			   " 雪の要素" + weather[3] +
	    			   " 朝の要素" + time[0] +
	    			   " 昼の要素" + time[1] +
	    			   "　夕方の要素" + time[2] +
	    			   " 夜の要素" + time[3] +
	    			   " 深夜の要素" + time[4]);
	}
}