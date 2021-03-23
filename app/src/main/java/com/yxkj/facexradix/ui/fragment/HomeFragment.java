package com.yxkj.facexradix.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Gpio;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.idl.face.main.CodeHints;
import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.face.main.callback.CameraDataCallback;
import com.baidu.idl.face.main.callback.FaceDetectCallBack;
import com.baidu.idl.face.main.camera.AutoTexturePreviewView;
import com.baidu.idl.face.main.camera.CameraPreviewManager;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.face.main.model.LivenessModel;
import com.baidu.idl.face.main.model.SingleBaseConfig;
import com.baidu.idl.face.main.model.User;
import com.baidu.idl.face.main.utils.ConfigUtils;
import com.baidu.idl.face.main.utils.DensityUtils;
import com.baidu.idl.face.main.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.utils.FileUitls;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.yxdz.commonlib.base.BaseFragment;
import com.yxdz.commonlib.util.LogUtils;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxdz.serialport.util.DataProcessUtil;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.netty.util.ClientUtil;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.Time;
import com.yxkj.facexradix.tts.SpeechX;
import com.yxkj.facexradix.ui.activity.MainActivity;
import com.yxkj.facexradix.utils.GlideUtils;
import com.yxkj.facexradix.view.XKeyBoard2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @PackageName: com.yxdz.fadox.ui.fragment
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/1/30 11:07
 */
public class HomeFragment extends BaseFragment {

    private final static String CARD_PASSWORD = "12";
    private final static String CARD_FACE_PASSWORD = "13";
    private final static String FACE_PASSWORD = "14";
    private final static String CARD_FACE = "11";
    private static final int PREFER_WIDTH = 640;
    private static final int PERFER_HEIGH = 480;
    //    private static final int PREFER_WIDTH = 480;
//    private static final int PERFER_HEIGH = 640;
    boolean isNoface = false;
    private TextView tvUserName;
    private ImageView ivFace;
    private LinearLayout llShowUser;
    private AutoTexturePreviewView previewview;
    private TextureView textureview;
    private FrameLayout previewHolder;
    private TextView tvModeDes;
    private ImageView ivMode;
    private LinearLayout llShowFace;
    private LinearLayout cardOrFaceInfo;
    private XKeyBoard2 xKeyBoard2;
    private LinearLayout passwordLayout;
    private MainActivity mainActivity;
    private TextView cardOrFaceText;
    private String mode;
    private String cardNo = "";
    private boolean noSameCard;
    private StringBuffer sb;
    private boolean flag = true;
    private Handler handle;
    private String code;
    private String dataCardNo;
    private int deviceCtrlMode;
    private int deviceOutputMode;
    private boolean isStartFace = false;
    private byte projectType = (byte) Constants.PROJECT_JIANGSHENG;
    private User user;
    private Task task = new Task();
    private Task2 task2 = new Task2();
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private TextureView mFaceDetectImageView;
    private Paint paint;
    private RectF rectF;
    private int mLiveType;
    BroadcastReceiver viewChangeReceviver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.stopSceenControlService();
            mainActivity.initScreenControlService();
            if (mainActivity.blackScreen.getVisibility() == View.VISIBLE) {
                mainActivity.blackScreen.setVisibility(View.GONE);
            }
            checkMode();
        }
    };
    private String faceHolderpassword = "";
    //
    private int frameCount = 0;
    private Bitmap oldBitmap;
    private int lastSimilarity = 0;

    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }

    private static Result decodeQR(Bitmap bitmap) {
        Result result = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            // 新建一个RGBLuminanceSource对象
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            // 将图片转换成二进制图片
            BinaryBitmap binaryBitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            QRCodeReader reader = new QRCodeReader();// 初始化解析对象
            try {
                result = reader.decode(binaryBitmap, CodeHints.getCustomDecodeHints("ISO8859-1"));// 开始解析
            } catch (NotFoundException | ChecksumException | FormatException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static int similarity(Bitmap b, Bitmap viewBt) {
        //把图片转换为Bitmap
        int t = 0;
        int f = 0;
        Bitmap bm_one = b;
        Bitmap bm_two = viewBt;
        //保存图片所有像素个数的数组，图片宽×高
        int[] pixels_one = new int[bm_one.getWidth() * bm_one.getHeight()];
        int[] pixels_two = new int[bm_two.getWidth() * bm_two.getHeight()];
        //获取每个像素的RGB值
        bm_one.getPixels(pixels_one, 0, bm_one.getWidth(), 0, 0, bm_one.getWidth(), bm_one.getHeight());
        bm_two.getPixels(pixels_two, 0, bm_two.getWidth(), 0, 0, bm_two.getWidth(), bm_two.getHeight());
        //如果图片一个像素大于图片2的像素，就用像素少的作为循环条件。避免报错
        if (pixels_one.length >= pixels_two.length) {
            //对每一个像素的RGB值进行比较
            for (int i = 0; i < pixels_two.length; i++) {
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                //RGB值一样就加一（以便算百分比）
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }
        } else {
            for (int i = 0; i < pixels_one.length; i++) {
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }
        }
        return myPercent(t, t + f);
    }

    /**
     * 百分比的计算
     */
    public static int myPercent(int y, int z) {
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = (baiy / baiz) * 1000;
        return (Double.valueOf(fen)).intValue();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    public void onModel() {
        cardOrFaceText = rootView.findViewById(R.id.cardOrFaceText);
        ivFace = rootView.findViewById(R.id.ivFace);
        previewHolder = rootView.findViewById(R.id.flContainer);
        llShowUser = rootView.findViewById(R.id.llShowUser);
        tvUserName = rootView.findViewById(R.id.tvUserName);
        xKeyBoard2 = rootView.findViewById(R.id.xKeyBoard2);
        passwordLayout = rootView.findViewById(R.id.passwordLayout);
        llShowFace = rootView.findViewById(R.id.llShowFace);
        cardOrFaceInfo = rootView.findViewById(R.id.cardOrFaceInfo);
        ivMode = rootView.findViewById(R.id.ivMode);
        tvModeDes = rootView.findViewById(R.id.tvModeDes);
        SingleBaseConfig.getBaseConfig().setMirrorRGB(1);
        SingleBaseConfig.getBaseConfig().setType(2);
//        SingleBaseConfig.getBaseConfig().setDetectDirection(270);
//        SingleBaseConfig.getBaseConfig().setVideoDirection(90);
        ConfigUtils.modityJson();
    }

    @Override
    public void onData(Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_RECEIVER_CHANGEVIEW);
        MyApplication.getAppContext().registerReceiver(viewChangeReceviver, intentFilter);
        handle = new Handler();
        sb = new StringBuffer();
        mainActivity = (MainActivity) getActivity();
        xKeyBoard2.setKeyListener(key -> {
            if (key == '#') {
                handle.removeCallbacks(task2);
                flag = true;
                //设备控制模式，1为外接控制模式
                if (deviceCtrlMode == 1) {
                    //设备的输出模式，0为韦根输出模式，1为485输出模式
                    if (deviceOutputMode == 0) {
                        mainActivity.outBeer();
                        //韦根
                        outputPassword((byte) 0x0B);
                    } else {
                        //485
                        if (TextUtils.isEmpty(sb)) {
                            mainActivity.outBeer();
                            return;
                        }
                        String s = null;
                        try {
                            //s=intToHex(Integer.parseInt(sb.toString()));
                            //卡号数据为大写
                            s = Long.toHexString(Long.parseLong(sb.toString())).toUpperCase();
                        } catch (Exception e) {
                            ToastUtils.showLongToast("按键过多");
                        }

                        User isCardUser = FaceApi.getInstance().getUserByCardNo(s);
                        if (isCardUser != null) {
                            //输入是卡号
                            outputCardNo(s);
                            ClientUtil.sendOpenRecord(isCardUser.getUserId(), 40, 0, s);
                            sb.delete(0, sb.length());
                            mainActivity.outBeer();
                        } else {
                            mainActivity.outBeer();
                            outputPassword(sb.toString());
                            sb.delete(0, sb.length());
                        }
                    }
                    xKeyBoard2.setDisOrder();
                } else {
                    dealPassword();
                }
            } else if (key == '*') {
                mainActivity.outBeer();
                if (deviceCtrlMode == 1) {
                    outputPassword((byte) 0x0A);
                }
                sb.delete(0, sb.length());
                handle.removeCallbacks(task2);
            } else {
                if (deviceCtrlMode == 1) {
                    if (deviceOutputMode == 0) {
                        //韦根
                        mainActivity.outBeer();
                        outputPassword((byte) Integer.parseInt(key + ""));
                    } else {
                        mainActivity.outBeer();
                        sb.append(key);
                    }
                } else {
                    mainActivity.outBeer();
                    sb.append(key);
                }
                if (flag) {
                    flag = false;
                    handle.postDelayed(task2, 10000);
                }
            }


        });
        checkMode();
        xKeyBoard2.changeColor();
        initView();
    }

    private void outputPassword(byte data) {
        handle.postDelayed(() -> mainActivity.outputPassword(data), 100);
    }

    private void outputPassword(String data) {
        handle.postDelayed(() -> mainActivity.outputPassword(projectType, data), 100);
    }


    //================================================================================================
    //人脸识别

    public void checkMode() {
        isStartFace = false;
        deviceCtrlMode = SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0);
        deviceOutputMode = SPUtils.getInstance().getInt(Constants.DEVICE_OUTPUT_MODE, 1);
        mode = SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE, "1");
        ivMode.setVisibility(View.VISIBLE);
        tvModeDes.setVisibility(View.VISIBLE);
        try {
            switch (mode) {
                case CARD_FACE:
                    cardOrFaceInfo.setVisibility(View.GONE);
                    llShowUser.setVisibility(View.GONE);
                    previewHolder.setVisibility(View.GONE);
                    llShowFace.setVisibility(View.VISIBLE);
                    passwordLayout.setVisibility(View.GONE);
                    ivMode.setImageResource(R.drawable.ic_svg_face3);
                    tvModeDes.setText("请刷卡，刷卡后进入人脸识别");
                    break;
                case CARD_FACE_PASSWORD:
                    tvModeDes.setText("刷卡后进入人脸识别，确认后请输入密码");
                    cardOrFaceInfo.setVisibility(View.GONE);
                    previewHolder.setVisibility(View.GONE);
                    llShowUser.setVisibility(View.GONE);
                    llShowFace.setVisibility(View.VISIBLE);
                    passwordLayout.setVisibility(View.VISIBLE);
                    CameraPreviewManager.getInstance().stopPreview();
                    clearAndCreateTextureView();
                    break;
                case CARD_PASSWORD:
                    cardOrFaceInfo.setVisibility(View.GONE);
                    tvModeDes.setVisibility(View.VISIBLE);
                    tvModeDes.setText("刷卡后请输入用户密码");
                    previewHolder.setVisibility(View.GONE);
                    llShowUser.setVisibility(View.GONE);
                    llShowFace.setVisibility(View.VISIBLE);
                    passwordLayout.setVisibility(View.VISIBLE);
                    CameraPreviewManager.getInstance().stopPreview();
                    clearAndCreateTextureView();
                    break;
                case FACE_PASSWORD:
                    cardOrFaceInfo.setVisibility(View.GONE);
                    tvModeDes.setVisibility(View.GONE);
                    ivMode.setVisibility(View.GONE);
                    previewHolder.setVisibility(View.VISIBLE);
                    llShowUser.setVisibility(View.GONE);
                    llShowFace.setVisibility(View.VISIBLE);
                    passwordLayout.setVisibility(View.VISIBLE);
                    CameraPreviewManager.getInstance().stopPreview();
                    startFace();
                    break;
                default:
                    checkCode();
                    break;
            }
        } catch (NumberFormatException e) {
            checkCode();
        }
    }

    private void checkCode() {
        code = SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE, "1");
        llShowUser.setVisibility(View.GONE);
        llShowFace.setVisibility(View.GONE);
//        llShowFaceMiddle.setVisibility(View.GONE);
        cardOrFaceInfo.setVisibility(View.GONE);

        String[] codes = code.split("-");
        List<String> list = Arrays.asList(codes);
        if (list.contains("0")) {
            ivMode.setVisibility(View.VISIBLE);
            tvModeDes.setVisibility(View.VISIBLE);
            cardOrFaceInfo.setVisibility(View.GONE);
            llShowFace.setVisibility(View.VISIBLE);
            ivMode.setImageResource(R.drawable.ic_svg_card);
            tvModeDes.setText("请刷卡");
            previewHolder.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.GONE);
        }

        if (list.contains("1")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
            cardOrFaceText.setText("人脸识别");
        }

        if (list.contains("2")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
            cardOrFaceText.setText("二维码");
        }

        if (list.contains("3")) {
            ivMode.setVisibility(View.VISIBLE);
            tvModeDes.setVisibility(View.VISIBLE);
            cardOrFaceInfo.setVisibility(View.GONE);
            ivMode.setImageResource(R.drawable.ic_svg_face3);
            llShowFace.setVisibility(View.VISIBLE);
            tvModeDes.setText("请输入密码");
            previewHolder.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.VISIBLE);
        }

        if (list.contains("0") && list.contains("1")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
            cardOrFaceText.setText("刷卡或人脸");
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
        }
        if (list.contains("0") && list.contains("2")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
            cardOrFaceText.setText("刷卡或二维码");
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
        }
        if (list.contains("0") && list.contains("3")) {
            llShowFace.setVisibility(View.VISIBLE);
            ivMode.setImageResource(R.drawable.ic_svg_card);
            ivMode.setVisibility(View.VISIBLE);
            tvModeDes.setText("刷卡或输入密码");
            previewHolder.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.VISIBLE);
        }


        if (list.contains("1") && list.contains("2")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
            cardOrFaceText.setText("人脸或二维码");
        }
        if (list.contains("1") && list.contains("3")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
        }

        if (list.contains("2") && list.contains("3")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
        }


        if (list.contains("0") && list.contains("1") && list.contains("2")) {
            cardOrFaceInfo.setVisibility(View.VISIBLE);
            cardOrFaceText.setText("卡或人脸或二维码");
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.VISIBLE);
        }
        if (list.contains("0") && list.contains("2") && list.contains("3")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
        }
        if (list.contains("0") && list.contains("3") && list.contains("1")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
        }

        if (list.contains("1") && list.contains("2") && list.contains("3")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
        }


        if (list.contains("0") && list.contains("1") && list.contains("2") && list.contains("3")) {
            ivMode.setVisibility(View.GONE);
            tvModeDes.setVisibility(View.GONE);
            cardOrFaceInfo.setVisibility(View.GONE);
            previewHolder.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
        }
        if (list.contains("1") || list.contains("2")) {
            startFace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "Resume");
        projectType = (byte) SPUtils.getInstance().getInt(Constants.PROJECT_TYPE, Constants.PROJECT_JIANGSHENG);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopFace();
            user = null;
            tvUserName.setText("无人脸");
            ivFace.setImageResource(R.drawable.ic_face_en);
            mainActivity.setHomeView(false);
            mainActivity.setHelpView(false);
            mainActivity.setUpgradeView(true);
            MyApplication.getAppContext().unregisterReceiver(viewChangeReceviver);
        } else {
            IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_RECEIVER_CHANGEVIEW);
            MyApplication.getAppContext().registerReceiver(viewChangeReceviver, intentFilter);
            checkMode();
            mainActivity.setHomeView(true);
            mainActivity.setHelpView(true);
            mainActivity.setUpgradeView(false);
            projectType = (byte) SPUtils.getInstance().getInt(Constants.PROJECT_TYPE, Constants.PROJECT_JIANGSHENG);
        }
    }

    public void clearAndCreateTextureView() {
//        Gpio.Set_Led(0);
        previewHolder.removeAllViews();
        previewview = null;
        textureview = null;
        previewview = new AutoTexturePreviewView(mainActivity);
        textureview = new TextureView(mainActivity);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        previewview.setLayoutParams(layoutParams);
        textureview.setLayoutParams(layoutParams);
        textureview.setOpaque(false);
        previewHolder.addView(previewview);
        previewHolder.addView(textureview);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Gpio.Set_Led(0);
        super.onDestroy();
    }

    public void onNoFace() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llShowUser.setVisibility(View.GONE);
                if (!isNoface) {
                    isNoface = true;
                }
                user = null;
            }
        });

    }

    public void haveCardNo(String cardNo) {
        Log.d("HomeFragment+cardNo", cardNo);
        User user = FaceApi.getInstance().getUserByCardNo(cardNo);
        switch (SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE)) {
            case CARD_FACE:
                this.cardNo = cardNo;
                if (!isStartFace) {
                    if (user != null) {
                        previewHolder.setVisibility(View.VISIBLE);
                        llShowFace.setVisibility(View.GONE);
                        startFace();
                        countDate();
                    } else {
                        if ((SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1) && deviceCtrlMode == 0) {
                            SpeechX.getInstance().speak("没有权限");
                            ClientUtil.sendOpenRecord("", 40, 1, cardNo);
                        }
                    }
                }
                break;
            case CARD_PASSWORD:
                this.cardNo = cardNo;
                if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) == 1) {
                    outputCardNo(cardNo);
                }
                passwordLayout.setVisibility(View.VISIBLE);
                if ((SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                    SpeechX.getInstance().speak("请输入密码");
                }
                break;
            case CARD_FACE_PASSWORD:
                this.cardNo = cardNo;
                if (!isStartFace) {
                    previewHolder.setVisibility(View.VISIBLE);
                    llShowFace.setVisibility(View.GONE);
                    startFace();
                }
                break;
            default:
                code = SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE);
                String[] codes = SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE).split("-");
                List<String> list = Arrays.asList(codes);
                if (list.contains("0")) {
                    this.cardNo = cardNo;
                    outputCardNo(cardNo, true);
                    if (user != null) {
                        if ((SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                            SpeechX.getInstance().speak("欢迎");
                        }
                        mainActivity.sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_OPEN_DOOR));
                        ClientUtil.sendOpenRecord(user.getUserId(), 40, 0, user.getCard());
                    } else {
                        if ((SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                            SpeechX.getInstance().speak("没有权限");
                        }
                        handle.postDelayed(() -> outputCardNo(cardNo), 100);
                        ClientUtil.sendOpenRecord("", 40, 1, cardNo);
                    }
                }
                break;
        }
    }

    private void countDate() {
        handle.postDelayed(task, 15000);
    }

    public void dealPassword() {
        mainActivity.outBeer();
        xKeyBoard2.setDisOrder();
        if (TextUtils.isEmpty(sb)) {
            return;
        }
        String s = null;
        try {
            s = Long.toHexString(Long.parseLong(sb.toString())).toUpperCase();
        } catch (Exception e) {
            ToastUtils.showLongToast("按键过多");
        }
        User isCardUser = FaceApi.getInstance().getUserByCardNo(s);
        if (isCardUser != null) {
//            输入是卡号
            outputCardNo(s);
            ClientUtil.sendOpenRecord(isCardUser.getUserId(), 40, 0, s);
            sb.delete(0, sb.length());
            xKeyBoard2.setDisOrder();
        } else {
//            输入不是卡号
            flag = true;
            //设备控制模式，1为外接控制模式
            if (deviceCtrlMode == 1) {
                //设备的输出模式，0为韦根输出模式，1为485输出模式
                if (deviceOutputMode == 0) {
                    //韦根
                    outputPassword((byte) 0x0B);
                    sb.delete(0, sb.length());
                } else {
                    //485
                    outputPassword(sb.toString());
                    sb.delete(0, sb.length());
                }
            } else {
                String holdPassword = SPUtils.getInstance().getString(Constants.DEVICE_HOLD_PASSWORD, "");
                String passwordholder = SPUtils.getInstance().getString(Constants.DEVICE_COMMON_PASSWORD, "");
                LogUtils.d("SerialPortModuleX", "sb" + sb.toString() + "," + holdPassword);
                if (holdPassword.equals(sb.toString())) {
                    LogUtils.d("SerialPortModuleX", "挟持密码：" + holdPassword);
                    handle.postDelayed(() -> {
                        if (SPUtils.getInstance().getInt(Constants.DEVICE_ALARM, 1) == 1) {
                            mainActivity.outputHoldWarm(true);
                            ClientUtil.sendEventRecord(3);
                            ClientUtil.sendOpenRecord("", 45, 0, "");
                            sb.delete(0, sb.length());
                        }
                    }, 500);
                    ToastUtils.showLongToast("如想使用房号拨打通话功能,请按左上角拨号按扭");
                } else if (passwordholder.equals(sb.toString())) {
                    LogUtils.d("SerialPortModuleX", "公共密码：" + holdPassword);
                    outputCardNo("");
                    ClientUtil.sendOpenRecord("", 44, 0, "");
                    sb.delete(0, sb.length());
                } else {
                    User user = FaceApi.getInstance().getUserByCardNo(cardNo);
                    if (user != null) {
                        switch (mode) {
                            case CARD_PASSWORD:
                                faceHolderpassword = user.getPwd();
                                checkPassoword(user, 47);
                                break;
                            case CARD_FACE_PASSWORD:
                                checkPassoword(user, 48);
                                break;
                            case FACE_PASSWORD:
                                checkPassoword(user, 49);
                                break;
                        }
                    } else {
                        if (!noSameCard && (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                            SpeechX.getInstance().speak("没有权限");
                            ToastUtils.showLongToast("如想使用房号拨打通话功能,请按左上角拨号按扭");
                        }
                        ClientUtil.sendOpenRecord("", 40, 1, "");
                    }
                    sb.delete(0, sb.length());
                }
                if ("13".equals(mode)) {
                    if (!TextUtils.isEmpty(cardNo)) {
                        countDate();
                    }
                }
                sb.delete(0, sb.length());
            }
        }
        sb.delete(0, sb.length());
    }

    public void checkPassoword(User user, int modecode) {
        if (user != null) {
            if (faceHolderpassword.equals(sb.toString())) {
                outputCardNo(cardNo);
                if (!noSameCard && (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                    SpeechX.getInstance().speak("欢迎");
                    cardNo = "";
                    ClientUtil.sendOpenRecord(user.getUserId(), modecode, 0, user.getCard());
                }
                sb.delete(0, sb.length());
            } else {
                if (!noSameCard && (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                    SpeechX.getInstance().speak("密码错误");
                    ToastUtils.showLongToast("如想使用房号拨打通话功能,请按左上角拨号按扭");
                }
                ClientUtil.sendOpenRecord(user.getUserId(), modecode, 1, user.getCard());
                sb.delete(0, sb.length());
            }
        } else {
            if (!noSameCard && (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                SpeechX.getInstance().speak("没有权限");
                ToastUtils.showLongToast("如想使用房号拨打通话功能,请按左上角拨号按扭");
            }
            ClientUtil.sendOpenRecord("", modecode, 1, "");
            sb.delete(0, sb.length());
        }
        cardNo = "";
    }

    private void outputCardNo(String dataCardNo) {
        if (!checkTimearea()){
            ToastUtils.showLongToast("输入无效,不在时区内");
            return;
        }
        if (TextUtils.isEmpty(dataCardNo)) {
            mainActivity.sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_OPEN_DOOR));
        } else {
            if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) == 1) {
                handle.postDelayed(() -> mainActivity.outputCardNo(dataCardNo), 100);
            } else {
                mainActivity.sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_OPEN_DOOR));
            }
        }
    }

    private boolean checkTimearea() {
        Time time = FacexDatabase.getInstance(getActivity()).getTimeDao().getTime();
        if (time == null){
            return  true;
        }
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                boolean sun1 = checkTime(time.getSun1Fr(), time.getSun1To());
                boolean sun2 = checkTime(time.getSun2Fr(),time.getSun2To());
                boolean sun3 = checkTime(time.getSun3Fr(),time.getSun3To());
                boolean sun4 = checkTime(time.getSun4Fr(),time.getSun4To());
                return  sun1 || sun2 || sun3 || sun4 ;
            case 2:
                boolean mon1 = checkTime(time.getMon1Fr(), time.getMon1To());
                boolean mon2 = checkTime(time.getMon2Fr(),time.getMon2To());
                boolean mon3 = checkTime(time.getMon3Fr(),time.getMon3To());
                boolean mon4 = checkTime(time.getMon4Fr(),time.getMon4To());
                return  mon1 || mon2 || mon3 || mon4 ;
            case 3:
                boolean tue1 = checkTime(time.getTue1Fr(), time.getTue1To());
                boolean tue2 = checkTime(time.getTue2Fr(),time.getTue2To());
                boolean tue3 = checkTime(time.getTue3Fr(),time.getTue3To());
                boolean tue4 = checkTime(time.getTue4Fr(),time.getTue4To());
                return  tue1 || tue2 || tue3 || tue4 ;
            case 4:
                boolean wed1 = checkTime(time.getWed1Fr(), time.getWed1To());
                boolean wed2 = checkTime(time.getWed2Fr(),time.getWed2To());
                boolean wed3 = checkTime(time.getWed3Fr(),time.getWed3To());
                boolean wed4 = checkTime(time.getWed4Fr(),time.getWed4To());
                return  wed1 || wed2 || wed3 || wed4 ;
            case 5:
                boolean thu1 = checkTime(time.getThu1Fr(), time.getThu1To());
                boolean thu2 = checkTime(time.getThu2Fr(),time.getThu2To());
                boolean thu3 = checkTime(time.getThu3Fr(),time.getThu3To());
                boolean thu4 = checkTime(time.getThu4Fr(),time.getThu4To());
                return  thu1 || thu2 || thu3 || thu4 ;
            case 6:
                boolean fri1 = checkTime(time.getFri1Fr(),time.getFri1To());
                boolean fri2 = checkTime(time.getFri2Fr(),time.getFri2To());
                boolean fri3 = checkTime(time.getFri3Fr(),time.getFri3To());
                boolean fri4 = checkTime(time.getFri4Fr(),time.getFri4To());
                return  fri1 || fri2 || fri3 || fri4 ;
            case 7:
                boolean sat1 = checkTime(time.getSat1Fr(), time.getSat1To());
                boolean sat2 = checkTime(time.getSat2Fr(),time.getSat2To());
                boolean sat3 = checkTime(time.getSat3Fr(),time.getSat3To());
                boolean sat4 = checkTime(time.getSat4Fr(),time.getSat4To());
                return  sat1 || sat2 || sat3 || sat4 ;
            default:
                return false;
        }
    }
    public boolean checkTime(String from,String to){
        if (from.isEmpty() && to.isEmpty()){
            return  false;
        }
        Calendar cal = Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        int fh = Integer.parseInt(from.split(":")[0]);
        int fm = Integer.parseInt(from.split(":")[1]);
        int th = Integer.parseInt(to.split(":")[0]);
        int tm = Integer.parseInt(to.split(":")[1]);
        if (h > fh){
            if (h < th ){
                return true;
            }
            if (h == th  && m < tm){
                return true;
            }
        }
        if (h == fh &&  m > fm){
            if (h < th ){
                return true;
            }
            if (h == th  && m < tm){
                return true;
            }

        }
        return  false;
    }


    private void outputCardNo(String dataCardNo, boolean isOutDoor) {
        if (isOutDoor)
            handle.postDelayed(() -> outputCardNo(dataCardNo), 100);
        else
            mainActivity.sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_OPEN_DOOR));
    }

    public void startFace() {
        isStartFace = true;
        if (mainActivity.blackScreen.getVisibility() == View.VISIBLE) {
            Gpio.Set_Led(1);
        } else {
            clearAndCreateTextureView();
            if (!ClientUtil.isOnCall) {
                Gpio.Set_Led(1);
            }
//            faceManager.initFaceIdentity(previewview, textureview, MyApplication.getAppContext(), HomeFragment.this, mode, mainActivity.blackScreen);
            initView();
        }
    }

    public void stopFace() {
        isStartFace = false;
        CameraPreviewManager.getInstance().stopPreview();
        clearAndCreateTextureView();
        Gpio.Set_Led(0);
    }

    public void startFaceIdenty() {
        checkMode();
    }


    private void initView() {
        // 活体状态
        mLiveType = 2;
        // 活体阈值
        float mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // 获取整个布局
        LinearLayout relativeLayout = rootView.findViewById(R.id.all_relative);
        // 画人脸框
        paint = new Paint();
        rectF = new RectF();
        if (textureview == null) {
            return;
        }
        mFaceDetectImageView = textureview;
        mFaceDetectImageView.setOpaque(false);
        mFaceDetectImageView.setKeepScreenOn(true);


        // 单目摄像头RGB 图像预览
        mAutoCameraPreviewView = previewview;
        mAutoCameraPreviewView.setVisibility(View.VISIBLE);


        // 屏幕的宽
        int displayWidth = DensityUtils.getDisplayWidth(getActivity());
        // 屏幕的高
        int displayHeight = DensityUtils.getDisplayHeight(getActivity());
        // 当屏幕的宽大于屏幕宽时
        if (displayHeight < displayWidth) {
            // 获取高
            int height = displayHeight;
            // 获取宽
            int width = (int) (displayHeight * ((9.0f / 16.0f)));
            // 设置布局的宽和高
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            // 设置布局居中
            params.gravity = Gravity.CENTER;
            relativeLayout.setLayoutParams(params);
        }
        startTestCloseDebugRegisterFunction();
    }

    //开始人脸识别
    private void startTestCloseDebugRegisterFunction() {
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);
        CameraPreviewManager.getInstance().startPreview(getActivity(), mAutoCameraPreviewView,
                PREFER_WIDTH, PERFER_HEIGH, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                        if (mainActivity.blackScreen.getVisibility() != View.VISIBLE) {

                            switch (mode) {
                                case CARD_FACE:
                                case CARD_FACE_PASSWORD:
                                case FACE_PASSWORD:
                                    FaceSDKManager.getInstance().onDetectCheck(data, null, null,
                                            height, width, mLiveType, new FaceDetectCallBack() {
                                                @Override
                                                public void onFaceDetectCallback(LivenessModel livenessModel) {
                                                    // 输出结果
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (livenessModel == null) {
                                                                onNoFace();
                                                            } else {
                                                                checkCloseResult(livenessModel);
                                                            }
                                                        }
                                                    }).start();
                                                }

                                                @Override
                                                public void onTip(int code, String msg) {
                                                }

                                                @Override
                                                public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                                                    showFrame(livenessModel);
                                                }
                                            });
                                    break;
                                default:
                                    String[] codes = mode.split("-");
                                    List<String> list = Arrays.asList(codes);
                                    if (list.contains("2") && list.contains("1")) {
                                        FaceSDKManager.getInstance().onDetectCheck(data, null, null,
                                                height, width, mLiveType, new FaceDetectCallBack() {
                                                    @Override
                                                    public void onFaceDetectCallback(LivenessModel livenessModel) {
                                                        // 输出结果
//                                                        new Thread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
                                                        if (livenessModel == null) {
                                                            onNoFace();
                                                        } else {
                                                            checkCloseResult(livenessModel);
                                                        }
                                                        checkQr(toGrayscale(getBitmapImageFromYUV(data, width, height)));
//                                                            }
//                                                        }).start();
                                                    }

                                                    @Override
                                                    public void onTip(int code, String msg) {
                                                    }

                                                    @Override
                                                    public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                                                        showFrame(livenessModel);
                                                    }
                                                });
                                    } else if (list.contains("1")) {
                                        FaceSDKManager.getInstance().onDetectCheck(data, null, null,
                                                height, width, mLiveType, new FaceDetectCallBack() {
                                                    @Override
                                                    public void onFaceDetectCallback(LivenessModel livenessModel) {
                                                        // 输出结果
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (livenessModel == null) {
                                                                    onNoFace();
                                                                } else {
                                                                    checkCloseResult(livenessModel);
                                                                }
                                                            }
                                                        }).start();
                                                    }

                                                    @Override
                                                    public void onTip(int code, String msg) {
                                                    }

                                                    @Override
                                                    public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                                                        showFrame(livenessModel);
                                                    }

                                                });
                                    } else if (list.contains("2")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                checkQr(toGrayscale(getBitmapImageFromYUV(data, width, height)));
                                            }
                                        }).start();
                                    }
                                    break;
                            }
                        } else {
                            if (SPUtils.getInstance().getInt("DEVICE_WAKE_MODE", 1) == 1) {
                                frameCount += 1;
                                if (frameCount % 15 == 0) {
                                    Bitmap bitmap = toGrayscale(getBitmapImageFromYUV(data, width, height));
                                    if (oldBitmap == null) {
                                        oldBitmap = bitmap;
                                    } else {
                                        int similarity = similarity(oldBitmap, bitmap);
                                        int similarity1 = getSimilarity(lastSimilarity, similarity);
                                        if (lastSimilarity == 0) {
                                            similarity1 = 0;
                                        }

                                        Log.d("MainActivity", "similarity1:" + similarity1);
                                        if (similarity1 >= 10) {
                                            getContext().sendBroadcast(new Intent("OPENSCREEN"));
                                            oldBitmap = null;
                                            lastSimilarity = 0;
                                        } else {
                                            oldBitmap = bitmap;
                                            lastSimilarity = similarity;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void checkCloseResult(final LivenessModel livenessModel) {
        // 当未检测到人脸UI显示
        MainActivity.resetOperateTime();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel == null) {
                    return;
                }
                if (user != null && livenessModel.getUser() != null) {
                    if (user.getCard() != null && livenessModel.getUser().getCard() != null) {
                        if (user.getCard().equals(livenessModel.getUser().getCard())) {
                            return;
                        } else {
                            user = livenessModel.getUser();
                        }
                    } else {
                        return;
                    }
                } else {
                    user = livenessModel.getUser();
                }

                llShowUser.setVisibility(View.VISIBLE);
                if (user != null) {
                    dataCardNo = user.getCard();
                    File faceDir = FileUitls.getFaceDirectory();
                    if (faceDir != null && faceDir.exists()) {
                        File file = new File(faceDir, user.getImageName());
                        if (file.exists()) {
                            if (getActivity() != null) {
                                GlideUtils.displayCropCircle(getActivity(), ivFace, file);
                                tvUserName.setText(user.getUserName());
                                switch (mode) {
                                    case CARD_FACE:
                                        if (cardNo.equals(dataCardNo)) {
                                            outputCardNo(dataCardNo);
                                            ClientUtil.sendOpenRecord(user.getUserId(), 46, 0, cardNo);
                                            cardNo = "";
                                            noSameCard = false;
                                        } else {
                                            noSameCard = true;
                                        }
                                        if (!noSameCard && (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                                            SpeechX.getInstance().speak("欢迎");
                                        }
                                        break;
                                    case CARD_FACE_PASSWORD:
                                        if (cardNo.equals(dataCardNo)) {
                                            if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) == 1) {
                                                outputCardNo(dataCardNo);
                                            }
                                            if ((SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                                                SpeechX.getInstance().speak("请输入密码");
                                            }
                                            faceHolderpassword = user.getPwd();
                                            noSameCard = false;
                                        } else {
                                            noSameCard = true;
                                        }
                                        break;
                                    case FACE_PASSWORD:
                                        noSameCard = false;
                                        if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) == 1) {
                                            outputCardNo(dataCardNo);
                                        }
                                        if ((SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                                            SpeechX.getInstance().speak("请输入密码");
                                        }
                                        faceHolderpassword = user.getPwd();
                                        cardNo = dataCardNo;
                                        break;
                                    default:
                                        outputCardNo(dataCardNo);
                                        ClientUtil.sendOpenRecord(user.getUserId(), 41, 0, dataCardNo);
                                        if (!noSameCard && (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                                            SpeechX.getInstance().speak("欢迎");
                                        }
                                        cardNo = "";
                                        break;
                                }
                            }
                        }
                    } else {
                        ivFace.setImageResource(R.drawable.ic_face_en);
                        ClientUtil.sendOpenRecord("", 41, 1, "");
                        tvUserName.setText("没有权限");
                    }
                } else {
                    ivFace.setImageResource(R.drawable.ic_face_en);
                    tvUserName.setText("没有权限");
                }
                if (mode.equals(CARD_FACE) || mode.equals(CARD_FACE_PASSWORD)) {
                    xKeyBoard2.setDisOrder();
                    handle.removeCallbacks(task);
                    llShowFace.setVisibility(View.VISIBLE);
                    CameraPreviewManager.getInstance().stopPreview();
                    clearAndCreateTextureView();
                    isStartFace = false;
                    previewHolder.setVisibility(View.GONE);
                    if (noSameCard) {
                        tvUserName.setTextColor(getResources().getColor(R.color.red));
                        tvUserName.setText("卡与人脸数据不一致");
                        if (noSameCard && (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1)) {
                            SpeechX.getInstance().speak("数据不一致");
                            if (mode.equals(CARD_FACE))
                                ClientUtil.sendOpenRecord(user.getUserId(), 46, 1, dataCardNo);
                            if (mode.equals(CARD_FACE_PASSWORD))
                                ClientUtil.sendOpenRecord(user.getUserId(), 48, 1, dataCardNo);
                        }
                    }
                    handle.postDelayed(() -> {
                        tvUserName.setTextColor(getResources().getColor(R.color.black));
                        llShowUser.setVisibility(View.GONE);
                    }, 3000);
                }
            }
        });
    }

    private void showFrame(final LivenessModel model) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = mFaceDetectImageView.lockCanvas();
                if (canvas == null) {
                    mFaceDetectImageView.unlockCanvasAndPost(canvas);
                    return;
                }
                if (model == null) {
                    // 清空canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mFaceDetectImageView.unlockCanvasAndPost(canvas);
                    return;
                }
                FaceInfo[] faceInfos = model.getTrackFaceInfo();
                if (faceInfos == null || faceInfos.length == 0) {
                    // 清空canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mFaceDetectImageView.unlockCanvasAndPost(canvas);
                    return;
                }
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                FaceInfo faceInfo = faceInfos[0];

                rectF.set(FaceOnDrawTexturViewUtil.getFaceRectTwo(faceInfo));
                // 检测图片的坐标和显示的坐标不一样，需要转换。
                FaceOnDrawTexturViewUtil.mapFromOriginalRect(rectF,
                        mAutoCameraPreviewView, model.getBdFaceImageInstance());
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                // 绘制框
                canvas.drawRect(rectF, paint);
                mFaceDetectImageView.unlockCanvasAndPost(canvas);
            }
        });
    }

    public void onResponseQr(String cardNo) {
        User user = FaceApi.getInstance().getUserByCardNo(cardNo);
        if (user != null) {
            outputCardNo(cardNo);
            stopFace();
            startFace();

            if (SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE) == 1 && deviceCtrlMode == 0) {
                SpeechX.getInstance().speak("欢迎");
            }
            ClientUtil.sendOpenRecord(user.getUserId(), 42, 0, user.getCard());
        } else {
            ClientUtil.sendOpenRecord("", 42, 1, cardNo);
        }
    }

    private void checkQr(Bitmap bitmap) {
        Result result = decodeQR(bitmap);
        if (result != null) {
            String s1 = DataProcessUtil.byteArraytoHex(result.getText().getBytes(StandardCharsets.UTF_8));
            s1 = s1.replace("C2", "").replace("C3", "");
            s1 = s1.substring(32, 40);
            // // log.d("HomeFragment", s1);
            onResponseQr(s1);
        }
    }

    public int getSimilarity(int old, int news) {
        if (old > news) {
            return old - news;
        } else {
            if (news - old > 60) {
                return 0;
            } else {
                return news - old;
            }
        }
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            isStartFace = false;
            CameraPreviewManager.getInstance().stopPreview();
            clearAndCreateTextureView();
            previewHolder.setVisibility(View.GONE);
            llShowFace.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
        }
    }

    private class Task2 implements Runnable {
        @Override
        public void run() {
            flag = true;
            dealPassword();
        }
    }

}
