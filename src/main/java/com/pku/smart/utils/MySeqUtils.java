package com.pku.smart.utils;

import java.util.concurrent.atomic.AtomicLong;

public class MySeqUtils {
    private static AtomicLong pay_seq = new AtomicLong(0L);
    private static String pay_seq_prefix = "P";
    private static AtomicLong refund_seq = new AtomicLong(0L);
    private static String refund_seq_prefix = "R";
    private static String node = "00";

    public static String getPay() {
        return getSeq(pay_seq_prefix, pay_seq);
    }

    public static String getRefund() {
        return getSeq(refund_seq_prefix, refund_seq);
    }

    private static String getSeq(String prefix, AtomicLong seq) {
        prefix += node;
        return String.format("%s%s%06d", prefix, DateUtils.getSeqString(), (int) seq.getAndIncrement() % 1000000);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println("pay=" + getPay());
            System.out.println("refund=" + getRefund());
        }
    }
}
