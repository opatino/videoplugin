package com.google.android.exoplayer2.extractor.rtp;

import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.ts.TimestampAdjuster;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.ParsableByteArray;

import java.io.IOException;

/**
 * Created by opatino on 9/13/16.
 */
public class RtpExtractor implements Extractor {

    private final String TAG = "RtpExtractor";
    private final int RTP_MP2TS_TYPE = 33;
    public static final int RTP_NOT_A_VALID_TYPE = -1;
    private final int TS_PACKET_SIZE = 188;

    Extractor extractor;
    TimestampAdjuster timestampAdjuster;
    int workaroundFlags;

    int headerLength;

    private ExtractorInput out;
    private ByteArrayDataSource dataSource;

    private byte[] data;
    private ParsableByteArray bufferData;

    DataSpec dataSpec;

    public class RtpPacket {
        int ssrc;
        int[] csrc;
        int timestamp;
        int seqNumber;
        int p;
        int x;
        int m;
        int cc;
        int v;
        int type;

        RtpPacket() {
            ssrc = 0;
            timestamp = 0;
            seqNumber = 0;
            p = 0;
            x = 0;
            m = 0;
            cc = 0;
            v = 0;
            type = 0;
            csrc = new int[16];
        }
    }

    RtpPacket packet;
    int packetType = RTP_NOT_A_VALID_TYPE;

    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {

        @Override
        public Extractor[] createExtractors() {
            return new Extractor[] {new RtpExtractor()};
        }

    };

    public RtpExtractor() {
        this(new TimestampAdjuster(0));
    }

    public RtpExtractor(TimestampAdjuster ptsTimestampAdjuster) {
        this(ptsTimestampAdjuster, 0);
    }

    public RtpExtractor(TimestampAdjuster ptsTimestampAdjuster, int workaroundFlags) {
        Log.d(TAG, "Creating rtpoverts object");
        packetType = RTP_NOT_A_VALID_TYPE;
        this.timestampAdjuster = ptsTimestampAdjuster;
        this.workaroundFlags = workaroundFlags;
        headerLength = 0;
        packet = new RtpPacket();
    }

    @Override
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        Log.d(TAG, "Sniffing...");
        byte[] scratch = new byte[4];
        input.peekFully(scratch, 0, 4);
        parseRtpHeader(scratch);
        if (packet.v != 2) {
            return false;
        }
        switch (packet.type) {
            case RTP_MP2TS_TYPE:
                packetType = RTP_MP2TS_TYPE;
                headerLength = skipRtpHeader(input);
                extractor = new TsExtractor(timestampAdjuster, workaroundFlags);
                boolean ok = extractor.sniff(input);
                if (ok) {
                    initBuffers(TS_PACKET_SIZE);
                }
                return ok;
            default:
                return false;
        }
    }

    @Override
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        switch(packetType) {
            case RTP_MP2TS_TYPE:
            {
                input.read(bufferData.data, 0, 4);
                if ((bufferData.data[0] & 0xFF) != 0x47) {
                    parseRtpHeader(bufferData.data);
                    headerLength = skipRtpHeader(input);
                    input.skip(headerLength);
                    input.read(bufferData.data, 0, 4);
                }
                int read = input.read(bufferData.data, 4, TS_PACKET_SIZE-4);
                if (read == C.RESULT_END_OF_INPUT) {
                    Log.d(TAG, "end of input ");
                    return 0;
                }
                dataSource.open(dataSpec);
                break;
            }
            default:
                return 0;
        }
        return extractor.read(out, seekPosition);
    }

    private void parseRtpHeader(byte[] scratch) {
        int firstByte = (int) scratch[0];
        int secondByte = (int) scratch[1];
        packet.v = (firstByte & 0xC0) >> 6;
        packet.p = (firstByte & 0x20) >> 5;
        packet.x = (firstByte & 0x10) >> 4;
        packet.cc = firstByte & 0x0F;
        packet.m = (secondByte & 0x80) >> 7;
        packet.type = secondByte & 0x7F;
        packet.seqNumber = ((scratch[2] & 0xFF) << 8) + (scratch[3] & 0xFF);
    }

    private int skipRtpHeader(ExtractorInput input) throws IOException, InterruptedException {
        byte[] array = new byte[4];
        int extensionLength = 0;
        input.peekFully(array, 0, 4);
        packet.timestamp = (array[3] & 0xFF) + ((array[2] & 0xFF) << 8) + ((array[1] & 0xFF) << 16) + ((array[0] & 0xFF) << 24);
        input.peekFully(array, 0, 4);
        packet.ssrc = (array[3] & 0xFF) + ((array[2] & 0xFF) << 8) + ((array[1] & 0xFF) << 16) + ((array[0] & 0xFF) << 24);
        for (int i = 0; i < 16; i++) {
            packet.csrc[i] = 0;
        }
        for (int i = 0;  i < packet.cc; i++) {
            input.peekFully(array, 0, 4);
            packet.csrc[i] = (array[3] & 0xFF) + ((array[2] & 0xFF) << 8) + ((array[1] & 0xFF) << 16) + ((array[0] & 0xFF) << 24);
        }
        if (packet.x != 0) {
            byte[] xLength = new byte[2];
            input.advancePeekPosition(2); // skip 'profile defined' 2 bytes
            input.peekFully(xLength, 0, 2);
            extensionLength = (((xLength[0] & 0xFF) << 8) + (xLength[1] & 0xFF)) * 4;
            input.advancePeekPosition(extensionLength);
        }
        return (8 + packet.cc *4 + 4 + extensionLength);
    }

    private void initBuffers(int size) {
        data = new byte[size];
        bufferData = new ParsableByteArray(data, size);
        dataSource = new ByteArrayDataSource(bufferData.data);
        dataSpec = new DataSpec(null, 0, size, null);
        try {
            dataSource.open(dataSpec);
        } catch (IOException e) {
            Log.e(TAG, "Exception creating Rtp DataSource", e);
        }
        out = new DefaultExtractorInput(dataSource, 0, -1);
    }

    @Override
    public void seek(long time) {
        extractor.seek(time);
    }

    @Override
    public void init(ExtractorOutput output) {
        extractor.init(output);
    }

    @Override
    public void release() {
        extractor.release();
    }

}
