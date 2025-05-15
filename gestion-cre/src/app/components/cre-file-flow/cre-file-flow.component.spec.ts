import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreFileFlowComponent } from './cre-file-flow.component';

describe('CreFileFlowComponent', () => {
  let component: CreFileFlowComponent;
  let fixture: ComponentFixture<CreFileFlowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreFileFlowComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreFileFlowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
