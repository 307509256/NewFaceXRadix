package com.yxkj.facexradix.tts;

/**
 * @author: Dreamcoding
 * @Desription:  用于TTS的初始化状态监听
 * @date: 2018/12/6
 */
public interface TTSListener {
    void onSuccess(int result);
    void onError(int errCode, String message);
}
