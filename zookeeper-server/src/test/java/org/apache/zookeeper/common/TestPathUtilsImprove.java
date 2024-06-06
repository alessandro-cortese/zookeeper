package org.apache.zookeeper.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestPathUtilsImprove {
    private String path;
    private boolean isSequential;
    private boolean isExpectedException;

    public TestPathUtilsImprove(

            String path,
            boolean isSequential,
            boolean isExpectedException){

        this.path = path;
        this.isSequential = isSequential;
        this.isExpectedException = isExpectedException;

    }

    @Parameterized.Parameters
    public static Collection<?> data() {

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

                //After report of ba-dua and JaCoCo

                {"/abab//ab",           false,                  true},              //case 17
                {"/abab/./ab",          true,                   true},              //case 18
                {"/abab/../ab",         false,                  true},              //case 19
                {invalidPath[0],        false,                  true},              //case 20
                {invalidPath[1],        false,                  true},              //case 21
                {invalidPath[2],        false,                  true},              //case 22
                {invalidPath[3],        false,                  true},              //case 23
                {invalidPath[4],        false,                  true},              //case 24
                {invalidPath[5],        false,                  true},              //case 25
                {invalidPath[6],        false,                  true},              //case 25
                {"/.",                  false,                  true},              //case 26
                {"/a..",                false,                  true},              //case 27
                {"/a/./.",              false,                  true}               //case 28

        });
    }

    @Test
    public void validatePathTest() {

        try {
            PathUtils.validatePath(this.path, this.isSequential);
        }catch (IllegalArgumentException e) {
            Assert.assertTrue(this.isExpectedException);
        }

    }

}