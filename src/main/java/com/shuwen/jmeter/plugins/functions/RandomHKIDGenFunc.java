package com.shuwen.jmeter.plugins.functions;

import com.shuwen.jmeter.plugins.functions.service.IdentityGenerateService;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * jMeter函数： 生成随机大陆身份证号码
 * @author shuwen
 */
public class RandomHKIDGenFunc extends AbstractFunction {

    private static final String FUNCTION_NAME_FOR_JMETER = "__random_hkid_generate";

    private static final List<String> ARGS_DESC = new ArrayList<>();
    static {
        ARGS_DESC.add("HKID initialRange for first char, default value: QWERTYUIOPASDFGHJKLZXCVBNM.");
    }

    private CompoundVariable[] variables = new CompoundVariable[1];

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) throws InvalidVariableException {
        return IdentityGenerateService.randomHKIDGen(variables[0].execute().trim());
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 1, 1);
        parameters.toArray(this.variables);
    }

    @Override
    public String getReferenceKey() {
        return FUNCTION_NAME_FOR_JMETER;
    }

    @Override
    public List<String> getArgumentDesc() {
        return ARGS_DESC;
    }
}
