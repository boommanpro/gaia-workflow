package cn.boommanpro.gaia.workflow;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.parser.ChainParser;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class GaiaWorkflow {

    private String data;

    private ChainParser chainParser = new ChainParser();

    public GaiaWorkflow(String data) {
        this.data = data;
    }

    public Chain toChain(){
        JSONObject jsonObject = JSONUtil.parseObj(data);
        JSONArray nodes = jsonObject.getJSONArray("nodes");
        JSONArray edges = jsonObject.getJSONArray("edges");
        return chainParser.parse(nodes, edges, this);
    }
}
