package com.baidu.idl.face.main;


import android.util.Log;

import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.face.main.model.Group;
import com.baidu.idl.face.main.model.User;
import com.baidu.idl.main.facesdk.utils.FileUitls;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceManager {
    private static ExecutorService es = Executors.newSingleThreadExecutor();


    private static FaceManager faceManager = null;

    private FaceManager() {
    }


    public static FaceManager getFaceManager() {
        if (faceManager != null) {
            return faceManager;
        } else {
            return new FaceManager();
        }
    }


//    public boolean initFaceRegitster(String userName, String groupName, Bitmap bitmap) {
//        FaceRegitster faceRegitster = new FaceRegitster();
//        return faceRegitster.register(username, userid, bitmap);
//        return true;
//    }


    public static void deleteFace(String userId) {
        List<User> facex = FaceApi.getInstance().getUserList("facex");
        for (User user : facex) {
            if (user.getUserId().equals(userId)) {
                if (user != null) {
                    File faceDir = FileUitls.getFaceDirectory();
                    File file = new File(faceDir, user.getImageName());
                    if (file != null)
                        file.delete();
                    user.setFeature(null);
                    user.setImageName(null);
                    FaceApi.getInstance().userUpdate(user);
                } else {
                    Log.e("facetext", "不存在用户");
                    return;
                }
            }
        }


    }


    public static void clearAllAsync() {
        FaceApi.getInstance().groupDelete("facex");
        File faceDir = FileUitls.getFaceDirectory();
        RecursionDeleteFile(faceDir);
        Group group = new Group();
        group.setGroupId("facex");
        boolean ret = FaceApi.getInstance().groupAdd(group);
        Log.e("clear", ret + "");
        FaceApi.getInstance().initDatabases(true);
        FileUitls.getFaceDirectory();
    }


    private static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }




    public static int getCount() {
        List<Group> facex1 = FaceApi.getInstance().getGroupListByGroupId("facex");

        if (facex1 != null) {
            int count = 0;
            List<User> facex = FaceApi.getInstance().getUserList("facex");
            if (facex != null) {
                if (facex.size() != 0) {
                    for (User user : facex) {
                        count += 1;
                    }
                    return count;
                } else {
                    return 0;
                }
            }else{
                return 0;
            }
        } else {
            return 0;
        }
    }
}
