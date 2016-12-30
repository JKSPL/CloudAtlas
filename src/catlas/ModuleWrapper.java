package catlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by julek on 30-Dec-16.
 */
public class ModuleWrapper implements KryoSerializable {
    Module module;
    ModuleWrapper(){}
    ModuleWrapper(Module mod){
        module = mod;
    }
    @Override
    public void write(Kryo kryo, Output output) {
        output.writeInt(module.id);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        int id = input.readInt();
        module = Util.hmapModules.get(id);
    }
}
