/*******************************************************************************
 * Copyright 2014 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ibm.hrl.proton.epa.state;

import java.io.Serializable;

import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;





/**
 * <code>AggregationOperatorData</code>.
 * 
 * 
 */
public class AggregationOperatorData extends OperatorData
{
    //local calculations for the operands
    Instance instanceData[];
    /**
     * @param operandsNumber
     */
    public AggregationOperatorData(int operandsNumber)
    {
        super(operandsNumber);       
        instanceData = new Instance[operandsNumber];
    }
    
  //TODO: perhaps not needed
    public void initializeLocalStatistics(int operandIndex)
    {
        if (instanceData[operandIndex] == null){
            instanceData[operandIndex] = new Instance();
        }
    }
    
    public void clearLocalStatistics (int operandIndex)
    {
        instanceData[operandIndex] = null;
    }
    
    public void addInstance(int operandIndex,IEventInstance eventInstance,IExpression minExpression,
                              IExpression maxExpression,
                              IExpression averageExpression,
                              IExpression sumExpression,
                              int weight)
    {
        Instance instance = getInstance(operandIndex); //either it is with some local calculation or empty        
        
        //add local calculations where applicable
        if (maxExpression != null)
        {
            Number currentMax = (Number)maxExpression.evaluate(eventInstance);
            double oldMax = instance.getMax();                  

            if(Double.isNaN(oldMax) || currentMax.doubleValue() > oldMax)
            {
                instance.setMax(currentMax.doubleValue());
            }                
             
        }
        
        if (minExpression != null)
        {
            Number currentMin = (Number)minExpression.evaluate(eventInstance);
            double oldMin = instance.getMin();                  

            if(Double.isNaN(oldMin) || currentMin.doubleValue() < oldMin)
            {
                instance.setMin(currentMin.doubleValue());
            }                
             
        }
        
        if(averageExpression != null){
            Number currAverage = (Number)(averageExpression.evaluate(eventInstance)); 
            instance.setAverageCount(instance.getAverageCount()+1);
            instance.setAverageSum(instance.getAverageSum()+currAverage.doubleValue());
            
        }
        
        if(sumExpression != null) {
            Number currentSum = (Number)sumExpression.evaluate(eventInstance);
            
            if(Double.isNaN(instance.getSum()))
               instance.setSum(0);
            instance.setSum(instance.getSum()+currentSum.doubleValue());
        }
        
        
        if (Double.isNaN(instance.getCount()))
            instance.setCount(0);
        instance.setCount(instance.getCount()+weight);
        
    
        instanceData[operandIndex] = instance;
    }
    
    public Instance getInstance(int operandIndex)
    {
       
        if (instanceData[operandIndex] == null) return new Instance();
        return instanceData[operandIndex];
    }

    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public final class Instance implements Serializable
    {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        /**
         * local calculation values per operand
         */
        private double count;
        private double sum;    
        private double max;    
        private double min;
        private double averageSum;
        private double averageCount;
       
        
        
        
        public Instance()
        {
            this.count = Double.NaN;
            this.sum = Double.NaN;
            this.max = Double.NaN;
            this.min  = Double.NaN;
            this.averageSum = Double.NaN;
            this.averageCount =  Double.NaN;
            
        }
        
        public Instance(double count, double sum, double min, double max, double averageSum,double averageCount, double percentage) {          
            this.count = count;
            this.sum = sum;
            this.min = min;
            this.max = max;
            this.averageSum = averageSum;
            this.averageCount = averageCount;
                       
        }
        
        public double getCount()
        {
            return count;
        }

        public void setCount(double count)
        {
            this.count = count;
        }

        public double getSum()
        {
            return sum;
        }

        public void setSum(double sum)
        {
            this.sum = sum;
        }

        public double getMax()
        {
            return max;
        }

        public void setMax(double max)
        {
            this.max = max;
        }

        public double getMin()
        {
            return min;
        }

        public void setMin(double min)
        {
            this.min = min;
        }

        public double getAverageSum()
        {
            return averageSum;
        }
        
        public double getAverageCount()
        {
            return averageCount;
        }

        public void setAverageSum(double averageSum)
        {
            this.averageSum = averageSum;
        }

        public void setAverageCount(double averageCount){
            this.averageCount = averageCount;
        }
      

       
 
    }
    
}
