package simple.util.net.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import simple.config.Simple;
import simple.util.tools.LogUtil;

/**
 * Created by 20150726 on 2016/6/4.
 */
public class TexasSocketConstants {

    /**
     * 获取SOCKET的URI
     *
     * @param uid
     * @param roomId
     * @return
     */
    public static final URI getRoomWebSocketUri(String uid, String roomId) {
        try {
            StringBuffer socketUrl = new StringBuffer();
            socketUrl.append(Simple.Config.getWBSocket())
                    .append("ws?uid=")
                    .append(uid)
                    .append("&room_id=")
                    .append(roomId);
            URI uri = new URI(socketUrl.toString());
            LogUtil.d("TexasSocketConstants", socketUrl.toString());
            return uri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
