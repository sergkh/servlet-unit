package org.servletunit.util;

import org.springframework.stereotype.Component;

import static org.junit.Assert.fail;

/**
 * Created by yaroslav on 14/04/15.
 *
 * @author Yaroslav.Derman - yaroslav.derman@gmail.com
 */

@Component
public class Util {

    public String base64(String string2encode) {
        try {
            return Base64.encodeToString(string2encode.getBytes(), false);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error while Base64 encoding string: " + string2encode);
        }

        return null;
    }
}
