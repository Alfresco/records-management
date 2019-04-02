package org.alfresco.bm.dataload.rm.services;

import org.alfresco.bm.utils.ParameterCheck;
import org.apache.commons.lang3.StringUtils;


/**
 * Data object representing a record
 *
 * @author Ana Bozianu
 * @since 2.6
 */
public class RecordData
{
    // final properties
    private final String id;
    private final RecordContext context;
    private final String inPlacePath;

    // editable properties
    private String name;
    private String parentPath;
    private ExecutionState executionState;
    private int randomizer;

    public RecordData(String id, RecordContext context, String name, String parentPath, String inPlacePath, ExecutionState executionState)
    {
        ParameterCheck.mandatoryString("id", id);
        ParameterCheck.mandatory("context", context);
        ParameterCheck.mandatoryString("name", name);
//      ParameterCheck.mandatoryString("parentPath or inPlacePath", parentPath + inPlacePath);

        randomizer = (int)(Math.random() * 1E6);
        this.id = id;
        this.context = context;
        this.name = name;
        this.parentPath = parentPath;
        this.inPlacePath = inPlacePath;
        this.executionState = executionState;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("RecordData [");
        builder.append("id=").append(id)
               .append(", context=").append(context)
               .append(", name=").append(name)
               .append(", parentPath=").append(parentPath);
        if(!StringUtils.isBlank(inPlacePath))
        {
            builder.append(", inPlacePath=").append(inPlacePath);
        }
        builder.append(", recordState=").append(executionState);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode()
    {
        return id.hashCode() + context.hashCode() + name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RecordData other = (RecordData) obj;
        return this.id.equals(other.id) &&
               this.context.equals(other.context) &&
               this.name.equals(other.name) &&
               (this.parentPath + "|" + this.inPlacePath).equals(other.parentPath + "|" + other.inPlacePath) &&
               this.executionState.equals(other.executionState);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getParentPath()
    {
        return parentPath;
    }

    public void setParentPath(String parentPath)
    {
        this.parentPath = parentPath;
    }

    public String getId()
    {
        return id;
    }

    public RecordContext getContext()
    {
        return context;
    }

    public String getInPlacePath()
    {
        return inPlacePath;
    }

    public ExecutionState getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState(ExecutionState executionState)
    {
        this.executionState = executionState;
    }

    public int getRandomizer()
    {
        return randomizer;
    }

    public void setRandomizer(int randomizer)
    {
        this.randomizer = randomizer;
    }
}
