import {BaseDataVM} from "../models/basedata.model"
import {ContextVM} from "../models/context.model"

export class ContextWeightVM extends ContextVM{
    weight : number
    isSelected : boolean
    value : string
    contextId : string
    contextName : string
}