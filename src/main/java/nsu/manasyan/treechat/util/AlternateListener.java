package nsu.manasyan.treechat.util;

import java.net.InetSocketAddress;

public interface AlternateListener {
    void onUpdate(InetSocketAddress newAlternate);
}
