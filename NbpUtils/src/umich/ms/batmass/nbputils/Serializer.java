/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.nbputils;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openide.util.Lookup;

/**
 * Notice the very important point, that it first statically loads the NBP
 * ClassLoader, that knows about all the loaded classes and sets it as the
 * loader for FSTConfiguration.<br/>
 * That is needed as FST library is loaded in NbpUtils module, which doesn't
 * know about other classes loaded by the platform.
 * @author dmitriya
 */
public class Serializer {

    private final static ClassLoader nbpClassloader = Lookup.getDefault().lookup(ClassLoader.class);
    private final static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    static {
        conf.setClassLoader(nbpClassloader);
    }

    private Serializer() {
    }

    /**
     * Serializes the whole object to the provided stream using FST library.
     * <b>It will close the input stream when it's finished.</b>
     * @param toSerialize the object to be serialized
     * @param os the stream to which to serialize
     * @throws java.io.IOException
     */
    public static void serialize(Object toSerialize, OutputStream os) throws IOException {
        FSTObjectOutput out = conf.getObjectOutput(os);
        out.writeObject(toSerialize);
        os.close();
    }

    /**
     * Factory method for creating an instance by reading it from an XML file.
     * <b>It will close the input stream when it's finished.</b>
     * @param is a stream to a file created by XMLEncoder.writeObject()
     * @return instance of serialized object, to be cast to a proper class by
     * receiver.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
        FSTObjectInput in = conf.getObjectInput(is);
        Object obj = in.readObject();
        return obj;
    }
}
