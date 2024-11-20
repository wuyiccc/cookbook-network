package com.wuyiccc.cookbook.network.day14.other;

import com.wuyiccc.cookbook.network.day14.other.util.ObjectUtil;
import com.wuyiccc.cookbook.network.day14.other.util.SystemPropertyUtil;

import java.util.Locale;

/**
 * @author wuyiccc
 * @date 2024/11/20 23:11
 */
public final class NettyRuntime {

    private NettyRuntime() {

    }

    /**
     * Holder class for available processors to enable testing.
     */
    static class AvailableProcessorsHolder {

        private int availableProcessors;


        synchronized void setAvailableProcessors(final int availableProcessors) {

            ObjectUtil.checkPositive(availableProcessors, "availableProcessors");
            if (this.availableProcessors != 0) {
                final String message = String.format(
                        Locale.ROOT
                        , "availableProcessors is already set to [%d], rejecting [%d]"
                        , this.availableProcessors
                        , availableProcessors
                );

                throw new IllegalArgumentException(message);
            }

            this.availableProcessors = availableProcessors;
        }


        synchronized int availableProcessors() {

            if (this.availableProcessors == 0) {
                final int availableProcessors = SystemPropertyUtil.getInt("io.netty.availableProcessors", Runtime.getRuntime().availableProcessors());
                setAvailableProcessors(availableProcessors);
            }

            return this.availableProcessors;
        }
    }

    private static final AvailableProcessorsHolder holder = new AvailableProcessorsHolder();

    public static void setAvailableProcessors(final int availableProcessors) {
        holder.setAvailableProcessors(availableProcessors);
    }

    public static int availableProcessors() {

        return holder.availableProcessors();
    }


}
