package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jks on 1/5/17.
 */
public class MessageBlob {
    static int maxSize = 200;
    byte[] blob;
    int part;
    int parts;
    int id;
    MessageBlob(){
    }
    MessageBlob(byte[] tblob, int tpart, int tparts){
        id = ThreadLocalRandom.current().nextInt();
        blob = tblob;
        part = tpart;
        parts = tparts;
    }

    MessageBlob(byte[] tblob, int tpart, int tparts, int tid){
        id = tid;
        blob = tblob;
        part = tpart;
        parts = tparts;
    }

    static List<MessageBlob> divideIntoBlobs(byte[] blob){
        ArrayList<MessageBlob> res = new ArrayList<>();

        int idx = 0;
        int id = ThreadLocalRandom.current().nextInt();
        int parts = (blob.length + maxSize - 1) / maxSize;
        while(idx * maxSize < blob.length){
            MessageBlob m = new MessageBlob(
                    Arrays.copyOfRange(blob, idx * maxSize, (idx + 1) * maxSize),
                    idx,
                    parts,
                    id
            );
            res.add(m);
            idx++;
        }
        return res;
    }

    public static Message combine(Set<MessageBlob> s, Kryo kryo){
        ArrayList<byte[]> acc = new ArrayList<>();
        int total = 0;
        int i = 0;
        for(MessageBlob m: s){
            total += m.blob.length;
            assert(m.part == i);
            acc.add(m.blob);
            i++;
        }
        byte[] res = new byte[total];
        int idx = 0;
        for(byte[] arr: acc){
            System.arraycopy(arr, 0, res, idx, arr.length);
            idx += arr.length;
        }
        return Message.deserialize(kryo, res);
    }

    public byte[] serialize(Kryo kryo){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Output output = new Output(stream);
        kryo.writeClassAndObject(output, this);
        output.close();
        byte[] res = stream.toByteArray();
        return res;
    }

    public static MessageBlob deserialize(Kryo kryo, byte[] arr){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(arr);
        Input input = new Input (inputStream);
        MessageBlob m = (MessageBlob)kryo.readClassAndObject(input);
        return m;
    }
}
