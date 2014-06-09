package icongetter.core;

import java.util.Vector;

import icongetter.ui.PrintableUi;

/**
 * Created with IntelliJ IDEA.
 * User: Auggie Liang
 * Date: 13-12-22
 * Time: 下午4:30
 */
public class MyLog {
    /** 可以打印的UI**/
    private static PrintableUi mPrintableUi;
    public  static int MESSAGE_PRINT = 4;
    public static final Vector<Message> mMessage = new Vector<Message>();

    public static void init(PrintableUi printableUi){
        mPrintableUi = printableUi;

    }
    /**
     * 普通信息
     * @param str
     */
    public static void i(String str){
        print("info-->" + str);
    }

    /**
     * 错误信息
     * @param str
     */
    public static void e(String str){
        print("error-->" + str);
    }

    /**
     * 警告信息
     * @param str
     */
    public static void w(String str){
        print("wrong-->" + str);
    }

    private static void print(String str) {
//        Message msg = new Message();
//        msg.what = MESSAGE_PRINT;
//        msg.object = str;
//        mPrintableUi.println(str);
        System.out.println(str);
        mPrintableUi.println(str);
//        sendMessage(msg);
    }

    private void printToUI(){
//        mPrintableUi.print();
    }
}
