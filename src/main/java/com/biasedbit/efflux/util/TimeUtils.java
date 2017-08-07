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

package com.biasedbit.efflux.util;

import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * @author <a:mailto="bruno.carvalho@wit-software.com" />Bruno de Carvalho</a>
 */
public class TimeUtils {

    public static final double SECS_FROM_1900_TO_1970 = 2208988800.0;
    private static final int NTP_TIMESTAMP_BYTES_LENGTH = Double.SIZE/8;
    
	// constructors ---------------------------------------------------------------------------------------------------

	private TimeUtils() {
    }

    // public static methods ------------------------------------------------------------------------------------------

    /**
     * Retrieve a timestamp for the current instant.
     *
     * @return Current instant.
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Retrieve a timestamp for the current instant, in nanoseconds.
     *
     * @return Current instant.
     */
    public static long nowNanos() {
        return System.nanoTime();
    }

    /**
     * Test whether a given event has timed out (in seconds).
     *
     * @param now        Current instant.
     * @param eventTime  Instant at which the event took place.
     * @param timeBuffer The amount of time for which the event is valid (in seconds).
     *
     * @return <code>true</code> if the event has expired, <code>false</code> otherwise
     */
    public static boolean hasExpired(long now, long eventTime, long timeBuffer) {
        return hasExpiredMillis(now, eventTime, timeBuffer * 1000);
    }

    /**
     * Test whether a given event has timed out (in milliseconds).
     *
     * @param now        Current instant.
     * @param eventTime  Instant at which the event took place.
     * @param timeBuffer The amount of time for which the event is valid (in milliseconds).
     *
     * @return <code>true</code> if the event has expired, <code>false</code> otherwise
     */
    public static boolean hasExpiredMillis(long now, long eventTime, long timeBuffer) {
        return (eventTime + timeBuffer) < now;
    }
    
    public static double decodeTimestamp(long timestamp) {
    	ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(ByteOrder.LITTLE_ENDIAN, NTP_TIMESTAMP_BYTES_LENGTH);
    	
    	buffer.writeLong(timestamp);
    	
    	return decodeTimestamp(buffer);
    }
    
	/**
     * Will read 8 bytes of a message beginning at <code>buffer</code>
     * and return it as a double, according to the NTP 64-bit timestamp
     * format.
     */
    public static double decodeTimestamp(ChannelBuffer buffer)
    {
        double r = 0.0;

        int readerIndex = buffer.readerIndex();
        
		for(int i = 0; i < NTP_TIMESTAMP_BYTES_LENGTH; i++)
        {
            byte readByte = buffer.getByte(readerIndex + NTP_TIMESTAMP_BYTES_LENGTH - i - 1);
			r += unsignedByteToShort(readByte) * Math.pow(2, (3-i)*8);
        }

        return r;
    }

    /**
     * Converts an unsigned byte to a short.  By default, Java assumes that
     * a byte is signed.
     */
    public static short unsignedByteToShort(byte b)
    {
        if((b & 0x80)==0x80) return (short) (128 + (b & 0x7f));
        else return (short) b;
    }
    
    /**
     * Returns a timestamp (number of seconds since 00:00 1-Jan-1900) as a
     * formatted date/time string. 
     */
    public static String timestampToString(double timestamp)
    {
        if(timestamp==0) return "0";

        Date dateTime = timestampToDateTime(timestamp);
        
		String dateString = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS").format(dateTime);

        /*
        // fraction
        double fraction = timestamp - ((long) timestamp);
        String fractionSting = new DecimalFormat(".000000").format(fraction);
		*/
        
        return dateString; // + fractionSting;
    }

	public static Date timestampToDateTime(double timestamp) {
		if (timestamp==0) return null;
		
		// timestamp is relative to 1900, utc is used by Java and is relative
        // to 1970 
        double utc = timestamp - SECS_FROM_1900_TO_1970;

        // milliseconds
        long ms = (long) (utc * 1000.0);
        //long ms = (long) (timestamp * 1000.0);

        // date/time
        Date dateTime = new Date(ms);
		return dateTime;
	}
}
