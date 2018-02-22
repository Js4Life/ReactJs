import {BaseDataVM} from "../models/basedata.model"
import {RegulationVM} from "../models/regulation.model"
import {ParagraphVM} from "../models/paragraph.model"
import {ContextWeightVM} from "../models/contextweight.model";
import {GlossaryTermVM} from "../models/glossaryterm.model"

export enum DocType {
    DOC = 1,
    DOCX,
    XLS,
    XLSX
}

export class DocumentVM extends BaseDataVM{
    numPages : number
    docType : DocType
    uploadDate : string
    uploadedBy : string
    numParas : number
    regulation : RegulationVM
    paragraphs : Array<ParagraphVM>
    glossaryList : Array<GlossaryTermVM>
    contextDistribution : Array<ContextWeightVM>
}