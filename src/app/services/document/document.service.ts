import { Injectable }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {RegulationVM} from "../../models/regulation.model"
import {ContextVM} from "../../models/context.model"
import {FolderVM} from "../../models/folder.model"
import {DocumentVM} from "../../models/document.model"
import {HttpVerbs} from "../../models/constants.model"
import {BaseHttpService} from "../../services/base-http.service"
import { ClientState} from '../../providers/clientstate.provider';
import { DocumentUploadRquestVM} from '../../models/documentuploadrequest.model';
import { ContextWord } from '../../models/contextword.model';

declare var stringformat : any;

@Injectable()
export class DocumentSvc extends BaseHttpService{

    myURL : string = this.baseUrl + 'docexplorer/';

    constructor( appState: ClientState, http: Http ){
        super(appState, http);
    }

    getAllDocuments() /*: Promise<RegulationVM>*/{
        /*return this
                .invokeService(this.myURL+DocumentURLS.ALLDOCUMENTS.url,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.ALLDOCUMENTS.propname]);*/
    }
    getDocumentById(docId : string) : Promise<DocumentVM>{
        // var docId='10:393';
        var remUrl = stringformat(DocumentURLS.DOCBYID.url,this.filterIdString(docId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.DOCBYID.propname]);
    }
    getDocumentsByRegulationId(regulationId : string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.DOCS_BY_REGULATIONID.url,this.filterIdString(regulationId));
        
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.DOCBYID.propname]);
    }

    deleteDocumentById(docId:string) : Promise<DocumentVM>{
        var remUrl = stringformat(DocumentURLS.DELETE_DOC_BY_ID.url,this.filterIdString(docId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.DELETE)
                .then(data => data.data);
    }
    getDocumentInfo(docId:string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_DOC_INFO_WITH_PARAS.url,this.filterIdString(docId));
        
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data);
    }
    uploadANewDocument(doc: FormData,folderId:string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.UPLOAD_DOCUMENT_TO_FOLDER.url,this.filterIdString(folderId));
        return this
                .invokeService(this.myURL+remUrl,doc,HttpVerbs.POST,'')
                .then(data => data.data[DocumentURLS.UPLOAD_DOCUMENT_TO_FOLDER.propname]);
    }
    getPageImage(docId: string, pageNo: string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_PAGE_IMAGE.url,this.filterIdString(docId), pageNo);
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_PAGE_IMAGE.propname]);
    }
    getAllVocabsOfADoc(docId: string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_VOCABS_OF_A_DOC.url,this.filterIdString(docId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_VOCABS_OF_A_DOC.propname]);
    }
    getVocabDetailsById(docId: string, vocabId: string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_VOCAB_DETAILS_BY_ID.url, this.filterIdString(docId),this.filterIdString(vocabId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_VOCAB_DETAILS_BY_ID.propname]);
    }
    getDetailsOfVocab(vocabTerm:any) : Promise<any>{
        var vocabUrl=this.myURL.replace('docexplorer/','wordDictionary');
        // var vocabUrl='http://34.225.174.109:9000/feed/api/v1/wordDictionary';
        return this
                   .invokeService(vocabUrl,vocabTerm,HttpVerbs.POST)
                   .then(data=> data.data[DocumentURLS.GET_DETAILS_OF_VOCAB.propname]);
    }
    
    getAllContextsOfADoc(docId: string) : Promise<DocumentVM>{
        var remUrl = stringformat(DocumentURLS.GET_All_CONTEXTS_OF_DOC.url,this.filterIdString(docId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_All_CONTEXTS_OF_DOC.propname]);
    }

    getAllContextsOfAParaOfADoc(docId:string, paraId:string) : Promise<ContextVM[]>{
        var remUrl = stringformat(DocumentURLS.GET_ALL_CONTEXTS_OF_A_PARA_OF_A_DOC.url,this.filterIdString(docId),this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_ALL_CONTEXTS_OF_A_PARA_OF_A_DOC.propname]);
    }
    getAllVocabsOfAParaOfADoc(docId:string, contextId:string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_ALL_VOCABS_OF_A_PARA_OF_A_DOC.url,this.filterIdString(docId),this.filterIdString(contextId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_ALL_VOCABS_OF_A_PARA_OF_A_DOC.propname]);
    }
    getAllParasOfAllDocsByContext(contextId:string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_ALL_PARAS_OF_ALL_DOCS_WITH_SPECIFIC_CONTEXT.url,this.filterIdString(contextId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_ALL_PARAS_OF_ALL_DOCS_WITH_SPECIFIC_CONTEXT.propname]);
    }
    //GET_ALL_CONTEXT_OF_A_PARA
    getAllContextsOfAPara(docId:string, paraId:string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_ALL_CONTEXT_OF_A_PARA.url,this.filterIdString(docId),this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_ALL_CONTEXT_OF_A_PARA.propname]);
    }
    getAllGlobalVectors(docId:string, paraId:string) : Promise<any>{
        var remUrl = stringformat(DocumentURLS.GET_ALL_GLOBAL_VECTORS.url,this.filterIdString(docId),this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.GET_ALL_GLOBAL_VECTORS.propname]);
    }

    getContributingContextWords(paraId:string) : Promise<ContextWord[]>{

        var remUrl = stringformat(DocumentURLS.CONTEXTWORDS.url,this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[DocumentURLS.CONTEXTWORDS.propname]);
    }

    
    //////////////////////////
    ////Folder operations/////
    getTopLevelFolders() : Promise<any>{
        return this
                .invokeService(this.myURL+FolderURLS.TOP_LEVEL_FOLDERS.url,'', HttpVerbs.GET)
                .then( data => data.data[FolderURLS.TOP_LEVEL_FOLDERS.propname]);
    }
    getAllFolders() : Promise<any>{
        //var remUrl = stringformat(FolderURLS.GETALLFOLDERS.url,'', HttpVerbs.GET)
        return this
                .invokeService(this.myURL+FolderURLS.GET_ALL_FOLDERS.url,'', HttpVerbs.GET)
                .then( data => data.data[FolderURLS.GET_ALL_FOLDERS.propname]);
    }
    getFolderContent(folderId) : Promise<FolderVM>{
        var remUrl = stringformat(FolderURLS.GET_FOLDER_CONTENT.url,this.filterIdString(folderId));
        return this
                .invokeService(this.myURL+remUrl,null, HttpVerbs.GET)
                .then( data => data.data[FolderURLS.GET_FOLDER_CONTENT.propname]);
                //.then( data => data.data);
    }
    createAFolder(folder:FolderVM) : Promise<FolderVM>{
        //var remUrl = FolderURLS.CREATEFOLDER.url;
        return this
                .invokeService(this.myURL+FolderURLS.CREATEFOLDER.url,folder, HttpVerbs.POST)
                .then( data => data.data[FolderURLS.CREATEFOLDER.propname]);
    }
    modifyFolder(folderId, aFolder: FolderVM) : Promise<FolderVM>{
        var remUrl = stringformat(FolderURLS.MODIFYFOLDER.url,this.filterIdString(folderId));
        return this
                .invokeService(this.myURL+remUrl,aFolder, HttpVerbs.PUT)
                .then( data => data.data[FolderURLS.MODIFYFOLDER.propname]);
    }
    deleteFolder(folderId) : Promise<any>{
        var remUrl = stringformat(FolderURLS.DELETEFOLDER.url,this.filterIdString(folderId));
        return this
                .invokeService(this.myURL+remUrl,null, HttpVerbs.DELETE)
                .then( data => data.data[FolderURLS.DELETEFOLDER.propname]);
    }
    //////////////////////////
}

var  DocumentURLS = {
    ALLDOCUMENTS : {
        url : "documents",
        propname : "documents" 
    },
    DOCBYID : {
        url : "documents/{0}",
        propname : "document" 
    },
    DOCS_BY_REGULATIONID : {
        url : "{0}/documents",
        propname : "documents" 
    },
    //Uploads a New Document to The Folder
    UPLOAD_DOCUMENT_TO_FOLDER:{
        url : "{0}/documents",
        propname : "documents" 
    },
    //delete a document
    DELETE_DOC_BY_ID : {
        url : "documents/{0}",
        propname : "document" 
    },
    //get page image
    GET_PAGE_IMAGE : {
        url: "documents/{0}/page/{1}",
        propname: "image"
    },

    //Get Document Info along with all the paras
    GET_DOC_INFO_WITH_PARAS : {
        url: "documents/{0}/all",
        propname: ""
    },
    //Get all glossary terms of a doc //<BASE_URL>/docexplorer/document/{docId}/vocabs	
    GET_VOCABS_OF_A_DOC : {
        url: "documents/{0}/vocabs",
        propname: "vocabularies"
    },
    GET_VOCAB_DETAILS_BY_ID : {
        url: "documents/{0}/vocabs/{1}/details",
        propname: "vocabularyDetails"
    },
    //get details of a vocab ///feed/api/v1/vocabs/{vocabId}/wordDictionary
    GET_DETAILS_OF_VOCAB :  {
        url: "{0}/wordDictionary",
        propname: "wordDictionary"
    },
    //Get all contexts of a doc
    GET_All_CONTEXTS_OF_DOC: {
        url: "documents/{0}/contexts",
        propname: "contexts"
    },
    //Get all the Contexts of a para of a document
    GET_ALL_CONTEXTS_OF_A_PARA_OF_A_DOC: {
        url: "documents/{0}/contexts/{1}",
        propname: "contexts"
    },
    //Get all the Vocabs of a para of a document
    GET_ALL_VOCABS_OF_A_PARA_OF_A_DOC: {
        url: "documents/{0}/vocabs/{1}",
        propname: "vocabs"
    },
    //Get all the paras of specified document having specified context
    GET_ALL_PARAS_OF_SPECIFIC_CONTEXT: {
        url:"documents/{0}/contexts/{1}",///////////////////////////////////////////////////////////////////////////////needs to check
        propname:"contexts"
    },
    //Get All the Paras of All Documents having that context
    GET_ALL_PARAS_OF_ALL_DOCS_WITH_SPECIFIC_CONTEXT: {
        url:"contexts/{0}",
        propname:"paras"//needs to check
    },
    //Get all contexts of a para
    GET_ALL_CONTEXT_OF_A_PARA:{
        url:"documents/{0}/contexts/{1}",
        propname:""//needs to check
    },
    //Get all global vector contexts, components, component-types and business segments of a para
    GET_ALL_GLOBAL_VECTORS:{
        url:"documents/{0}/globalvectors/{1}",
        propname:""//needs to check
    },
    //Advanced Search where we can have various filters like documents, contexts, vocabs etc.
    SEARCH:{
        url:"search",
        propname:""//needs to check
    },
    CONTEXTWORDS:{
        url:"paragraph/{0}/contextWords",
        propname:"contextWords"//needs to check
    }
}
var FolderURLS = {
    //Get All Top Level Folders configured in the system
    TOP_LEVEL_FOLDERS: {
        url: "folders",
        propname:"folders" //needs to check
    },
    //Get All Top Level Folders and sub configured in the system, no documents
    GET_ALL_FOLDERS: {
        url: "folders/all",
        propname: "" //needs to check
    },
    //Get All Documents Under a Folder and its subfolder
    GET_FOLDER_CONTENT: {
        url: "folders/{0}",
        propname: "folder" //needs to check
    },
    //to create a folder under a specific folder
    CREATEFOLDER: {
        url: "folders",
        propname: "folder" 
    },
    //Modifies a Folder
    MODIFYFOLDER:{
        url:"folders/{0}",
        propname:""//needs to check
    },
    //Deletes a Folder and All Documents Under It
    DELETEFOLDER:{
        url:"folders/{0}",
        propname:""//needs to check
    }
}

