package online.elves.utils;

import com.google.common.base.Charsets;

import java.security.MessageDigest;

/**
 * 加解密工具.
 */
public class EncryptUtil {

    /**
     * MD5 加密
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(Charsets.UTF_8));

            byte[] byteArray = messageDigest.digest();
            StringBuffer md5StrBuff = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
            return md5StrBuff.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
