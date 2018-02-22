import {BaseDataVM} from "../models/basedata.model"

export class NewChecklistRequestVM extends BaseDataVM{
    description : string
    users : Array<string>
}