import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentParaViewComponent } from './document-para-view.component';

describe('DocumentParaViewComponent', () => {
  let component: DocumentParaViewComponent;
  let fixture: ComponentFixture<DocumentParaViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DocumentParaViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentParaViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
