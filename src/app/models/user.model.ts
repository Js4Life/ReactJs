import {BaseDataVM} from "../models/basedata.model"
import {RoleVM} from "../models/role.model"

export class UserVM extends BaseDataVM{
    role : RoleVM = new RoleVM();
    lastLogin : string
    userId : string
    password : string
}