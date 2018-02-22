import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router} from '@angular/router';

import { AuthenticationService} from '../services/security/authentication.service';
import { UserSvc} from '../services/user/user.service';

import {Constants} from '../models/constants.model';
import {AppRouteConfig} from '../app.router-config';
import { ClientState } from '../providers/clientstate.provider';

import {UserVM} from "../models/user.model";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  errorMessage : String;
  loginForm:any={};
  registerForm: UserVM = new UserVM();
  options:any=[
    {id:0,name:'Dashboard'},{id:1,name:'Option 2'}
  ];
  roleList:any = [];
  user:any;
  roleName:string;
  roleListOpen:boolean = false;
  submitted = false;
  loading : boolean = false;
  showLogInDiv : boolean = false;
  showForgetPasswordDiv : boolean = false;
  showResetLinkDiv : boolean = false;
  showResetPasswordDiv : boolean = false;
  registerDiv:boolean = false;

  constructor(private authSvc:AuthenticationService,public router : Router,private arc:AppRouteConfig, private userSvc:UserSvc, private clientState:ClientState) { }


  onSubmit() {
      this.submitted = true;
  }

  login(){
    console.log(this.loginForm);
    this.errorMessage ='';
    if(this.loginForm.userId=="" || this.loginForm.password=="")
    {
      this.errorMessage = ' Username and password can not be null';
    }
    else {
      this.loading = true;    
      this.authSvc.login(this.loginForm).then(a => {
        this.loading = false;    
        console.log(a);
        if( a ){
            this.user= JSON.parse(localStorage.getItem(Constants.LOGGED_USER));
            this.clientState.selectedFilesForAnalysis=[];
            this.arc.gotoPage('dashboard');
        }
        else {
              this.errorMessage = 'Invalid Username or password';
        }
      });
    }
  }

  register(){
    console.log(this.registerForm);
    this.errorMessage ='';
    if(this.registerForm.userId=="" || this.registerForm.password=="")
    {
      this.errorMessage = ' Username and password can not be null';
    }
    else {
      this.loading = true;    
      // this.userModel.roles.roleId=this.registerForm.role;
      // this.userModel.roles.name=this.roleName;
      // this.userModel.password=this.registerForm.password;
      // this.userModel.name=this.registerForm.name;
      
      this.userSvc.register(this.registerForm).then(a => {
        this.loading = false;    
        if( a ){
            this.showingDivs();
        }
        else {
            this.errorMessage = 'Some error occured! Please register again';
        }
      });
    }
  }

  assignRole(idx,role){
    console.log(role);
    this.registerForm.role.roleId = role.roleId;
    this.registerForm.role.id = role.id;
    this.roleName = role.name;
    this.roleListOpen = false;
    // this.userModel.roles.id=role.id;
  }

//   resetUser() {
//     this.loginForm = new loginForm('','');
// }

  getAllRoles(){
    this.userSvc.getAllRoles().then(data=>{
      this.roleList = data;
      console.log(this.roleList);
    })
  }

  ngOnInit() {
    this.showingDivs();
    this.getAllRoles();
  }

  showingDivs(){
    this.errorMessage=null;
    this.registerDiv= false;
    this.showLogInDiv=true;
    this.showForgetPasswordDiv=false;
    this.showResetLinkDiv=false;
    this.showResetPasswordDiv=false;
  }
  forgetPassword(){
    this.registerDiv= false;
    this.showLogInDiv=false;
    this.showForgetPasswordDiv=true;
    this.showResetLinkDiv=false;
    this.showResetPasswordDiv=false;
  }
  backToLogIn(){
    this.showingDivs();    
  }
  sendResetPasswordLink(){
    this.registerDiv= false;
    this.showLogInDiv=false;
    this.showForgetPasswordDiv=false;
    this.showResetLinkDiv=true;
    this.showResetPasswordDiv=false;
  }
  returnToLogIn(){
    this.registerDiv= false;
    this.showLogInDiv=false;
    this.showForgetPasswordDiv=false;
    this.showResetLinkDiv=false;
    this.showResetPasswordDiv=true;
  }
  UpdatePassword(){
    this.showingDivs();
  }
  showRegister(){
    this.registerDiv= true;
    this.showLogInDiv=false;
    this.showForgetPasswordDiv=false;
    this.showResetLinkDiv=false;
    this.showResetPasswordDiv=false;
  }
}
