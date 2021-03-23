package com.yxdz.serialport.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * DataProcessUtil
 */

public class DataProcessUtil {


    /**
     * 获取距离当前时间的时间差
     * @param time1
     * @return 秒
     */
    public static long untilNow(Long time1){
        Long nowTime = new Date().getTime();
        return Math.abs((nowTime-time1)/1000);
    }



    public static String byteArraytoHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static String byteArraytoHex2(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            StringBuilder stringBuilder = new StringBuilder(s);
            stringBuilder.insert(s.length()-1,"0");
            s = stringBuilder.toString();
        }
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    /**
     * 十六进制转ASCII
     * @param hexValue
     * @return
     */
    public static String hexToASCII(String hexValue){
        hexValue = hexValue.replace("00", "");
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    /**
     * ASCII转十六进制
     * @param asciiValue
     * @return
     */

    public static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }



    //卡号转换，24,26,32,34
    public static String cardParse(String weigen, String data){
        String card=null;
        byte[] temp=null;
        switch (weigen){
            case "20":
                //32位卡号
                card=addZeroForNumHead(data, 8);
                break;
            case "22":
                //34位卡号
                temp=new byte[5];
                System.arraycopy(data,0,temp,0,5);
                int i = 0;
                int a=temp[0];
                int b=temp[1];
                int c=temp[2];
                int d=temp[3];
                int e=temp[4];
                i = (a << 31);
                i |= (b << 23);
                i |= (c << 15);
                i |= (d << 7);
                i |= (e);
                byte[] result=new byte[4];
                result[3] = (byte) (i & 0xff);
                result[2] = (byte) ((i >> 8) & 0xff);
                result[1] = (byte) ((i >> 16) & 0xff);
                result[0] = (byte) ((i >> 24) & 0xff);
                card= DataProcessUtil.byteArraytoHex(result);
                break;
            case "1A":
                //26位卡号
                temp=new byte[4];
                System.arraycopy(data,0,temp,0,4);
                int j=0;
                int a2=temp[0];
                int b2=temp[1];
                int c2=temp[2];
                int d2=temp[3];
                i = (a2 << 31);
                i |= (b2 << 23);
                i |= (c2 << 15);
                i |= (d2 << 7);
                byte[] result2=new byte[3];
                result2[2] = (byte) ((i >> 8) & 0xff);
                result2[1] = (byte) ((i >> 16) & 0xff);
                result2[0] = (byte) ((i >> 24) & 0xff);
                card= DataProcessUtil.byteArraytoHex(result2);
                break;
            case "18":
                //24位卡号
                card=addZeroForNumHead(data, 8);
                break;
        }
        long cardNoLong= Long.getLong(card);
        return Long.toOctalString(cardNoLong);
    }


    /**
     * 数据卡号去0转换,转换成10进制卡号
     * @param weigen
     * @param data
     * @return
     */
    public static String parseCardNo(String weigen, String data){
        String cardNo=null;
        switch (weigen){
            case "20":
                //32位卡号
            case "22":
                //34位卡号
                cardNo=data.substring(0,8);
                break;
            case "1A":
                //26位卡号
            case "18":
                //24位卡号
                cardNo=data.substring(0,6);
                break;
        }

        //将卡号反转过来
        char temp;
        char[] openArr=cardNo.toCharArray();
        int len=cardNo.length();
        for(int i=0;i<cardNo.length()/2;i++){
            if(i%2==0){
                temp=openArr[i];
                openArr[i]=openArr[len-2-i];
                openArr[len-2-i]=temp;
            }else{
                temp=openArr[i];
                openArr[i]=openArr[len-i];
                openArr[len-i]=temp;
            }
        }
        cardNo= String.valueOf(openArr);
        return Long.toString(Long.parseLong(cardNo,16));
    }


    public static String formatCard(String cardNo){
        String data = removeHeadZero(cardNo);
        String result=null;
        if (data.length()%2!=0){
            result=addZeroForNumHead(data,data.length()+1);
        }else{
            result=data;
        }
        //将卡号反转过来
        char temp;
        char[] openArr=result.toCharArray();
        int len=result.length();
        for(int i=0;i<result.length()/2;i++){
            if(i%2==0){
                temp=openArr[i];
                openArr[i]=openArr[len-2-i];
                openArr[len-2-i]=temp;
            }else{
                temp=openArr[i];
                openArr[i]=openArr[len-i];
                openArr[len-i]=temp;
            }
        }
        result= String.valueOf(openArr);
        return  result;
    }


    public static String parsePassword(String password){
        StringBuffer sb=new StringBuffer();
        char[] chars=password.toCharArray();
        for(int i=1;i<password.length();i+=2){
            sb.append(chars[i]);
        }
        return  sb.toString();
    }

    /***
     * 移除头部的0
     * @param cardNo
     * @return
     */
    public static String removeHeadZero(String cardNo){
        String newStr = cardNo.replaceFirst("^0*", "");
        return newStr;
    }


    /**
     * 时间算法 Byte3 Byte2 Byte1 Byte0 [yyyyyydd] [dddmmmms] [ssssssss][ssssssss]
     *
     * @param time
     * @return
     */
    public static byte[] dateTime2Bytes(Date time) {
        byte[] data = new byte[4];
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTime());

        int y = cal.get(Calendar.YEAR) - 2000;
        int d = cal.get(Calendar.DATE);
        int m = cal.get(Calendar.MONTH) + 1;
        int s = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE)
                * 60 + cal.get(Calendar.SECOND);

        int i = 0;
        i = (y << 26); // 32-6=26 左移26位表示 日的长度+月的长度+秒的长度
        i |= (d << 21);
        i |= (m << 17);
        i |= s;

        data[3] = (byte) (i & 0xff);
        data[2] = (byte) ((i >> 8) & 0xff);
        data[1] = (byte) ((i >> 16) & 0xff);
        data[0] = (byte) ((i >> 24) & 0xff);

        return data;
    }






    /**
     * 补0
     * @param str
     * @param length
     * @return
     */

    public static String addZeroForNum(String str, int length){
        if(str.length()>=length){
            return str.substring(0,length);
        }
        int strLen = str.length() / 2;
        if(strLen < length){
            while (strLen < length){
                StringBuffer sb = new StringBuffer("");
                sb.append(str).append("0");
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }


    public static String addNullStr(String str, int length){
        if(str.length()>=length){
            return str.substring(0,length);
        }
        int strLen = str.length() / 2;
        if(strLen < length){
            while (strLen < length){
                StringBuffer sb = new StringBuffer("");
                sb.append(str).append(" ");
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    public static String addZeroForNumHead(String str, int length){
        if(str.length()>=length){
            return str.substring(0,length);
        }
        int len=length-str.length();
        StringBuffer sb = new StringBuffer("");
        for(int i=0;i<len;i++){
            sb.append("0");
        }
        return sb.append(str).toString();
    }

    /**
     * 数据卡号去0转换,转换成10进制卡号
     * @return
     */
    public static String parseCardNo(String cardNo){
        //将卡号反转过来
        char temp;
        char[] openArr=cardNo.toCharArray();
        int len=cardNo.length();
        for(int i=0;i<cardNo.length()/2;i++){
            if(i%2==0){
                temp=openArr[i];
                openArr[i]=openArr[len-2-i];
                openArr[len-2-i]=temp;
            }else{
                temp=openArr[i];
                openArr[i]=openArr[len-i];
                openArr[len-i]=temp;
            }
        }
        cardNo= String.valueOf(openArr);
        return Long.toString(Long.parseLong(cardNo,16));
    }


    public static String extractCardno(byte[] cardnodata){
        long t_cardno = 0;
        String outdata = "";
        for (int i = cardnodata[0]; i < 15; i++)
        {
            t_cardno <<= 8;
//            t_cardno |= cardnodata[i];
            t_cardno = t_cardno | (((long)cardnodata[i]) & 0xff);
        }
        t_cardno = (t_cardno >> 1);
        t_cardno &= 0xffffff;
        outdata = Long.toString(t_cardno);
        return outdata;
    }






    public static String bytes2cardNo(byte[] data, int offset,int CARD_LENGTH) {
        if (CARD_LENGTH == 16) {
            long longHwCardNo = 0;
            long longDecCardNo = 0;
            longHwCardNo = getValue(data, offset, 3); // 0x20 0AA7
            longDecCardNo = ((longHwCardNo >> 16) & 0xff) * 100000 + (longHwCardNo & 0xffff); // 32
            // 02727
            return Long.toString(longDecCardNo);

        } else if (CARD_LENGTH == 24) {
            long l = getValue(data, offset, 5);
            return Long.toString(l);
        } else {
            System.out.println("系统参数：Device.CardLen 设置不正确，应该为16 or 24");
            return "";
        }
    }
    private static long getValue(byte recv[], int offset, int len) {
        long result = 0;
        for (int i = 0; i < len; i++) {
            result <<= 8;
            result |= (recv[offset + i] & 0xff); // 防止因Java的默认的数据转换带来的问题

        }
        return result;
    }






    public static String toHexString(String s)
    {
        String str="";
        for (int i=0;i<s.length();i++)
        {
            int ch = (int)s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    public static String addZeroForDate(int data){
        StringBuffer sb = new StringBuffer("");
        String dataStr;
        if (data < 10){
            dataStr = sb.append("0").append(String.valueOf(data)).toString();
        }else {
            dataStr = String.valueOf(data);
        }
        return dataStr;
    }

    public static String randomHexString(int len)  {
        StringBuffer result = new StringBuffer();
        String rand="BB";
        try {
            for(int i=0;i<len;i++) {
                result.append(Integer.toHexString(16 + new Random().nextInt(225)));
            }
            rand=result.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return rand;
        }
    }




    public static String getCmdData(String rnd, String cmd, String data) {
        String dataText = data.replace(" ", "");
        String cmdText = cmd.replace(" ", "");
        String rndText = rnd.replace(" ", "");
        byte[] dataArray = hexStringToByteArray(dataText);
        byte[] cmdArray = hexStringToByteArray(cmdText);
        byte[] rndArray = hexStringToByteArray(rndText);
        byte[] check = { cmdArray[0] };
        for (byte b : dataArray) {
            check[0] ^= b;
        }
        check[0] ^= rndArray[0];
        return "AA " + rnd + cmd + data + byteArraytoHex(check) + "DD";
    }

    public static byte[] getCmdDataByteArray(String cmdData) {
        String text = cmdData.replace(" ", "");
        byte[] array = hexStringToByteArray(text);
        return array;
    }


}
