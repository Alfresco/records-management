var hiddenWorkflowNames = model.hiddenWorkflowNames,
   i,
   length = hiddenWorkflowNames.length,
   index;

for (i = 0; i < length; i++)
{
   if (hiddenWorkflowNames[i] == "activiti$activitiRequestForInformation")
   {
      index = i;
      break;
   }
}

if (index)
{
   hiddenWorkflowNames.splice(index, 1);
   model.hiddenWorkflowNames = hiddenWorkflowNames;
}