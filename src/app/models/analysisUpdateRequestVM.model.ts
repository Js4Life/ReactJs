import {BaseDataVM} from "../models/basedata.model"
import {AnalysisRequestVM} from "../models/analysisrequest.model"

export class AnalysisUpdateRequestVM extends BaseDataVM{
    analysisToDelete : AnalysisRequestVM
    analysisToUpdate : AnalysisRequestVM
}