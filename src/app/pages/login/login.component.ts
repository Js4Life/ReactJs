import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import 'rxjs/add/operator/map';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  
  data: any = null;
  constructor(private _http: Http) { 
    this.getMyBlog();
    this.getAccount();
  }


  ngOnInit() {
  }
  private getMyBlog() {
    return this._http.get('https://public-api.wordpress.com/rest/v1.1/sites/oliverveits.wordpress.com/posts/3078')
                .map((res: Response) => res.json())
                 .subscribe(data => {
                        this.data = data;
                        console.log(this.data);
                });
  }


  private getAccount() {
    return this._http.get('https://djvp2idgi0.execute-api.ap-south-1.amazonaws.com/dev/hierarchy/1')
                .map((res: Response) => res.json())
                 .subscribe(data => {
                        this.data = data;
                        console.log(this.data);
                });
  }
}
