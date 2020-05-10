import com.webank.wedatasphere.exchangis.datax.core.processor.Processor;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;

import java.util.List;

/**
 * @author davidhua
 * 2019/8/27
 */
public class CustomProcessor implements Processor<DocWriteRequest>{
    @Override
    public DocWriteRequest process(List<Object> columnData) throws Exception {
        return null;
    }
}
