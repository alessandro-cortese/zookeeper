package org.apache.zookeeper.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class TestPathUtilsPit {
    private String path;
    private boolean isSequential;
    private boolean isExpectedException;

    public TestPathUtilsPit(

            String path,
            boolean isSequential,
            boolean isExpectedException){

        this.path = path;
        this.isSequential = isSequential;
        this.isExpectedException = isExpectedException;

    }

    @Parameterized.Parameters
    public static Collection<?> getParameters() {

        String validPath = "/abab/";
        StringBuilder pathBuilder = new StringBuilder(validPath);
        String[] invalidPath = new String[7];
        char[] invalidCharacter = {
                '\u001f', '\u007f', '\u009f', '\ud800', '\uf8ff', '\ufff0', '\0'
        };

        for(int i = 0; i < invalidCharacter.length; i++){
            pathBuilder.append(invalidCharacter[i]);
            invalidPath[i] = pathBuilder.toString();
        }

        return Arrays.asList(new Object[][] {
                //{PATH,                IS_SEQUENTIAL,          EXCEPTION}
                //After PIT
                {"a/b",                 false,                  true},          //case 29
                {"a/b/c",               false,                  true},          //case 30
                {"/./a",                false,                  true},          //case 31
                {"/a/..",               false,                  true},          //case 32
                {"a/../b",              false,                  true},          //case 33
                {"a/b/..",              false,                  true},          //case 34
                {"/a/b/63/",            false,                  true},          //case 35
                {"/\u0001",             false,                  true},          //case 36
                {"/\u0019",             false,                  true},          //case 37
                {"/\u007F",             false,                  true},          //case 38
                {"/\u009F",             false,                  true},          //case 39
                {"/\ud800",             false,                  true},          //case 40
                {"/\uF8FF",             false,                  true},          //case 41
                {"/\uFFF0",             false,                  true},          //case 42
                {"/\uFFFF",             false,                  true}           //case 43
        });
    }

    @Test
    public void validatePathTest() {

        IllegalArgumentException exception = null;

        try {
            PathUtils.validatePath(this.path, this.isSequential);
        }catch (IllegalArgumentException e) {
            exception = e;
        }

        Assert.assertNotNull(exception);
    }

}