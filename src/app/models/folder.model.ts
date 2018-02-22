import {BaseDataVM} from "../models/basedata.model"
import {DocumentVM} from "../models/document.model"

export class FolderVM extends BaseDataVM{
    parentFolderName : string
    parentFolderId : string
    children : Array<FolderVM>
    documentList : Array<DocumentVM>
}
