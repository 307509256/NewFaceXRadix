package com.yxkj.facexradix.tts;

import android.speech.tts.TextToSpeech;

import com.yxdz.commonlib.util.LogUtils;
import com.yxkj.facexradix.MyApplication;

import java.util.Locale;

/**
 * @ClassName: TextToSpeechX
 * @Desription: 文字转语音类
 * @author: Dreamcoding
 * @date: 2018/12/6 10:12
 */
public class SpeechX {

    private static final String TAG = SpeechX.class.getSimpleName();
    private static volatile SpeechX instance=null;
    private TextToSpeech textToSpeech;
    public boolean bOpen;
    public String errorMessage;

    private SpeechX(){
        textToSpeech = new TextToSpeech(MyApplication.getAppContext(),new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        errorMessage="数据丢失或语言不支持";
                    }else {
                        LogUtils.d(TAG,"文本语音初始化成功");
                        bOpen=true;
                    }
                }else{
                    errorMessage="文本转语音初始化失败";
                }
            }
        });
    }
    public static SpeechX getInstance() {
        if(instance==null){
            synchronized(SpeechX.class){
                if(instance==null){
                    instance=new SpeechX();
                }
            }
        }
        return instance;
    }

    /**
     * @author Dreamcoding
     * @update 2018/12/6 11:12
     * @desc 根据文字speak
     * @param speakStr 说话内容
     */
    public void speak(String speakStr){
        speak(speakStr,1.0f);

    }

    /**
     * @author Dreamcoding
     * @update 2018/12/6 11:12
     * @desc 根据文字speak
     * @param speakStr 说话内容
     * @param pitch  设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
  */
    public void speak(String speakStr,float pitch){
        if (bOpen && !textToSpeech.isSpeaking()) {
            textToSpeech.setPitch(pitch);
            textToSpeech.speak(speakStr,TextToSpeech.QUEUE_FLUSH, null);
//            textToSpeech.speak(speakStr,TextToSpeech.QUEUE_FLUSH,null,null);
        }else{
            LogUtils.d(TAG,"异常错误无法播放语音");
        }
    }

    public void close(){
        textToSpeech.shutdown();
    }



}
