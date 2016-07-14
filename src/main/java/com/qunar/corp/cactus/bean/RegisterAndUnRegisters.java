/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * @author zhenyu.nie created on 2013 13-12-26 下午2:46
 */
public class RegisterAndUnRegisters {

    public final List<GovernanceData> registers = Lists.newArrayList();
    public final List<GovernanceData> unRegisters = Lists.newArrayList();

    public RegisterAndUnRegisters() {
    }

    public List<GovernanceData> getRegisters() {
        return getCopy(registers);
    }

    private List<GovernanceData> getCopy(List<GovernanceData> origins) {
        List<GovernanceData> copies = Lists.newArrayList();
        for (GovernanceData origin : origins) {
            copies.add(origin.copy());
        }
        return copies;
    }

    public List<GovernanceData> getUnRegisters() {
        return getCopy(unRegisters);
    }

    public RegisterAndUnRegisters addRegister(GovernanceData data) {
        this.registers.add(data.copy());
        return this;
    }

    public RegisterAndUnRegisters addUnRegister(GovernanceData data) {
        this.unRegisters.add(data.copy());
        return this;
    }

    public RegisterAndUnRegisters add(RegisterAndUnRegisters datas) {
        this.unRegisters.addAll(datas.unRegisters);
        this.registers.addAll(datas.registers);
        return this;
    }


}
