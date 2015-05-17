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
package com.ibm.hrl.proton.runtime.metadata.epa;


/**
 * Metadata for agent queues. 
 * Includes specification of the ordering policy (occurence or detection time) , sorting policy (sorted on non sorted queue),
 * the buffering time etc.
* <code>AgentChannelMetadata</code>.
* 
*
 */
public class AgentQueueMetadata
{
    /**
     TODO: determine how to set those policies based on metadata
    *Policies for the agent queues. The policy dictates if the queue is sorted/unsorted and if sorted
    *which parameter it is sorted on - the occurence or detection time. These policies are derived from intersection
    *between agent and context definitions - if agent is a temporal agent it would make sence to use a sorted
    *queue, but event if the agent is not temporal , but the context it is defined on is, 
    *it would also make sence to make the queue sorted. 
    * 
    *
    */
    public enum OrderingPolicy {OCCURENCE_TIME,DETECTION_TIME};
    public enum SortingPolicy {SORTED, NON_SORTED};
    
    private String agentTypeName;
    private OrderingPolicy orderingPolicy;
    private long bufferingTime;
    private SortingPolicy sortingPolicy;
    
    public AgentQueueMetadata(String agentTypeName,
            OrderingPolicy orderingPolicy, long bufferingTime,SortingPolicy sortingPolicy)
    {
        super();
        this.agentTypeName = agentTypeName;
        this.orderingPolicy = orderingPolicy;
        this.bufferingTime = bufferingTime;
        this.sortingPolicy = sortingPolicy;
    }
    
    public SortingPolicy getSortingPolicy()
    {
        return sortingPolicy;
    }

    public void setSortingPolicy(SortingPolicy sortingPolicy)
    {
        this.sortingPolicy = sortingPolicy;
    }

    public String getAgentTypeName()
    {
        return agentTypeName;
    }

    public void setAgentTypeName(String agentTypeName)
    {
        this.agentTypeName = agentTypeName;
    }

    public OrderingPolicy getOrderingPolicy()
    {
        return orderingPolicy;
    }

    public void setOrderingPolicy(
            OrderingPolicy orderingPolicy)
    {
        this.orderingPolicy = orderingPolicy;
    }

    public long getBufferingTime()
    {
        return bufferingTime;
    }

    public void setBufferingTime(long bufferingType)
    {
        this.bufferingTime = bufferingType;
    }

   

    
    
    
    
}
