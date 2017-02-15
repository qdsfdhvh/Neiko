package  seiko.neiko.dao.engine;


import org.noear.sited.SdNodeFactory;
import org.noear.sited.SdNode;
import org.noear.sited.SdNodeSet;
import org.noear.sited.SdSource;

/**
 * Created by yuety on 16/2/1. YiKu
 */
public class DdNodeFactory extends SdNodeFactory {

    @Override
    public SdNode createNode(SdSource source) {return new DdNode(source);}

    @Override
    public SdNodeSet createNodeSet(SdSource source) {return new DdNodeSet(source);}
}
