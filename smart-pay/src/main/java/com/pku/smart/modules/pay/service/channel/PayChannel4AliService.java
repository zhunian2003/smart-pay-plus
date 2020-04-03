package com.pku.smart.modules.pay.service.channel;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.config.AliPayConfig;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.service.IPayChannelService;
import com.pku.smart.modules.pay.vopackage.VoTradeResult;
import com.pku.smart.utils.HttpClientUtil;
import com.pku.smart.utils.StringUtils;
import com.pku.smart.utils.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PayChannel4AliService {
    private final MyLog _log = MyLog.getLog(PayChannel4AliService.class);

    @Autowired
    AliPayConfig aliPayConfig;

    @Autowired
    IPayChannelService payChannelService;

    /**
     * 构建支付宝服务
     *
     * @param channelId
     * @param mchId
     * @return
     */
    public AlipayClient buildAlipayClient(String channelId, String mchId) {
        _log.info("============开始构建支付宝服务============");
        PayChannel payChannel = payChannelService.selectPayChannel(channelId, mchId);
        aliPayConfig.init(payChannel.getParam());
        String url = aliPayConfig.getUrl();
        String app_id = aliPayConfig.getApp_id();
        String sign_type = aliPayConfig.getSign_type();
        Short is_sandbox = aliPayConfig.getIsSandbox();
        _log.info("支付宝网关：{}，app_id：{}，签名类型：{}，沙箱模式否：{}", url, app_id, sign_type, is_sandbox);
        AlipayClient client = new DefaultAlipayClient(url, app_id, aliPayConfig.getRsa_private_key(), aliPayConfig.FORMAT, aliPayConfig.CHARSET, aliPayConfig.getAlipay_public_key(), sign_type);
        _log.info("============构建支付宝服务完毕============");

        return client;
    }

    /**
     * 支付宝条码支付
     *
     * @param client
     * @param model
     * @return
     */
    public VoTradeResult tradePay(AlipayClient client, AlipayTradePayModel model) {
        _log.info("============支付宝条码支付开始============");
        String mchId = model.getOperatorId();
        AlipayTradePayRequest alipay_request = new AlipayTradePayRequest();
        alipay_request.setBizModel(model);
        String notify_url = String.format(aliPayConfig.getNotify_url(), mchId, PayConstant.PAY_CHANNEL_ALIPAY_BR);
        _log.debug("设置异步通知地址");
        alipay_request.setNotifyUrl(notify_url);
        String return_url = String.format(aliPayConfig.getReturn_url(), mchId, PayConstant.PAY_CHANNEL_ALIPAY_BR);
        _log.debug("设置同步地址");
        alipay_request.setReturnUrl(return_url);

        VoTradeResult result = new VoTradeResult();
        try {
            String payOrderId = model.getOutTradeNo();
            _log.info("支付单号：{}", payOrderId);
            AlipayTradePayResponse response = client.execute(alipay_request);
            if ("10000".equals(response.getCode())) {
                _log.info("交易成功，直接返回");
            } else if ("10003".equals(response.getCode())) {
                _log.info("等待用户支付，轮询查交易结果，如果超时调用交易撤销");
                response = loopQueryAndCancel(client, payOrderId, response);
            } else if ("20000".equals(response.getCode())) {
                _log.info("系统错误，查询一次交易结果，如果交易失败调用撤销");
                response = checkQueryAndCancel(client, payOrderId, response);
            } else {
                _log.info("其他错误，直接返回失败");
            }

            result = toTradeResult(response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "============支付宝条码支付异常============");
            result = toTradeResult(e);
        }
        _log.info("============支付宝条码支付结束============");

        return result;
    }

    public VoTradeResult tradePrecreate(AlipayClient client, AlipayTradePrecreateModel model) {
        _log.info("============支付宝扫码支付(预下单)开始============");
        String mchId = model.getOperatorId();
        AlipayTradePrecreateRequest alipay_request = new AlipayTradePrecreateRequest();
        alipay_request.setBizModel(model);
        String notify_url = String.format(aliPayConfig.getNotify_url(), mchId, PayConstant.PAY_CHANNEL_ALIPAY_BR);
        _log.debug("设置异步通知地址");
        alipay_request.setNotifyUrl(notify_url);
        String return_url = String.format(aliPayConfig.getReturn_url(), mchId, PayConstant.PAY_CHANNEL_ALIPAY_BR);
        _log.debug("设置同步地址");
        alipay_request.setReturnUrl(return_url);

        VoTradeResult result = new VoTradeResult();
        try {
            AlipayTradePrecreateResponse response = client.execute(alipay_request);
            _log.info("支付宝扫码支付(预下单)调用成功");
            result = toTradeResult(response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "============支付宝扫码支付异常============");
            result = toTradeResult(e);
        }
        _log.info("============支付宝扫码支付(预下单)结束============");

        return result;
    }

    public VoTradeResult tradeQuery(AlipayClient client, AlipayTradeQueryModel model) {
        _log.info("============支付宝订单查询开始============");
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        alipay_request.setBizModel(model);

        VoTradeResult result = new VoTradeResult();
        try {
            AlipayTradeQueryResponse response = client.execute(alipay_request);
            _log.info("支付宝订单查询调用成功");
            result = toTradeResult(response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "============支付宝订单查询异常============");
            result = toTradeResult(e);
        }
        _log.info("============支付宝订单查询结束============");

        return result;
    }

    public VoTradeResult tradeRefund(AlipayClient client, AlipayTradeRefundModel model) {
        _log.info("============支付宝订单退款开始============");
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();
        alipay_request.setBizModel(model);

        VoTradeResult result = new VoTradeResult();
        try {
            AlipayTradeRefundResponse response = client.execute(alipay_request);
            _log.info("支付宝订单退款调用成功");
            result = toTradeResult(response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "============支付宝订单退款异常============");
            result = toTradeResult(e);
        }
        _log.info("============支付宝订单退款结束============");

        return result;
    }

    public VoTradeResult tradeCancel(AlipayClient client, AlipayTradeCancelModel model) {
        _log.info("============支付宝订单撤销开始============");
        AlipayTradeCancelRequest alipay_request = new AlipayTradeCancelRequest();
        alipay_request.setBizModel(model);

        VoTradeResult result = new VoTradeResult();
        try {
            AlipayTradeCancelResponse response = client.execute(alipay_request);
            String retry_flag = response.getRetryFlag();
            _log.info("retry_flag:{}", retry_flag);
            if ("Y".equalsIgnoreCase(retry_flag)) {
                response = client.execute(alipay_request);
                retry_flag = response.getRetryFlag();
            }
            if ("Y".equalsIgnoreCase(retry_flag)) {
                _log.warn("撤销失败，需要重试。");
            }
            _log.info("支付宝订单撤销调用成功");
            result = toTradeResult(response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "============支付宝订单撤销异常============");
            result = toTradeResult(e);
        }
        _log.info("============支付宝订单撤销结束============");

        return result;
    }

    public VoTradeResult downloadurlGet(AlipayClient client, AlipayDataDataserviceBillDownloadurlQueryModel model) {
        _log.info("============支付宝对账单下载开始============");
        AlipayDataDataserviceBillDownloadurlQueryRequest alipay_request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        alipay_request.setBizModel(model);

        VoTradeResult result = new VoTradeResult();
        try {
            AlipayDataDataserviceBillDownloadurlQueryResponse response = client.execute(alipay_request);
            _log.info("支付宝对账单下载地址获取成功");
            result = toTradeResult(response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "============支付宝对账单下载地址获取异常============");
            result = toTradeResult(e);
        }
        String billDate = model.getBillDate();
        String fileName = model.getBillType() + billDate.replace("-", "") + ".zip";

        if (result.getResultSuccess()) {
            AlipayDataDataserviceBillDownloadurlQueryResponse response = (AlipayDataDataserviceBillDownloadurlQueryResponse) result.getResultObject();
            String downloadUrl = response.getBillDownloadUrl();
            _log.info("获取下载地址成功：{}", downloadUrl);
            String filePath = aliPayConfig.getBill_path();
            File file = new File(filePath + File.separator + fileName);
            new File(filePath).mkdirs();
            file.deleteOnExit();
            _log.info("开始下载对账单");
            String filedownPath = HttpClientUtil.downLoadFile(downloadUrl, "UTF-8", file);

            if (StringUtils.isBlank(filedownPath)) {
                _log.error("对账文件下载失败");
                result.setResultSuccess(false);
                result.setResultCode("ERROR");
                result.setResultMsg("对账文件下载失败");
                return result;
            }

            _log.info("对账单下载成功开始解压");
            file = new File(filedownPath);
            if (!file.exists()) {
                result.setResultSuccess(false);
                result.setResultCode("ERROR");
                result.setResultMsg("没有下载到对账文件." + file.getName());
                return result;
            }

            String downFile = file.getPath();
            String downPath = filePath + File.separator;
            _log.info("开始解压" + downFile + "到" + downPath);
            List<String> listFile = ZipUtil.unZip(downFile, downPath);
            _log.info("对账文件解压完毕,删除源文件。");
            file.deleteOnExit();

            String cvsFile = null;
            for (int i = 0; i < listFile.size(); i++) {
                fileName = listFile.get(i);
                if (fileName.endsWith("_业务明细.csv")) {
                    cvsFile = fileName;
                    _log.info("匹配到对账单明细文件：{}", cvsFile);
                    break;
                }
            }

            if (StringUtils.isBlank(cvsFile)) {
                result.setResultSuccess(false);
                result.setResultCode("ERROR");
                result.setResultMsg("对账单解压失败.");
                return result;
            }

            _log.info("准备解析对账文件。");
            File fileCvs = new File(cvsFile);
            try {
                InputStreamReader fReader = new InputStreamReader(new FileInputStream(fileCvs), "GBK");
                _log.info("跳过前面5行，正式数据从第6行开始。");
//                List<FianceAl> list = new CsvToBeanBuilder(fReader)
//                        .withSkipLines(5)
//                        .withType(FianceAl.class)
//                        .build()
//                        .parse();

                result.setResultSuccess(true);
//                result.setResultObject(list);
            } catch (IOException e) {
                e.printStackTrace();
                result.setResultSuccess(false);
                result.setResultCode("ERROR");
                result.setResultMsg(e.getMessage());
                result.setResultObject(e);
            }
            return result;
        } else {
            _log.error("获取下载地址失败，直接返回");
            return result;
        }
    }

    private AlipayTradeCancelResponse checkTradeCancel(AlipayClient client, String payOrderId, String channelOrderNo) {
        _log.info("支付宝撤销，被撤销支付单号：{}", payOrderId);
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        model.setOutTradeNo(payOrderId);
        model.setTradeNo(channelOrderNo);
        request.setBizModel(model);
        try {
            AlipayTradeCancelResponse response = client.execute(request);
            if ("10000".equals(response.getCode())) {
                String action = response.getAction();
                _log.info("撤销成功，本次撤销触发的交易动作：{}。（关闭交易，无退款(close)；产生了退款(refund)）", action);
            }
            String retry_flag = response.getRetryFlag();
            _log.info("是否需要再次撤销：{}", retry_flag);
            if ("Y".equals(retry_flag)) {
                _log.info("再次撤销，如果仍然失败再试一次");
                response = client.execute(request);
                retry_flag = response.getRetryFlag();
            }
            if ("Y".equals(retry_flag)) {
                _log.info("第2次尝试撤销，如果仍然失败不处理，等待对账");
                response = client.execute(request);
            }

            return response;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "支付宝撤销异常");
            throw new RuntimeException(e);
        }
    }

    /**
     * 轮询查订单支付结果 如果超时调用撤销
     *
     * @param client
     * @param payOrderId
     * @param response
     * @return
     */
    private AlipayTradePayResponse loopQueryAndCancel(AlipayClient client, String payOrderId, AlipayTradePayResponse response) {
        _log.info("轮询查询支付宝订单{}是否成功", payOrderId);
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel alipay_model = new AlipayTradeQueryModel();
        alipay_model.setOutTradeNo(payOrderId);//商户订单号
        alipay_request.setBizModel(alipay_model);
        AlipayTradeQueryResponse queryResponse = null;
        try {
            queryResponse = client.execute(alipay_request);
            if ("10000".equals(response.getCode())) {
                String tradeStatus = queryResponse.getTradeStatus();//交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
                _log.info("订单状态：{}", tradeStatus);
                if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                    _log.info("等待买家付款，延迟5秒后再次查询");
                    TimeUnit.SECONDS.sleep(5);//秒
                    queryResponse = client.execute(alipay_request);
                    tradeStatus = queryResponse.getTradeStatus();
                    _log.info("订单状态：{}", tradeStatus);
                }
                if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                    _log.info("等待买家付款，延迟10秒后再次查询");
                    TimeUnit.SECONDS.sleep(10);//秒
                    queryResponse = client.execute(alipay_request);
                    tradeStatus = queryResponse.getTradeStatus();
                    _log.info("订单状态：{}", tradeStatus);
                }
                if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                    _log.info("等待买家付款，延迟15秒后再次查询");
                    TimeUnit.SECONDS.sleep(15);//秒
                    queryResponse = client.execute(alipay_request);
                    tradeStatus = queryResponse.getTradeStatus();
                    _log.info("订单状态：{}", tradeStatus);
                }
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    _log.info("将查询应答转换为支付应答.");
                    response = toTradePayResponse(response, queryResponse);
                    _log.info("查询结果：付款交易成功.");
                    return response;
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "支付宝异常,准备撤销交易：{}", e.getMessage());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            _log.error(ex, "系统异常,准备撤销交易：{}", ex.getMessage());
        }

        _log.info("交易失败或超时，开始调用支付宝撤销");
        AlipayTradeCancelResponse cancelResponse = checkTradeCancel(client, payOrderId, "");
        if (cancelResponse == null) {
            _log.warn("撤销订单：{}失败了", payOrderId);
        } else if ("Y".equalsIgnoreCase(cancelResponse.getRetryFlag())) {
            _log.warn("撤销订单：{}失败了,需要再次撤销", payOrderId);
        }

        return response;
    }

    private AlipayTradePayResponse checkQueryAndCancel(AlipayClient client, String payOrderId, AlipayTradePayResponse response) {
        _log.info("查询支付宝订单{}是否成功", payOrderId);
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel alipay_model = new AlipayTradeQueryModel();
        alipay_model.setOutTradeNo(payOrderId);//商户订单号
        alipay_request.setBizModel(alipay_model);
        AlipayTradeQueryResponse queryResponse = null;
        try {
            queryResponse = client.execute(alipay_request);
            if ("10000".equals(response.getCode())) {
                String tradeStatus = queryResponse.getTradeStatus();//交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
                _log.info("订单状态：{}", tradeStatus);
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    _log.info("将查询应答转换为支付应答");
                    response = toTradePayResponse(response, queryResponse);
                    _log.info("查询结果：付款交易成功");
                    return response;
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            _log.error(e, "支付宝异常,准备撤销交易：{}", e.getMessage());
        }

        _log.info("交易失败或超时，开始调用支付宝撤销");
        AlipayTradeCancelResponse cancelResponse = checkTradeCancel(client, payOrderId, "");
        if (cancelResponse == null) {
            _log.warn("撤销订单：{}失败了", payOrderId);
        } else if ("Y".equalsIgnoreCase(cancelResponse.getRetryFlag())) {
            _log.warn("撤销订单：{}失败了,需要再次撤销", payOrderId);
        }

        return response;
    }

    private AlipayTradePayResponse toTradePayResponse(AlipayTradePayResponse response, AlipayTradeQueryResponse queryResponse) {
        response.setCode("10000");
        response.setMsg(queryResponse.getMsg());
        response.setSubCode(queryResponse.getSubCode());
        response.setSubMsg(queryResponse.getSubMsg());
        response.setBody(queryResponse.getBody());
        response.setParams(queryResponse.getParams());
        response.setBuyerLogonId(queryResponse.getBuyerLogonId());
        response.setFundBillList(queryResponse.getFundBillList());
        response.setOpenId(queryResponse.getOpenId());
        response.setReceiptAmount(queryResponse.getReceiptAmount());
        response.setOutTradeNo(queryResponse.getOutTradeNo());
        response.setTotalAmount(queryResponse.getTotalAmount());
        response.setTradeNo(queryResponse.getTradeNo());
        return response;
    }

    private VoTradeResult toTradeResult(AlipayResponse response) {
        VoTradeResult result = new VoTradeResult();
        if ("10000".equals(response.getCode())) {
            result.setResultSuccess(true);
            result.setResultObject(response);
        } else {
            result.setResultSuccess(false);
            result.setResultCode(response.getSubCode());
            result.setResultMsg(response.getSubMsg());
            result.setResultObject(response);
        }
        return result;
    }

    private VoTradeResult toTradeResult(AlipayApiException e) {
        VoTradeResult result = new VoTradeResult();
        result.setResultSuccess(false);
        result.setResultCode(e.getErrCode());
        result.setResultMsg(e.getErrMsg());
        result.setResultObject(e);
        return result;
    }
}

