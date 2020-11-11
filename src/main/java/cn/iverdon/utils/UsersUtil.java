package cn.iverdon.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.ProtoCommon;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 * @author iverdon
 * @date 2020/10/30 15:56
 */
public class UsersUtil {

    private static String nginxHost = "http://114.55.255.42/group1/";

    public static String getTokenIn(String fileName) {
        int ts = (int)Instant.now().getEpochSecond();
        String token = null;
        try {
            token = ProtoCommon.getToken(fileName,ts,"FastDFS1234567890");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        StringBuilder sb =new StringBuilder();
        sb.append(nginxHost).append(fileName)
                .append("?token=").append(token)
                .append("&ts=").append(ts);
        return sb.toString();
    }
}
