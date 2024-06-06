package org.apache.zookeeper.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class TestPathUtils {
    private String path;
    private boolean isSequential;
    private boolean isExpectedException;

    public TestPathUtils(

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
                {"/",                   false,                  false},             //case 1
                {"/",                   true,                   false},             //case 2
                {"/abab/",              false,                  true},              //case 3
                {"/abab/",              true,                   true},              //case 4
                {"/abab",               true,                   false},             //case 5
                {"",                    false,                  true},              //case 6
                {"",                    false,                  true},              //case 7
                {null,                  true,                   true},              //case 8
                {null,                  false,                  true},              //case 9
                {"/abab./",             true,                   true},              //case 10
                {"/.abab",              false,                  true},              //case 11
                {"/ab.ab/",             true,                   true},              //case 12
                {"ab.ab",               false,                  true},              //case 13
                {"abab/",               false,                  true},              //case 14
                {"/ab&ab",              false,                  true},              //case 15
                {"/ab|ab",              true,                   true}               //case 16

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