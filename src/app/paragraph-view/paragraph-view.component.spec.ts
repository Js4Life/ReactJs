import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ParagraphViewComponent } from './paragraph-view.component';

describe('ParagraphViewComponent', () => {
  let component: ParagraphViewComponent;
  let fixture: ComponentFixture<ParagraphViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParagraphViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParagraphViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
