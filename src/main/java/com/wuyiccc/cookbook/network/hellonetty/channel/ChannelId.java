package com.wuyiccc.cookbook.network.hellonetty.channel;

import java.io.Serializable;

/**
 * @author wuyiccc
 * @date 2024/11/30 17:18
 */
public interface ChannelId extends Serializable, Comparable<ChannelId> {

    String asShortText();

    String asLongText();


}
