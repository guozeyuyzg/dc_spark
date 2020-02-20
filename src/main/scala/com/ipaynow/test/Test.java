package com.ipaynow.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ipaynow0714
 * @date 2019/1/18
 */

public class Test {
    // 生产 userId = 294
    // 测试 1077




    private Date getDateOfTransDate(String transDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = sdf.parse(transDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    private Date getTimeOfTransDate(String transDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
        try {
            parse = sdf.parse(transDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }








    private static String md5(HashMap<String, String> header, HashMap<String, Object> body, String signKey) {
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>(header);
        if (body != null) {
            treeMap.putAll(body);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : treeMap.keySet()) {
            Object o = treeMap.get(key);
            if (o instanceof String && StringUtils.isNotBlank(o.toString())) {
                stringBuilder.append(key).append("=").append(treeMap.get(key)).append("&");
            } else if (o instanceof List) {
                if (CollectionUtils.isNotEmpty((ArrayList) o)) {
                    stringBuilder.append(key).append("=").append(JSON.toJSONString(o)).append("&");
                }
            }
        }
        stringBuilder.append("key=").append(signKey);
        return md5Sign(stringBuilder.toString()).toLowerCase();
    }

    private static String md5Sign(String str) {

        if (str == null) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return str;
        } catch (UnsupportedEncodingException e) {
            return str;
        }
        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        int aa;
        for (byte b : byteArray) {
            aa = b;
            aa = aa & 0xff;
            if (Integer.toHexString(aa).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(aa));
            else
                md5StrBuff.append(Integer.toHexString(aa));
        }
        return md5StrBuff.toString();
    }


    ///////////////////////////////////////////////////////////  transfer  ///////////////////////////////////////////////////////////
    private static final String KEY_CHARSET = "UTF-8";

    // 算法方式
    private static final String KEY_ALGORITHM = "AES";

    // 算法/模式/填充
    private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";

    // 私钥大小128/192/256(bits)位 即：16/24/32bytes，暂时使用128，如果扩大需要更换java/jre里面的jar包
    private static final Integer PRIVATE_KEY_SIZE_BIT = 128;
    private static final Integer PRIVATE_KEY_SIZE_BYTE = 16;

    public static String encrypt(String secretKey, String plainText) {
        if (secretKey.length() != PRIVATE_KEY_SIZE_BYTE) {
            throw new RuntimeException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
        }

        // 密文字符串
        String cipherText = "";
        try {
            // 加密模式初始化参数
            Cipher cipher = initParam(secretKey, Cipher.ENCRYPT_MODE);
            // 获取加密内容的字节数组
            byte[] bytePlainText = plainText.getBytes(KEY_CHARSET);
            // 执行加密
            byte[] byteCipherText = cipher.doFinal(bytePlainText);
            cipherText = org.apache.commons.codec.binary.Base64.encodeBase64String(byteCipherText);
        } catch (java.lang.Exception e) {
            throw new RuntimeException("AESUtil:encrypt fail!", e);
        }
        return cipherText;
    }

    /**
     * 解密
     *
     * @param secretKey  密钥：加密的规则 16位
     * @param cipherText 密文：加密后的内容，即需要解密的内容
     * @return plainText
     * 明文：解密后的内容即加密前的内容，如有异常返回空串：""
     */
    public static String decrypt(String secretKey, String cipherText) {
        if (secretKey.length() != PRIVATE_KEY_SIZE_BYTE) {
            throw new RuntimeException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
        }
        // 明文字符串
        String plainText = "";
        try {
            Cipher cipher = initParam(secretKey, Cipher.DECRYPT_MODE);
            // 将加密并编码后的内容解码成字节数组
            byte[] byteCipherText = Base64.decodeBase64(cipherText);
            // 解密
            byte[] bytePlainText = cipher.doFinal(byteCipherText);
            plainText = new String(bytePlainText, KEY_CHARSET);
        } catch (java.lang.Exception e) {
            throw new RuntimeException("AESUtil:decrypt fail!", e);
        }
        return plainText;
    }

    /**
     * 初始化参数
     *
     * @param secretKey 密钥：加密的规则 16位
     * @param mode      加密模式：加密or解密
     */
    public static Cipher initParam(String secretKey, int mode) {
        try {
            // 防止Linux下生成随机key
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(secretKey.getBytes());
            // 获取key生成器
            KeyGenerator keygen = KeyGenerator.getInstance(KEY_ALGORITHM);
            keygen.init(PRIVATE_KEY_SIZE_BIT, secureRandom);

            // 获得原始对称密钥的字节数组
            byte[] raw = secretKey.getBytes();

            // 根据字节数组生成AES内部密钥
            SecretKeySpec key = new SecretKeySpec(raw, KEY_ALGORITHM);
            // 根据指定算法"AES/CBC/PKCS5Padding"实例化密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            IvParameterSpec iv = new IvParameterSpec(secretKey.getBytes());
            cipher.init(mode, key, iv);
            return cipher;
        } catch (java.lang.Exception e) {
            throw new RuntimeException("AESUtil:initParam fail!", e);
        }
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    //transfer
    public static String generateSign(Map<String, Object> signMap, String key) {
        signMap.remove("sign");

        StringBuilder builder = new StringBuilder();
        for (String entryKey : signMap.keySet()) {
            String val = (String) signMap.get(entryKey);
            if (org.apache.commons.lang.StringUtils.isBlank(val)) continue;
            builder.append(entryKey + "=" + val + "&"); // 签名原串，不url编码
        }
        builder.append("key=" + key);

        String signStr = md5(builder.toString()).toLowerCase();
        return signStr;
    }

    //transfer
    public static String md5(String str) {
        if (str == null) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return str;
        } catch (UnsupportedEncodingException e) {
            return str;
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        int aa;

        for (int i = 0; i < byteArray.length; i++) {
            aa = byteArray[i];
            aa = aa & 0xff;
            if (Integer.toHexString(aa).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(aa));
            else
                md5StrBuff.append(Integer.toHexString(aa));
        }
        return md5StrBuff.toString();
    }

    public static void main(String[] args) throws Exception{
        Test test = new Test();
        test.transfer();
    }



    private String trade = "c9997ab525a76a09";
    private String agentTrade = "8228b4a15cc32bd6";
    private String crossBorder = "2acfc577a341e729";
    private String agentCrossBorder = "4ded8dd244bea5b5";
    private String smsTrans = "c438d67b78eafc8a";
    private String smsDetails = "483c3c111bc83a9d";
    private String account = "9214147e230f2c61";
    private String auth = "f79959221001473f";

    public void transfer() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        JSONObject dataObject = new JSONObject();
        dataObject.put("amount", "15");
        String transId = String.valueOf(System.currentTimeMillis());
        System.out.println("transId:"+transId);
        dataObject.put("updateTime", "2019-07-11 12:29:51");
        dataObject.put("createTime", "2019-07-11 12:29:51");
        dataObject.put("transDate", "2019-07-11 12:29:51");
        dataObject.put("transId", "1562811448413");
        dataObject.put("appId", "1");
        dataObject.put("channelTransId", "1");
        dataObject.put("deviceId", "1");
        dataObject.put("mchId", "1");
        dataObject.put("mchOrderDate", "");
        dataObject.put("mchOrderId", "1");
        dataObject.put("oriMhtOrderAmt", "1");
        dataObject.put("routerTypeId", "1");
        dataObject.put("spTxnTime", "2019-06-26 20:29:51");
        dataObject.put("thirdTransId", "1");
        dataObject.put("settleFlag", "1");
        dataObject.put("transStatus", "A001");
        dataObject.put("accDate", "2019-06-26 20:29:51");
        dataObject.put("currency", "HK");
        dataObject.put("fee", "3");
        dataObject.put("feeRmb", "2");
        dataObject.put("ipaynowAmount", "1");
        dataObject.put("ipaynowAmountRmb", "1");

        // String j = "{\"amount\":\"15385\",\"otherSideAccno\":\"1114813288445966\",\"orderNo\":\"62954\",\"otherSideAccName\":\"\",\"transTime\":\"2019-07-03 11:36:43\",\"mchManagerName\":\"杨福照\",\"mchOrderNo\":\"1146263169508634626\",\"mchName\":\"王伟\",\"memo\":\"打款申请，批次号：1146042919630442497\",\"mhtNo\":\"000000000512874\",\"mchManager\":\"73\",\"accCoreTransType\":\"avail-ins\",\"originReqId\":\"100212c300612201907031143310009976\",\"reqId\":\"100212c300612201907031143320009986\",\"accountId\":\"1114813288445966\",\"bizTransType\":\"T004\",\"fundPurpose\":\"ET01\",\"accType\":\"AT01\",\"mchManagerDeptName\":\"产品研发部\",\"accAvailableBalance\":\"653062606\",\"accCoreOrderNo\":\"100212c300612201907031143320009980_62955\",\"mchManagerDept\":\"9\"}";
        // JSONObject dataObject = JSON.parseObject(j);

        String json = JSON.toJSONString(dataObject);
        String body = encrypt(trade, json);/////////////////////////////////
        // String body = encrypt("2acfc577a341e728", json);

        Map<String, String> header = new HashMap<String, String>();
        header.put("source", "10000");
        header.put("time", String.valueOf(new Date().getTime()));
        header.put("id", "100212c300612201907031143320009980_62955");
        header.put("length", String.valueOf(json.length()));
        header.put("rdStr", String.valueOf(RandomUtils.nextInt()));
        header.put("msg", "");

        TreeMap<String, Object> signMap = JSONObject.parseObject(JSON.toJSONString(header), new TypeReference<TreeMap<String, Object>>() {
        });

        signMap.put("data", body);
        String sign = generateSign(signMap, "5ivAibPsb8NtYWN6");
        header.put("sign", sign);

        String send = HttpCilentUtils.send("http://ido-test.ipaynow.cn/transfer", header, body, "UTF-8");
        System.out.println(send);
    }

}

