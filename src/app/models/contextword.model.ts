import {BaseDataVM} from "../models/basedata.model"

export class ContextWord extends BaseDataVM{
    contextName : string
    contextUri : string
    words : string[]
}