package cn.boommanpro.gaia.workflow;

import cn.boommanpro.gaia.workflow.model.Chain;

public class GaiaWorkflow {

    private String data;

    public GaiaWorkflow(String data) {
        this.data = data;
    }

    public Chain toChain(){
        return new Chain();
    }
}
