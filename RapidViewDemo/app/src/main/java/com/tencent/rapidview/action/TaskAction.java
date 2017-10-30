package com.tencent.rapidview.action;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.parser.RapidParserObject;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class TaskAction
 * @Desc 调用task的动作
 *
 * @author arlozhang
 * @date 2016.08.04
 */
public class TaskAction extends ActionObject{

    public TaskAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run() {
        Var task = mMapAttribute.get("tid");
        RapidParserObject parser = getParser();

        if( parser == null || task == null ){
            return false;
        }

        parser.getTaskCenter().run(task.getString());

        return true;
    }
}
