import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChecklistViewComponent } from './checklist-view.component';

describe('ChecklistViewComponent', () => {
  let component: ChecklistViewComponent;
  let fixture: ComponentFixture<ChecklistViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChecklistViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChecklistViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
