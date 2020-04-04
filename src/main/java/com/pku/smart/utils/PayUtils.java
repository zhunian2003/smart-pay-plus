package com.pku.smart.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pku.smart.common.base.BaseResult;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class PayUtils {
    private final static MyLog _log = MyLog.getLog(PayUtils.class);

    /**
     * 将baseresult对象转换为返回结果map
     * @param baseResult
     * @return
     */
    private static String makeRetMap(BaseResult baseResult) {
        _log.info("返回信息参数：{}", JSON.toJSONString(baseResult));
        JSONObject retMap = new JSONObject(true);////关键要设置为true，否则乱序
        retMap.put("retCode",baseResult.getRetCode());
        retMap.put("retMsg",baseResult.getRetMsg());
        retMap.put("resCode",baseResult.getResCode());
        retMap.put("errCode",baseResult.getErrCode());
        retMap.put("errCodeDesc",baseResult.getErrCodeDes());
        Map<String, Object> objMap = baseResult.getResultObject();
        if (!CollectionUtils.isEmpty(baseResult.getResultObject())){
            retMap.putAll(objMap);
        }
        _log.info("构建返回信息：{}", JSON.toJSONString(retMap));
        return retMap.toJSONString();
    }

    /**
     * 构建返回错误信息
     * 适用于调用失败
     * 简单来说 不涉及到微信支付宝失败的用这个
     * 调用失败
     * @param retCode
     * @param retMsg
     * @return
     */
    public static String makeRetFail(String retCode, String retMsg){
        Assert.hasText(PayConstant.RETURN_VALUE_FAIL,"该方法仅用于调用失败返回");
        BaseResult result = new BaseResult();
        result.setRetCode(retCode);
        result.setRetMsg(retMsg);
        return makeRetMap(result);
    }

    /**
     * 构建返回错误信息
     * 适用于调用成功但业务失败  如支付宝返回业务繁忙
     * @param retCode
     * @param retMsg
     * @param resCode
     * @param errCode
     * @param errCodeDesc
     * @return
     */
    public static String makeRetFail(String retCode, String retMsg, String resCode, String errCode, String errCodeDesc){
        Assert.hasText(PayConstant.RETURN_VALUE_FAIL,"该方法仅用于业务失败返回");
        BaseResult result = new BaseResult();
        result.setRetCode(retCode);
        result.setRetMsg(retMsg);
        result.setResCode(resCode);
        result.setErrCode(errCode);
        result.setErrCodeDes(errCodeDesc);
        return makeRetMap(result);
    }

    /**
     * 构建成功返回结果
     * 适用范围 业务成功
     * @param retCode
     * @param retMsg
     * @param retMap
     * @return
     */
    public static String makeRetSuccess(String retCode, String retMsg, Map<String, Object> retMap){
        Assert.hasText(PayConstant.RETURN_VALUE_SUCCESS,"该方法仅用于业务成功返回");
        BaseResult result = new BaseResult();
        result.setRetCode(retCode);
        result.setRetMsg(retMsg);
        result.setResCode(retCode);
        result.setResultObject(retMap);
        return  makeRetMap(result);
    }

    /**
     * 验证支付中心签名
     * @param params
     * @param key
     * @return
     */
    public static boolean verifyPaySign(Map<String,Object> params, String key) {
        String sign = (String)params.get("sign"); // 签名
        params.remove("sign");	// 不参与签名
        String checkSign = PayDigestUtil.getSign(params, key);
        if (!checkSign.equalsIgnoreCase(sign)) {
            return false;
        }
        return true;
    }

    /**
     * 验证平台支付中心签名
     * @param params
     * @param key
     * @param noSigns
     * @return
     */
    public static boolean verifyPaySign(Map<String,Object> params, String key, String... noSigns) {
        String sign = (String)params.get("sign"); // 签名
        params.remove("sign");	// 不参与签名
        if(noSigns != null && noSigns.length > 0) {
            for (String noSign : noSigns) {
                params.remove(noSign);
            }
        }
        String checkSign = PayDigestUtil.getSign(params, key);
        if (!checkSign.equalsIgnoreCase(sign)) {
            return false;
        }
        return true;
    }

    public static String genUrlParams(Map<String, Object> paraMap) {
        if(paraMap == null || paraMap.isEmpty()) return "";
        StringBuffer urlParam = new StringBuffer();
        Set<String> keySet = paraMap.keySet();
        int i = 0;
        for(String key:keySet) {
            urlParam.append(key).append("=").append(paraMap.get(key));
            if(++i == keySet.size()) break;
            urlParam.append("&");
        }
        return urlParam.toString();
    }

    /**
     * 发起HTTP/HTTPS请求(method=POST)
     * @param url
     * @return
     */
    public static String call4Post(String url) {
        try {
            URL url1 = new URL(url);
            if("https".equals(url1.getProtocol())) {
                return HttpClient.callHttpsPost(url);
            }else if("http".equals(url1.getProtocol())) {
                return HttpClient.callHttpPost(url);
            }else {
                return "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
