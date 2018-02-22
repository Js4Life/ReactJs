import {BaseDataVM} from "../models/basedata.model"
import {DocumentVM} from "../models/document.model"
import {ContextWeightVM} from "../models/contextweight.model"
import {ContextVM} from "../models/context.model"
import {ParagraphVM} from "../models/paragraph.model"


export class AnalysisResponseVM extends BaseDataVM{
    contributingDocuments : Array<DocumentVM>
    contributingContexts : Array<ContextVM>
    contextNames : string
    mark : boolean
    checkListId : string
    checklistItemId : string
    taggedParagraphList: Array<ParagraphVM>
}