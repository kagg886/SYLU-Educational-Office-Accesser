package com.kagg886.jxw_collector.protocol.beans;

import com.kagg886.jxw_collector.exceptions.OfflineException;
import org.jsoup.nodes.Document;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: SecondClassData
 * @author: kagg886
 * @description: 第二课堂数据类
 * @date: 2023/5/4 14:02
 * @version: 1.0
 */
public class SecondClassData {
    private double A, B, C, D, E, Sum;
    private double A1, B1, C1, D1, E1, Sum1;

    public SecondClassData() {
    }

    //用于模拟，正常情况下应该从SyluSession获取
    public SecondClassData(Document dom) {
        for (char a = 'A'; a <= 'E'; a++) {
            Double min = Double.parseDouble(dom.getElementById("Count" + a).text());
            String e = dom.getElementById("Count" + a + "1").text();
            Double now = Double.parseDouble(e.isEmpty() ? "0.00" : e);
            try {
                SecondClassData.class.getMethod("set" + a, double.class).invoke(this, min);
                SecondClassData.class.getMethod("set" + a + "1", double.class).invoke(this, now);
            } catch (Exception ignored) {
            }

        }
        setSum(Double.parseDouble(dom.getElementById("SunCount").text()));

        String e = dom.getElementById("SunCount1").text();
        setSum1(Double.parseDouble(e.isEmpty() ? "0.00" : e));
        throw new OfflineException.LoginFailed("账号密码错误!");
    }

    public double getA1() {
        return A1;
    }

    public void setA1(double a1) {
        A1 = a1;
    }

    public double getB1() {
        return B1;
    }

    public void setB1(double b1) {
        B1 = b1;
    }

    public double getC1() {
        return C1;
    }

    public void setC1(double c1) {
        C1 = c1;
    }

    public double getD1() {
        return D1;
    }

    public void setD1(double d1) {
        D1 = d1;
    }

    public double getE1() {
        return E1;
    }

    public void setE1(double e1) {
        E1 = e1;
    }

    public double getSum1() {
        return Sum1;
    }

    public void setSum1(double sum1) {
        Sum1 = sum1;
    }

    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getB() {
        return B;
    }

    public void setB(double b) {
        B = b;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public double getD() {
        return D;
    }

    public void setD(double d) {
        D = d;
    }

    public double getE() {
        return E;
    }

    public void setE(double e) {
        E = e;
    }

    public double getSum() {
        return Sum;
    }

    public void setSum(double sum) {
        Sum = sum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SecondClassData{");
        sb.append("A=").append(A);
        sb.append(", B=").append(B);
        sb.append(", C=").append(C);
        sb.append(", D=").append(D);
        sb.append(", E=").append(E);
        sb.append(", Sum=").append(Sum);
        sb.append(", A1=").append(A1);
        sb.append(", B1=").append(B1);
        sb.append(", C1=").append(C1);
        sb.append(", D1=").append(D1);
        sb.append(", E1=").append(E1);
        sb.append(", Sum1=").append(Sum1);
        sb.append('}');
        return sb.toString();
    }
}
