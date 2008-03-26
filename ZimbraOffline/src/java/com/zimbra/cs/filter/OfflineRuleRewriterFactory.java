package com.zimbra.cs.filter;

public class OfflineRuleRewriterFactory extends RuleRewriterFactory {

	@Override
    RuleRewriter createRuleRewriter() {
    	return new OfflineRuleRewriter();
    }
}
