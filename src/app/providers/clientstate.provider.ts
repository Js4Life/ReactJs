import { Injectable }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {RegulationVM} from "../models/regulation.model";
import {ContextVM} from "../models/context.model";
import {UserVM} from "../models/user.model";


@Injectable()

export class ClientState{
  
    loggedUser : UserVM
    CURRENT_STATE:string;
    selectedDocumentsWithContexts:any;
    selectedFilesForAnalysis:any;
    selectedRegulation:any;
    allContexts:any;
    lastAnalysisId:any;
    currentChecklistId:any;
    selectedVocab:any;
    contextsColors:any={};
    selectedContextColor:any;  
      // contextColorMap = {
      //   'HistoricalCreditLossInformation':{
      //       id:'16:328', name:'Historical Credit Loss Information', color:'#a2d881'
      //   },
      //   PurchasedFinancialAssetswithCreditDeterioration:{id:'13:116', name:'PurchasedFinancialAssetswithCreditDeterioration', color:'#f6e365'},
      //   Collateral:{id:'16:279', name:'Collateral', color:'#8591ca'},
      //   MeasurementBasisforCreditLosses:{id:'9:45', name:'Measurement Basis for CreditLosses', color:'#915996'},
      //   FinancialInstrumentsCreditLosses:{id:'9:167', name:'Financial Instruments-Credit Losses', color:'#218e94'},
      //   Disclosures:{id:'12:157', name:'Disclosures', color:'#565461'},
      //   AMEXContexts:{id:'10:48', name:'AMEX Contexts', color:'#00ffff'},
      //   QualitativeOverlay:{id:'11:36', name:'Qualitative Overlay', color:'#80ff80'},
      //   DefinitionOfDefault:{id:'13:289', name:'Definition Of Default', color:'#cc00cc'},
      //   InformationConsiderations:{id:'12:362', name:'Information Considerations', color:'#8c8cd9'},
      //   OperationalandGovernance:{id:'16:20', name:'Operational and Governance', color:'#75a3a3'},
      //   "Staging(IFRS9Only)":{id:'16:45', name:'Staging (IFRS 9 Only)', color:'#ffa64d'},
      //   TroubledDebtRestructuring:{id:'14:361', name:'TroubledDebtRestructuring', color:'#ff5500'},
      //   LifeofLoan:{id:'14:357', name:'LifeofLoan', color:'#218e94'},
      //   CECLEffectiveDate:{id:'11:274', name:'CECL Effective Date', color:'#663300'},
      //   LoanCommitments:{id:'11:399', name:'Loan Commitments', color:'#c3c388'},
      //   Segmentation:{id:'9:49', name:'Segmentation', color:'#330033'},
      //   LoanCommitment:{id:'12:170', name:'LoanCommitment', color:'#ddffcc'},
      //   ExecutionFrequency:{id:'12:386', name:'Execution Frequency', color:'#8080ff'},
      //   Disclosure:{id:'14:316', name:'Disclosure', color:'#0000b3'},
      //   TimeValueofMoney:{id:'12:314', name:'TimeValueofMoney', color:'#66ffff'},
      //   ScopingandRegulation:{id:'13:152', name:'ScopingandRegulation', color:'#66ffd9'},
      //   RootNode:{id:'9:0', name:'RootNode', color:'#993333'},
      //   ForwardLookingInformation:{id:'9:147', name:'Forward Looking Information', color:'#ff80ff'},
      //   ModelingMethodology:{id:'12:193', name:'Modeling Methodology', color:'#808080'},
      //   OperationsandGovernance:{id:'14:127', name:'OperationsandGovernance', color:'#ff1a1a'}
      // }
}