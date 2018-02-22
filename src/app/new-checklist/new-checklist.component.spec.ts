import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewChecklistComponent } from './new-checklist.component';

describe('NewChecklistComponent', () => {
  let component: NewChecklistComponent;
  let fixture: ComponentFixture<NewChecklistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewChecklistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewChecklistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
