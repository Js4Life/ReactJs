import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VocabDetailsComponent } from './vocab-details.component';

describe('VocabDetailsComponent', () => {
  let component: VocabDetailsComponent;
  let fixture: ComponentFixture<VocabDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VocabDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VocabDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
