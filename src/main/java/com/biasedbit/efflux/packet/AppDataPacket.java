/*
 * Copyright 2010 Bruno de Carvalho
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.biasedbit.efflux.packet;

import org.jboss.netty.buffer.ChannelBuffer;

import com.biasedbit.efflux.util.TimeUtils;

/**
 * @author <a:mailto="bruno.carvalho@wit-software.com" />Bruno de Carvalho</a>
 */
public class AppDataPacket extends ControlPacket {

    // internal vars --------------------------------------------------------------------------------------------------

    private RtpVersion version;
    private int payloadType;
    private int sequenceNumber;
    private long timestamp;
    private double ntpTimestamp;
    private long rtpTimestamp;

    // constructors ---------------------------------------------------------------------------------------------------

    public AppDataPacket(Type type) {
        super(type);
        this.version = RtpVersion.V2;
    }

    // public static methods ------------------------------------------------------------------------------------------

    public static ChannelBuffer encode(int currentCompoundLength, int fixedBlockSize, AppDataPacket packet) {
        return null;
    }

    // ControlPacket --------------------------------------------------------------------------------------------------

    @Override
    public ChannelBuffer encode(int currentCompoundLength, int fixedBlockSize) {
        return encode(currentCompoundLength, fixedBlockSize, this);
    }

    @Override
    public ChannelBuffer encode() {
        return encode(0, 0, this);
    }
    
    public static AppDataPacket decode(ChannelBuffer buffer, Type type, int payloadType, int sequenceNumber) throws IndexOutOfBoundsException {
        AppDataPacket packet = new AppDataPacket(type);
        packet.payloadType = payloadType;

        packet.sequenceNumber = sequenceNumber;
        packet.timestamp = buffer.readUnsignedInt();
        
        if (buffer.readableBytes() >= 12) {
        	packet.ntpTimestamp = TimeUtils.decodeTimestamp(buffer.readLong());
	        packet.rtpTimestamp = buffer.readUnsignedInt();
        }
        
        return packet;
    }
    
    // getters & setters ----------------------------------------------------------------------------------------------

    public RtpVersion getVersion() {
        return version;
    }

    public void setVersion(RtpVersion version) {
        if (version != RtpVersion.V2) {
            throw new IllegalArgumentException("Only V2 is supported");
        }
        this.version = version;
    }

    public int getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(int payloadType) {
        if ((payloadType < 0) || (payloadType > 127)) {
            throw new IllegalArgumentException("PayloadType must be in range [0;127]");
        }
        this.payloadType = payloadType;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getNtpTimestamp() {
        return ntpTimestamp;
    }

    public void setNtpTimestamp(double ntpTimestamp) {
        this.ntpTimestamp = ntpTimestamp;
    }
    
    public long getRtpTimestamp() {
        return rtpTimestamp;
    }

    public void setRtpTimestamp(long rtpTimestamp) {
        this.rtpTimestamp = rtpTimestamp;
    }
    
    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder()
                .append("AppDataPacket{V=").append(this.version)
                .append(", PT=").append(this.payloadType)
                .append(", SN=").append(this.sequenceNumber)
                .append(", TS=").append(this.timestamp)
                .append(", NTPTS=").append(this.ntpTimestamp)
                .append(", RTPTS=").append(this.rtpTimestamp)
                .append("}")
                .toString();
    }
}
