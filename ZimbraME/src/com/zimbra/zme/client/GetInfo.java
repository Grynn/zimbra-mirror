/**
 * User: rossd
 * Date: Dec 12, 2006
 * Time: 10:25:34 PM
 */
package com.zimbra.zme.client;

import java.io.IOException;

public class GetInfo extends Command {

    public GetInfo(String url) {
        super(url);
    }

    public void exec()
            throws IOException {
        beginReq();
    }
}
