package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.hutool.json.JSONObject;

public interface NodeParser {
    ChainNode parse(JSONObject nodeJSONObject, GaiaWorkflow workflow);
}
