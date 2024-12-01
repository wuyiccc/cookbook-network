package com.wuyiccc.cookbook.network.hellonetty.channel;

/**
 * @author wuyiccc
 * @date 2024/11/30 20:29
 */
public class DefaultChannelId implements ChannelId {

    private String longValue;

    private static final long serialVersionUID = 3884076183504074063L;


    public static DefaultChannelId newInstance() {
        return new DefaultChannelId();
    }

    private DefaultChannelId() {

        long currentTimeMillis = System.currentTimeMillis();
        this.longValue = String.valueOf(currentTimeMillis);
    }




    @Override
    public String asShortText() {
        return null;
    }

    @Override
    public String asLongText() {

        String longValue = this.longValue;
        if (longValue == null) {
            this.longValue = longValue = String.valueOf(System.currentTimeMillis());
        }
        return longValue;
    }

    @Override
    public String toString() {
        return asShortText();
    }

    @Override
    public int compareTo(ChannelId o) {
        return 0;
    }
}
