package cn.zhz.privacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ZHZ
 * @date 2021-11-18
 * @apiNote
 */
public class Test {

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
//        System.out.println(list instanceof Collection);

        String str = "jhajja";
        list.add(str);
        list.add(1);
        System.out.println(str instanceof CharSequence);
    }
}
