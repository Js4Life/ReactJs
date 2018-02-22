import {BaseDataVM} from "../models/basedata.model"
import {ContextWeightVM} from "../models/contextweight.model"

export class ParagraphVM extends BaseDataVM{
    paraContent : string
    startPage : number
    endPage : number
    paraSequence : number
    contextDistribution : Array<ContextWeightVM>
    relatedParagraphs : Array<string>;
}