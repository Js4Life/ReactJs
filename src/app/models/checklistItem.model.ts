import {BaseDataVM} from "../models/basedata.model"

export class checklistItemVM extends BaseDataVM{
    requirementDetails : string
    implementationSteps : string
    itemStatus:string
    assignedUsers:any[]
    assigneeId:string
    currentAssigneeName:string
}