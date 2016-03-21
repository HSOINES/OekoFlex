package hsoines.oekoflex.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 14:23
 */
public class NumberFormatUtilTest {
    @Test
    public void testFormat() throws Exception {
        String s = NumberFormatUtil.format(0.12345678901234567890f);
        assertEquals("0,12", s);
    }

    @Test
    public void testThousends() throws Exception {
        String s = NumberFormatUtil.format(11111110.12345678901234567890f);
        assertEquals("11111110", s);
    }

    @Test
    public void testNormal() throws Exception {
        String s = NumberFormatUtil.format(110.12345678901234567890f);
        assertEquals("110,12", s);
    }
}