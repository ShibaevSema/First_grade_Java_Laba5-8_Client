package semashibaev.ifmo.client;


import semashibaev.ifmo.cfs.CommandForServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Serialization {

    public static byte[] serialize(CommandForServer commandForServer) throws IOException, ClassNotFoundException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);

        os.writeObject(commandForServer);
        os.flush();

        return out.toByteArray();
    }
}



