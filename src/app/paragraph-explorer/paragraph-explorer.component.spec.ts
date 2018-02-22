import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ParagraphExplorerComponent } from './paragraph-explorer.component';

describe('ParagraphExplorerComponent', () => {
  let component: ParagraphExplorerComponent;
  let fixture: ComponentFixture<ParagraphExplorerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParagraphExplorerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParagraphExplorerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
