import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RecentAnalysisComponent } from './recent-analysis.component';

describe('RecentAnalysisComponent', () => {
  let component: RecentAnalysisComponent;
  let fixture: ComponentFixture<RecentAnalysisComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RecentAnalysisComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RecentAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
