package org.haox.kerb.codec.legacy.pac;

import org.haox.kerb.codec.legacy.DecodingException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PacSignature {

    private int type;
    private byte[] checksum;

    public PacSignature(byte[] data) throws DecodingException {
        try {
            PacDataInputStream bufferStream = new PacDataInputStream(new DataInputStream(
                    new ByteArrayInputStream(data)));

            type = bufferStream.readInt();
            checksum = new byte[bufferStream.available()];
            bufferStream.readFully(checksum);
        } catch(IOException e) {
            throw new DecodingException("pac.signature.malformed", null, e);
        }
    }

    public int getType() {
        return type;
    }

    public byte[] getChecksum() {
        return checksum;
    }

}
