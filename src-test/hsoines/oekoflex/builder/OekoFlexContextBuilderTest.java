package hsoines.oekoflex.builder;

import hsoines.oekoflex.util.NumberFormatUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by jhense on 13.03.2016.
 */
public class OekoFlexContextBuilderTest {
@Before
    public void setup(){
    Locale.setDefault(Locale.GERMAN);
}

    @Test
    public void testCSVLocale() throws IOException {
        System.out.println("locale: " + Locale.getDefault().getDisplayName());
        File file = new File("test.csv");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(NumberFormatUtil.format(1.123456f)+"\n");
        fileWriter.write(NumberFormatUtil.format(-1.123456f)+"\n");
        fileWriter.write(NumberFormatUtil.format(1234567.123456f)+"\n");
        fileWriter.write(NumberFormatUtil.format(-1234567.123456f)+"\n");
        fileWriter.write(NumberFormatUtil.format(-1234567.123456f)+"\n");
        fileWriter.close();
    }

}