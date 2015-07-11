/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2015  
 */
package com.ibm.streamsx.topology.internal.functional.ops;

import static com.ibm.streamsx.topology.internal.functional.FunctionalHelper.getLogicObject;
import static com.ibm.streamsx.topology.internal.functional.FunctionalHelper.getOutputMapping;

import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingData.Punctuation;
import com.ibm.streams.operator.StreamingOutput;
import com.ibm.streams.operator.model.Icons;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.topology.function.Function;
import com.ibm.streamsx.topology.internal.spljava.SPLMapping;

@PrimitiveOperator
@InputPortSet(cardinality = 1)
@OutputPortSet(cardinality = 1)
@Icons(location16 = "opt/icons/functor_16.gif", location32 = "opt/icons/functor_32.gif")
public class FunctionTransform<T, U> extends FunctionQueueableFunctor<T> {

    private Function<T, U> transform;
    private SPLMapping<U> outputMapping;
    private StreamingOutput<OutputTuple> output;

    @Override
    public synchronized void initialize(OperatorContext context)
            throws Exception {
        super.initialize(context);

        setLogic(transform = getLogicObject(getFunctionalLogic()));
        output = getOutput(0);
        outputMapping = getOutputMapping(this, 0);
    }
    
    public void tuple(T value) throws Exception {

        U modValue;
        synchronized (transform) {
            modValue = transform.apply(value);
        }
        if (modValue != null) {
            output.submit(outputMapping.convertTo(modValue));
        }
    }
    
    @Override
    public void mark(Punctuation mark) throws Exception {
        output.punctuate(mark);
    }
}
