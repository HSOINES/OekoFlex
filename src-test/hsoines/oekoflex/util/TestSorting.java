package hsoines.oekoflex.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhense on 26.07.2016.
 */
public class TestSorting {
    @Test
    public void testSort(){
        List<Float> list = new ArrayList<>();
        list.add(3f);
        list.add(2f);
        list.add(4f);
        System.out.println(list.toString());
        list.sort(Float::compare);
        for (Float aFloat : list) {
            System.out.println(aFloat);
        }

    }
}
