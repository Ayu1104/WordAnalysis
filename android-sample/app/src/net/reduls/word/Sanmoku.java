package net.reduls.word;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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
        label.setText(noun_word.get(120));
        
	}//解析ボタンクリックした処理終わり
	//メモ
	//tf法
	//単語の頻度／文章で出現する総単語数
	private void wordanalysisbotton_OnClick(View v) {
		
		
	}
}