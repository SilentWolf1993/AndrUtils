package com.yhy.andrutils;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.yhy.utils.core.APIUtils;
import com.yhy.utils.core.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.yhy.andrutils", appContext.getPackageName());

//        YamlUtils.load(appContext.getResources().getAssets().open("api-asset.yml"));
//
//        String register = YamlUtils.getString("user.register");
//        LogUtils.i(register);
//        LogUtils.i(YamlUtils.getLong("header.time"));
        LogUtils.i(APIUtils.get("user.register"));
        LogUtils.i(APIUtils.get("user.login"));
    }
}
