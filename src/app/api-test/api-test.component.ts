import { Component, OnInit } from '@angular/core';
import { DocumentSvc} from '../services/document/document.service';
import { AnalysisService} from '../services/analysis/analysis.service';
import { MasterDataSvc} from '../services/masterdata/masterdata.service';
import { AuthenticationService} from '../services/security/authentication.service';
import { DocumentUploadRquestVM} from '../models/documentuploadrequest.model';
import { FolderVM} from '../models/folder.model';
import {AnalysisRequestVM} from '../models/analysisrequest.model';
import {AnalysisResponseVM} from '../models/analysisresponse.model';

@Component({
  selector: 'app-api-test',
  templateUrl: './api-test.component.html',
  styleUrls: ['./api-test.component.css']
})
export class ApiTestComponent implements OnInit {
  public docId :string;
  lastDomain:any = {};
  
  constructor(public analysisSvc : AnalysisService, public docSvc : DocumentSvc, public masterDataSvc : MasterDataSvc, public authSvc : AuthenticationService ) { }

  ngOnInit() {
    //this.docId = "somedocid"
  }

  callAnApi(){
    //test here:
    //////////////////////////////
    ////doc Operations here////

    //this.getDocumentById();//OK
    //this.getDocumentInfo();//not yet working
    //this.getPageImage();//OK
    //this.getAllVocabsOfADoc();//not yet working
    //this.getAllContextsOfADoc();//not yet working
    //this.getAllGlobalVectors();//not yet working

    //////////////////////////////
    ////folder Operations here////

    // this.getFolderContent("11:51"); //OK
    // this.getFolderContent("15:102");
    // this.getFolderContent("13:205");
    //this.getTopLevelFolders();//OK
    //this.getAllFolders();// not yet working
    //this.createAFolder();//OK
    //this.modifyFolder();//not yet working
    //this.deleteFolder();//not yet working

    //////////////////////////////
    ////Analysis Operations here//
    this.createNewAnalysis();
  }


  // docInfoWithPara(){
  //   this.docSvc.getDocumentInfoWithAllPara("10:393").then(res=>{
  //     console.log(res);
  //   })
  // }
  getTopLevelFolders(){
    this.docSvc.getTopLevelFolders().then(res=>{
      console.log(res);
    })
  }
// top level folder id = "#11:51"
  getFolderContent(a){
    this.docSvc.getFolderContent(a).then(res=>{
      console.log(res);
    })
  }
  getAllFolders(){
    this.docSvc.getAllFolders().then(res=>{
      console.log(res);
    })
  }
  createAFolder(){
    var aFolder: FolderVM;
    aFolder={
      name:"aNewFolderForTest3",
      parentFolderName: "FASBI",
      parentFolderId: "#11:51",
      children :[],
      documentList:[]
    }
    this.docSvc.createAFolder(aFolder).then(res=>{
      console.log(res);
    })
  }
  modifyFolder(){
    //this folder inside FASB => {parentFolderId: "11:51", children: null, documentList: null, id: "#16:525", name: "aNewFolderForTest"}
    var aFolder: FolderVM;
    aFolder={
      id : "",
      name:"modifyfolder test",
      parentFolderName: "FASBI",
      parentFolderId: "#11:51",
      children :[],
      documentList:[]
    }
    this.docSvc.modifyFolder("#16:525",aFolder).then(res=>{
      console.log(res);
    })
  }
  deleteFolder(){
    this.docSvc.deleteFolder("#11:528").then(res=>{
      console.log(res);
    })
  }
  //folder id: "#11:51". it has parent folder id
  //A folder operation test

  //doc id : "10:393"
  getDocumentById(){
  this.docSvc.getDocumentById( "#10:393").then(res=>{
    console.log(res);
  })
}
//get Document Info With All Para
//doc id #9:469
getDocumentInfo(){
  this.docSvc.getDocumentInfo( "9:469").then(res=>{
    console.log(res);
  })
}
//upload A New Document
// uploadANewDocument(){
//   var demoDocfile1 : DocumentUploadRquestVM = {
//    regulationId : "123",
//     folderId : "11:51",
//    isExternal : true
//   };
//   this.docSvc.uploadANewDocument(demoDocfile1).then(res=>{
//     console.log(res);
//   })
// }

  updateUploadUrl(e){
      var file = e.dataTransfer ? e.dataTransfer.files[0] : e.target.files[0] || e.srcElement;
    //   var pattern = /image-*/;
      var reader = new FileReader();
      reader.onload = this._handleReaderLoaded.bind(this);
      reader.readAsDataURL(file);
      var uploadedDoc:FormData=new FormData();
      uploadedDoc.append('doc',file);

      var folderId="11:51";

      // this.uploadObj={'folderId':this.lastDomain.id.replace('#','') , 'file':this.uploadedDoc};
      this.docSvc.uploadANewDocument(uploadedDoc,folderId).then(data=>{
        var res=data;
        console.log(res);
      });
  }
    _handleReaderLoaded(e) {
      var reader = e.target;
  }
//getAllVocabsOfADoc
//doc id : "10:393"
getAllVocabsOfADoc(){
  this.docSvc.getAllVocabsOfADoc("10:393").then(res=>{
    console.log(res);
  })
}

//getPageImage
  getPageImage(){
  this.docSvc.getPageImage( "10:393","0").then(res=>{
    console.log(res);
  })
}
//getAllContextsOfADoc
  getAllContextsOfADoc(){
    this.docSvc.getAllContextsOfADoc( "10:393").then(res=>{
    console.log(res);
  })
  }

  getAllGlobalVectors(){
    this.docSvc.getAllGlobalVectors( "10:393", "#9:137").then(res=>{
    console.log(res);
  })
  }

  ////////////Analysis test////////////
  createNewAnalysis(){
    var anAnalysis = new AnalysisRequestVM();
    anAnalysis={
      contextRids: [],
      docRids: [],
      name: "An Analysis"
    }
    this.analysisSvc.createNewAnalysis( anAnalysis).then(res=>{
    console.log(res);
  })
  }

}
