package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;
import org.tio.coding.DefaultDecoder;
import org.tio.coding.IDecoder;
import org.tio.common.channel.Channel;
import org.tio.common.packet.ReadPacket;
import org.tio.util.CheckSumUtil;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/20
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:  t-io解码器
 */
@Slf4j
public class DecodeTaskQueue extends AbstractTaskQueue<ByteBuffer> {

    private Channel context;

    public DecodeTaskQueue(Channel context) {
        this.context = context;
    }

    @Override
    public void runTask(ByteBuffer byteBuffer) throws InterruptedException {

        IDecoder decoder = DefaultDecoder.newInstance();
        ReadPacket packet = decoder.decode(byteBuffer, msgQueue);

        if (context.useChecksum()) {
            byte[][] bytes = {
                    packet.header(), packet.optional(), new byte[]{(byte) (packet.packetSeq() >> 8), (byte) packet.packetSeq()}, packet.body()
            };
            if (CheckSumUtil.judgeCheckSum(bytes)) {
                context.getHandlerRunnable().addMsg(packet);
            } else {
                log.warn("validate checkSum failed, and the packet[{}] will be discard.", packet.toString());
            }
        }
    }
}
