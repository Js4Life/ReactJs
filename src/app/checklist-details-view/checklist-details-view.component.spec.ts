import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChecklistDetailsViewComponent } from './checklist-details-view.component';

describe('ChecklistDetailsViewComponent', () => {
  let component: ChecklistDetailsViewComponent;
  let fixture: ComponentFixture<ChecklistDetailsViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChecklistDetailsViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChecklistDetailsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
