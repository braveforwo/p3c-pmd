/*
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.p3c.pmd.lang.vm.rule.other;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import org.junit.Test;


/**
 * Test for other vm rules.
 * 
 * @author keriezhang
 * @date 2017/06/18
 *
 */
public class OtherRulesTest extends SimpleAggregatorTst {

//    private static final String RULESET = "vm-ali-other";
    private static final String RULESET = "vm-rule-test";

    @Override
    public void setUp() {
        addRule(RULESET, "RuleTest");
//        addRule(RULESET,"RuleTest");
    }
    @Test
    public void Runtest(){
        Rule rule = this.findRule(RULESET,"RuleTest");
        runTests(rule,"RuleTest");
    }
}
