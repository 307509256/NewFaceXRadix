package com.baidu.idl.face.main.common;

/**
 * @PackageName: com.yxdz.facex.common
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/16 14:17
 */
public interface Constants {


    //
    String CAMERA_MODE = "camera_mode";
    String DEVICE_OPEATOR_MODE = "device_mode";
    String DEVICE_COMPOSITE_CODE = "composite_code";
    String DEVICE_OUTPUT_MODE = "output_mode";


    //用于config信息存储与配置，sp
    String SERVER_ADDRESS="SERVER_ADDRESS";
    String DEFAULT_SERVER_ADDRESS="192.168.10.1";
    String SERVER_ADDRESS_PORT="SERVER_ADDRESS_PORT";
    String DEFAULT_SERVER_ADDRESS_PORT="18088";
    String SERVER_ADDRESS_IMAGE_PORT="SERVER_ADDRESS_IMAGE_PORT";
    String DEFAULT_SERVER_ADDRESS_IMAGE_PORT="18088";


    String DEFAULT_AREA="/surpass/";

    String DEVICE_NAME="DEVICE_NAME";
    String DEFAULT_DEVICE_NAME="Face Device";

    String DEVICE_SN="DEVICE_SN";
    String DEFAULT_DEVICE_SN="";

    String DISPLAY_CONTENT="DISPLAY_CONTENT";
    String DEFAULT_DISPLAY_CONTENT="Hello";

    String TTS_MODE_CONTENT="TTS_MODE_CONTENT";
    String DEFAULT_TTS_MODE_CONTENT="Hello";

    String DEVICE_ACCESS_PASSWORD="DEVICE_ACCESS_PASSWORD";
    String DEFAULT_DEVICE_ACCESS_PASSWORD="123456";


    String DEVICE_HOLD_PASSWORD="DEVICE_HOLD_PASSWORD";

    String DEVICE_AD="DEVICE_AD";
    String DEVICE_NOTIC="DEVICE_NOTIC";


    String TTS_MODE="TTS_MODE";
    int DEFAULT_TTS_MODE=1;

    String DOOR_DALAY_TIME_FOR_CLOSE="DOOR_DALAY_TIME_FOR_CLOSE";
    //单位s
    int DEFAULT_DOOR_DALAY_TIME_FOR_CLOSE=2;

    String DOOR_WEIGEN="DOOR_WEIGEN";
    int DEFAULT_DOOR_WEIGEN=32;

    String FACE_MATCHING_SPEED="FACE_MATCHING_SPEED";
//    人脸识别速度，1：低、2：中、3：快
    int DEFAULT_FACE_MATCHING_SPEED=1;

    String FACE_MATCHING_THRESHOLD="FACE_MATCHING_THRESHOLD";
//    人脸识别容错率,默认0.01%，10%、1%、0.1%、0.001%%、0.0001%
    String DEFAULT_FACE_MATCHING_THRESHOLD="0.01%";

    String FACE_DUPLICATE="FACE_DUPLICATE";
//    0不重复，1可重复
    int DEFAULT_FACE_DUPLICATE=0;

    String OPEATE_TIME_OUT="OPEATE_TIME_OUT";
//    在限定时间内，用户没有操作就返回主界面（设置范围60-1800s）,默认120秒
    int DEFAULT_OPEATE_TIME_OUT=120;

    String DEVICE_MODE="DEVICE_MODE";
//    设备类型，1：室内机，2：多功能人脸识别终端
    int DEFAULT_DEVICE_MODE=1;


    String TO_SN = "toSn";
    String FROM_SN = "fromSn";

    int DEVICE_SERVER_PORT=8082;
    String DEVICE_SERVER_IP="DEVICE_SERVER_IP";
    String BROADCAST_RECEIVER_WEB_START="yxdz.web.server.start";
    String BROADCAST_RECEIVER_WEB_STOP="yxdz.web.server.stop";
    String BROADCAST_RECEIVER_WEB_EXCEPTION="yxdz.web.server.exception";
    String BROADCAST_RECEIVER_RTC="yxdz.rtc.video";
    String BROADCAST_RECEIVER_OPEN_DOOR="yxdz.web.open";
    String BROADCAST_RECEIVER_CHANGEVIEW="yxdz.web.changeview";
    String BROADCAST_RECEIVER_RESTART_SLAVE="yxdz.web.restart";
    String BROADCAST_RECEIVER_RESTART_READER="yxdz.web.restart.reader";
    String BROADCAST_RECEIVER_SERVER_STATUS="yxdz.web.serverstatus";
    String BROADCAST_RECEIVER_PASSWORD_OPEN_DOOR="yxdz.web.password.open";
    String BROADCAST_RECEIVER_CONFIG="yxdz.web.config.warm";
    String BROADCAST_RECEIVER_RESTART="yxdz.web.config.RESTART";
    String BROADCAST_RECEIVER_STARTLOADFACE ="yxdz.web.black";
    String BROADCAST_RECEIVER_TCP_CONNECTION="yxdz.web.tcp.connection";

    String BROADCAST_RECEIVER_CALL_CONNECT =  "yxdz.web.call";
    String BROADCAST_RECEIVER_UPDATA_NOTIC="yxdz.web.updata.notic";
    String BROADCAST_RECEIVER_NETWORK_NOTIC="yxdz.web.updata.network";
    String DATA_RTC_MESSAGE="RtcMessageBean";


//    String COPYRIGHT="Copyright@新疆康斯佳能源股份有限公司";
    String COPYRIGHT="Copyright@中山市宇信科技有限公司";
//    String COPYRIGHT="Copyright@博慧佳信科技（北京）有限公司";

    int FACE_DELAY_TIME = 3000;
    String CAMERA_ERROR_REMOVE = "CAMERA_ERROR_REMOVE";
    String SLEEP_TIME = "SLEEP_TIME";
    String DEVICE_COMMON_PASSWORD = "DEVICE_COMMON_PASSWORD";
    String DEVICE_SCENE_TYPE = "DEVICE_SCENE_TYPE";
    String DEVICE_CTRL_MODE = "DEVICE_CTRL_MODE";
    String DEVICE_CARD_DECODE = "DEVICE_CARD_DECODE";
    String DEVICE_ALARM = "DEVICE_CARD_DECODE";
    String FACE_LIVENSS = "FACE_LIVENSS";
    String ICE_SERVER_ADDRESS = "ICE_SERVER_ADDRESS";
//    String DEFAULT_ICE_SERVER_ADDRESS = "192.168.10.1";
    String DEFAULT_ICE_SERVER_ADDRESS = "111.230.25.62";
    String ICE_SERVER_ADDRESS_PORT= "ICE_SERVER_ADDRESS_PORT";
    String DEFAULT_ICE_SERVER_ADDRESS_PORT = "3478";
    String SERVER_CONNECT_STATS = "SERVER_CONNECT_STATS";
    String BROADCAST_RECEIVER_OPEN_DOOR_CTRL = "BROADCAST_RECEIVER_OPEN_DOOR_CTRL";
    String PROJECT_TYPE = "project_type";
    int PROJECT_JIANGSHENG=0xee;
    int PROJECT_STANDARD=0xcc;
    int DEFAULT_MODE_US_CONTROL=0;
    String DEFAULT_CARD = "FFFFFFFF";
    String TCP_CONNECTION_FAIL = "TCP_CONNECTION_FAIL";
    String BROADCAST_RECEIVER_NETWORK_FAIL = "BROADCAST_RECEIVER_NETWORK_FAIL";
}